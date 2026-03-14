package com.pengdeman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengdeman.config.WeChatConfig;
import com.pengdeman.dto.WxCode2SessionResponse;
import com.pengdeman.dto.WxLoginRequest;
import com.pengdeman.dto.WxLoginResponse;
import com.pengdeman.exception.WxLoginException;
import com.pengdeman.model.UserEntity;
import com.pengdeman.model.UserFinanceEntity;
import com.pengdeman.repository.UserRepository;
import com.pengdeman.repository.UserFinanceRepository;
import com.pengdeman.util.JwtUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * 微信登录服务
 */
@Service
public class WeChatService {

    private static final Logger log = LoggerFactory.getLogger(WeChatService.class);

    private final WeChatConfig weChatConfig;
    private final UserRepository userRepository;
    private final UserFinanceRepository userFinanceRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public WeChatService(WeChatConfig weChatConfig, UserRepository userRepository,
                         UserFinanceRepository userFinanceRepository,
                         JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.weChatConfig = weChatConfig;
        this.userRepository = userRepository;
        this.userFinanceRepository = userFinanceRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * 微信小程序登录
     *
     * @param request 登录请求
     * @return 登录响应
     */
    @Transactional
    public WxLoginResponse wxLogin(WxLoginRequest request) {
        // 1. 调用微信API获取openid和session_key
        WxCode2SessionResponse wxResponse = code2Session(request.getCode());

        // 2. 根据openid查找或创建用户
        boolean isNewUser = false;
        UserEntity user = userRepository.findByOpenid(wxResponse.getOpenid())
                .map(existingUser -> updateExistingUser(existingUser, request))
                .orElseGet(() -> {
                    UserEntity newUser = createNewUser(wxResponse, request);
                    createUserFinance(newUser.getId());
                    return newUser;
                });

        // 3. 判断是否为新用户 - 通过检查createdAt和lastLoginAt的时间差
        if (user.getCreatedAt() != null && user.getLastLoginAt() == null) {
            isNewUser = true;
        } else if (user.getCreatedAt() != null && user.getLastLoginAt() != null) {
            // 如果创建时间和最后登录时间相差不超过5秒，认为是新用户
            isNewUser = user.getCreatedAt().isAfter(user.getLastLoginAt().minusSeconds(5));
        }

        // 4. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        user = userRepository.save(user);

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());

        // 6. 获取用户资金信息
        UserFinanceEntity finance = userFinanceRepository.findByUserId(user.getId())
                .orElse(null);

        // 7. 构建响应
        WxLoginResponse.Builder builder = WxLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .openid(user.getOpenid())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .isNewUser(isNewUser);

        // 添加资金信息
        if (finance != null) {
            builder.balance(finance.getBalance())
                    .totalIncome(finance.getTotalIncome())
                    .withdrawableAmount(finance.getWithdrawableAmount())
                    .orderCount(finance.getOrderCount());
        }

        return builder.build();
    }

    /**
     * 调用微信code2session接口
     *
     * @param code 微信登录code
     * @return 微信响应
     */
    private WxCode2SessionResponse code2Session(String code) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weChatConfig.getCode2SessionUrl(),
                weChatConfig.getAppId(),
                weChatConfig.getAppSecret(),
                code);

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("调用微信API失败: {}", response);
                throw new WxLoginException("调用微信服务失败");
            }

            String body = response.body().string();
            log.debug("微信API响应: {}", body);

            WxCode2SessionResponse wxResponse = objectMapper.readValue(body, WxCode2SessionResponse.class);

            if (!wxResponse.isSuccess()) {
                log.error("微信API返回错误: errcode={}, errmsg={}", wxResponse.getErrCode(), wxResponse.getErrMsg());
                throw new WxLoginException(wxResponse.getErrCode(), wxResponse.getErrMsg());
            }

            return wxResponse;
        } catch (IOException e) {
            log.error("调用微信API异常", e);
            throw new WxLoginException("网络请求失败");
        }
    }

    /**
     * 创建新用户
     */
    private UserEntity createNewUser(WxCode2SessionResponse wxResponse, WxLoginRequest request) {
        UserEntity user = new UserEntity();
        user.setOpenid(wxResponse.getOpenid());
        user.setUnionid(wxResponse.getUnionid());
        user.setNickname(request.getNickname());
        user.setAvatar(request.getAvatar());
        user.setGender(request.getGender());
        user.setStatus(1);
        return user;
    }

    /**
     * 创建用户资金账户
     */
    private void createUserFinance(Long userId) {
        UserFinanceEntity finance = new UserFinanceEntity();
        finance.setUserId(userId);
        finance.setBalance(BigDecimal.ZERO);
        finance.setTotalIncome(BigDecimal.ZERO);
        finance.setWithdrawableAmount(BigDecimal.ZERO);
        finance.setOrderCount(0);
        finance.setPendingWithdrawal(BigDecimal.ZERO);
        finance.setTotalWithdrawn(BigDecimal.ZERO);
        userFinanceRepository.save(finance);
    }

    /**
     * 更新已有用户信息
     */
    private UserEntity updateExistingUser(UserEntity user, WxLoginRequest request) {
        // 如果有新的用户信息，则更新
        if (request.getNickname() != null) {
            user.setNickname(request.getNickname());
        }
        if (request.getAvatar() != null) {
            user.setAvatar(request.getAvatar());
        }
        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        return user;
    }

    /**
     * 根据用户ID获取用户信息
     */
    public UserEntity getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new WxLoginException("用户不存在"));
    }
}
