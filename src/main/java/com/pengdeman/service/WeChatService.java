package com.pengdeman.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pengdeman.config.WeChatConfig;
import com.pengdeman.dto.WxCode2SessionResponse;
import com.pengdeman.dto.WxLoginRequest;
import com.pengdeman.dto.WxLoginResponse;
import com.pengdeman.exception.WxLoginException;
import com.pengdeman.model.UserEntity;
import com.pengdeman.repository.UserRepository;
import com.pengdeman.util.JwtUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
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
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    public WeChatService(WeChatConfig weChatConfig, UserRepository userRepository,
                         JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.weChatConfig = weChatConfig;
        this.userRepository = userRepository;
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
        UserEntity user = userRepository.findByOpenid(wxResponse.getOpenid())
                .map(existingUser -> updateExistingUser(existingUser, request))
                .orElseGet(() -> createNewUser(wxResponse, request));

        // 3. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        user = userRepository.save(user);

        // 4. 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());

        // 5. 判断是否为新用户
        boolean isNewUser = user.getCreatedAt() != null
                && user.getLastLoginAt() != null
                && user.getCreatedAt().isAfter(user.getLastLoginAt().minusSeconds(5));

        // 6. 构建响应
        return WxLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .openid(user.getOpenid())
                .nickname(user.getNickname())
                .avatar(user.getAvatar())
                .isNewUser(isNewUser)
                .build();
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