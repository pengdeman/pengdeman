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
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * 微信小程序登录服务
 * 处理微信小程序code登录，调用微信code2session接口获取openid，
 * 查找或创建用户，初始化资金账户，返回JWT token
 */
@Service
public class WeChatService {

    private static final Logger log = LoggerFactory.getLogger(WeChatService.class);

    private final WeChatConfig weChatConfig;
    private final UserRepository userRepository;
    private final UserFinanceRepository userFinanceRepository;
    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper;

    /**
     * 构造函数注入依赖
     */
    public WeChatService(WeChatConfig weChatConfig, UserRepository userRepository,
                         UserFinanceRepository userFinanceRepository,
                         JwtUtil jwtUtil, ObjectMapper objectMapper) {
        this.weChatConfig = weChatConfig;
        this.userRepository = userRepository;
        this.userFinanceRepository = userFinanceRepository;
        this.jwtUtil = jwtUtil;
        this.objectMapper = objectMapper;
    }

    /**
     * OkHttp客户端配置，设置超时时间
     * 连接超时10秒，读取超时10秒，写入超时10秒
     */
    private final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .build();

    /**
     * 微信小程序登录主入口
     * 流程：
     * 1. 调用微信code2session接口，通过code获取openid和unionid
     * 2. 根据openid查找用户，不存在则创建新用户
     * 3. 为新用户创建资金账户
     * 4. 更新用户最后登录时间
     * 5. 生成JWT token
     * 6. 返回登录响应（包含token和用户信息、资金信息）
     *
     * @param request 微信登录请求，包含code、昵称、头像、性别
     * @return 登录响应，包含token、用户信息、资金信息
     * @throws WxLoginException 微信登录失败时抛出异常
     */
    @Transactional
    public WxLoginResponse wxLogin(WxLoginRequest request) {
        log.info("=== 开始微信登录流程，code: {}, nickname: {}, gender: {}",
                request.getCode(), request.getNickname(), request.getGender());

        // 1. 调用微信API获取openid和session_key
        WxCode2SessionResponse wxResponse = code2Session(request.getCode());

        // 2. 根据openid查找或创建用户
        UserEntity user = null;
        boolean isNewUser = false;

        // 先查找用户
        String openid = wxResponse.getOpenid();
        if (openid == null || openid.isEmpty()) {
            log.error("微信API返回openid为空，requestCode: {}", request.getCode());
            throw new WxLoginException("微信API返回openid为空，请检查appid和appsecret配置");
        }

        log.info("微信code2session成功，openid: {}, unionid: {}", openid, wxResponse.getUnionid());

        Optional<UserEntity> existingUserOpt = userRepository.findByOpenid(openid);
        if (existingUserOpt.isPresent()) {
            // 用户已存在，更新信息
            log.info("用户已存在，openid: {}, userId: {}, 更新用户信息", openid, existingUserOpt.get().getId());
            user = updateExistingUser(existingUserOpt.get(), request);
        } else {
            // 用户不存在，创建新用户
            log.info("新用户，openid: {}，创建用户账户", openid);
            UserEntity newUser = createNewUser(wxResponse, request);
            user = userRepository.save(newUser);
            log.info("用户创建成功，userId: {}", user.getId());

            // 创建用户资金记录
            createUserFinance(user.getId());
            log.info("新用户初始化完成，userId: {}, 资金账户已准备完成", user.getId());
            isNewUser = true;
        }

        // 3. 判断是否为新用户 - 通过检查createdAt和lastLoginAt的时间差
        // 如果用户从未登录过，或者创建后5秒内再次登录，认为是新用户
        if (user.getCreatedAt() != null && user.getLastLoginAt() == null) {
            isNewUser = true;
            log.debug("用户从未登录过，标记为新用户，userId: {}", user.getId());
        } else if (user.getCreatedAt() != null && user.getLastLoginAt() != null) {
            // 如果创建时间和最后登录时间相差不超过5秒，认为是新用户
            if (user.getLastLoginAt() != null && user.getCreatedAt().isAfter(user.getLastLoginAt().minusSeconds(5))) {
                isNewUser = true;
                log.debug("用户创建时间离上次登录不超过5秒，标记为新用户，userId: {}", user.getId());
            }
        }

        log.info("用户登录判断完成，userId: {}, openid: {}, isNewUser: {}", user.getId(), openid, isNewUser);

        // 4. 更新最后登录时间
        user.setLastLoginAt(LocalDateTime.now());
        user = userRepository.save(user);
        log.debug("更新用户最后登录时间完成，userId: {}", user.getId());

        // 5. 生成JWT token
        String token = jwtUtil.generateToken(user.getId(), user.getOpenid());
        log.debug("JWT token生成成功，userId: {}", user.getId());

        // 6. 获取用户资金信息，如果不存在则补建
        // 处理异常情况：用户存在但资金记录丢失
        UserFinanceEntity finance = userFinanceRepository.findByUserId(user.getId()).orElse(null);
        if (finance == null) {
            log.warn("用户资金记录不存在，补建资金账户，userId: {}", user.getId());
            createUserFinance(user.getId());
            finance = userFinanceRepository.findByUserId(user.getId()).orElse(null);
        }
        if (finance != null) {
            log.debug("获取用户资金信息成功，userId: {}, balance: {}, withdrawable: {}",
                    user.getId(), finance.getBalance(), finance.getWithdrawableAmount());
        } else {
            log.error("补建资金账户失败，userId: {}", user.getId());
        }

        // 7. 处理null昵称和头像，使用默认值
        String nickname = user.getNickname();
        if (nickname == null || nickname.isEmpty()) {
            nickname = "微信用户";
            log.debug("用户昵称为空，使用默认值，userId: {}", user.getId());
        }
        String avatar = user.getAvatar();
        if (avatar == null) {
            avatar = "";
            log.debug("用户头像为空，使用空字符串，userId: {}", user.getId());
        }

        // 8. 构建响应
        WxLoginResponse.Builder builder = WxLoginResponse.builder()
                .token(token)
                .userId(user.getId())
                .openid(user.getOpenid())
                .nickname(nickname)
                .avatar(avatar)
                .isNewUser(isNewUser);

        // 添加资金信息
        if (finance != null) {
            builder.balance(finance.getBalance())
                    .totalIncome(finance.getTotalIncome())
                    .withdrawableAmount(finance.getWithdrawableAmount())
                    .orderCount(finance.getOrderCount());
        }

        WxLoginResponse response = builder.build();
        log.info("=== 微信登录完成，userId: {}, openid: {}, isNewUser: {}",
                user.getId(), openid, isNewUser);

        return response;
    }

    /**
     * 调用微信code2session接口
     * 通过微信登录code换取openid和session_key
     *
     * @param code 微信小程序前端返回的登录code
     * @return 微信API返回的响应，包含openid、unionid
     * @throws WxLoginException 调用失败或微信返回错误时抛出异常
     * @see <a href="https://developers.weixin.qq.com/miniprogram/dev/OpenApiDoc/user-login/code2Session.html">微信文档</a>
     */
    private WxCode2SessionResponse code2Session(String code) {
        String url = String.format("%s?appid=%s&secret=%s&js_code=%s&grant_type=authorization_code",
                weChatConfig.getCode2SessionUrl(),
                weChatConfig.getAppId(),
                weChatConfig.getAppSecret(),
                code);
        log.debug("调用微信code2session接口，url: {}", url.replace(weChatConfig.getAppSecret(), "******"));

        Request request = new Request.Builder()
                .url(url)
                .get()
                .build();

        try (Response response = okHttpClient.newCall(request).execute()) {
            if (!response.isSuccessful() || response.body() == null) {
                log.error("调用微信API失败，response: {}", response);
                throw new WxLoginException("调用微信服务失败");
            }

            String body = response.body().string();
            log.debug("微信API响应，body: {}", body);

            WxCode2SessionResponse wxResponse = objectMapper.readValue(body, WxCode2SessionResponse.class);

            if (!wxResponse.isSuccess()) {
                log.error("微信API返回错误，errcode={}, errmsg={}", wxResponse.getErrCode(), wxResponse.getErrMsg());
                throw new WxLoginException(wxResponse.getErrCode(), wxResponse.getErrMsg());
            }

            log.debug("微信code2session调用成功，openid: {}, session_key长度: {}",
                    wxResponse.getOpenid(),
                    wxResponse.getSessionKey() != null ? wxResponse.getSessionKey().length() : 0);
            return wxResponse;
        } catch (IOException e) {
            log.error("调用微信API网络异常", e);
            throw new WxLoginException("网络请求失败");
        }
    }

    /**
     * 创建新用户实体
     * 根据微信返回的信息和前端传来的用户信息设置用户属性
     *
     * @param wxResponse 微信code2session响应
     * @param request   前端登录请求
     * @return 新建的用户实体（尚未保存到数据库）
     */
    private UserEntity createNewUser(WxCode2SessionResponse wxResponse, WxLoginRequest request) {
        log.debug("创建新用户实体，openid: {}, unionid: {}, requestNickname: {}, requestAvatar: {}",
                wxResponse.getOpenid(), wxResponse.getUnionid(),
                request.getNickname() != null ? request.getNickname() : "null",
                request.getAvatar() != null ? "not null" : "null");

        UserEntity user = new UserEntity();
        user.setOpenid(wxResponse.getOpenid());
        user.setUnionid(wxResponse.getUnionid());
        // 处理null，设置默认值
        if (request.getNickname() != null && !request.getNickname().isEmpty()) {
            user.setNickname(request.getNickname());
        } else {
            user.setNickname("微信用户");
            log.debug("新用户昵称空，使用默认值'微信用户'");
        }
        user.setAvatar(request.getAvatar()); // null 允许
        user.setGender(request.getGender()); // null 允许
        user.setStatus(1); // 正常状态

        log.debug("新用户实体创建完成，openid: {}", wxResponse.getOpenid());
        return user;
    }

    /**
     * 创建用户资金账户
     * 为新用户初始化资金信息，所有金额初始化为0
     * 如果资金账户已存在，则跳过创建（避免唯一键冲突）
     *
     * @param userId 用户ID
     */
    private void createUserFinance(Long userId) {
        log.debug("尝试创建用户资金账户，userId: {}", userId);

        // 先检查是否已经存在
        if (userFinanceRepository.findByUserId(userId).isPresent()) {
            log.warn("用户资金账户已存在，跳过创建，userId: {}", userId);
            return;
        }

        UserFinanceEntity finance = new UserFinanceEntity();
        finance.setUserId(userId);
        finance.setBalance(BigDecimal.ZERO);          // 账户总余额
        finance.setTotalIncome(BigDecimal.ZERO);     // 累计总收入
        finance.setWithdrawableAmount(BigDecimal.ZERO); // 可提现金额
        finance.setOrderCount(0);                     // 订单数量
        finance.setPendingWithdrawal(BigDecimal.ZERO); // 待审核提现金额
        finance.setTotalWithdrawn(BigDecimal.ZERO);  // 累计已提现

        userFinanceRepository.save(finance);
        log.info("用户资金账户创建完成，userId: {}", userId);
    }

    /**
     * 更新已有用户信息
     * 如果前端传来了新的昵称、头像、性别，则更新
     *
     * @param user    已有用户实体
     * @param request 登录请求
     * @return 更新后的用户实体（尚未保存到数据库）
     */
    private UserEntity updateExistingUser(UserEntity user, WxLoginRequest request) {
        log.debug("更新已有用户信息，userId: {}, 当前nickname: {}",
                user.getId(), user.getNickname());

        int updateCount = 0;
        // 如果有新的用户信息，则更新
        if (request.getNickname() != null && !request.getNickname().equals(user.getNickname())) {
            user.setNickname(request.getNickname());
            updateCount++;
            log.debug("更新用户昵称，userId: {}, newNickname: {}", user.getId(), request.getNickname());
        }
        if (request.getAvatar() != null && !request.getAvatar().equals(user.getAvatar())) {
            user.setAvatar(request.getAvatar());
            updateCount++;
            log.debug("更新用户头像，userId: {}", user.getId());
        }
        if (request.getGender() != null && !request.getGender().equals(user.getGender())) {
            user.setGender(request.getGender());
            updateCount++;
            log.debug("更新用户性别，userId: {}, newGender: {}", user.getId(), request.getGender());
        }

        if (updateCount > 0) {
            log.info("用户信息更新完成，userId: {}, 更新字段数: {}", user.getId(), updateCount);
        } else {
            log.debug("用户信息无变化，不需要更新，userId: {}", user.getId());
        }

        return user;
    }

    /**
     * 根据用户ID获取用户信息
     *
     * @param userId 用户ID
     * @return 用户实体
     * @throws WxLoginException 用户不存在时抛出异常
     */
    public UserEntity getUserById(Long userId) {
        log.debug("根据ID获取用户信息，userId: {}", userId);
        return userRepository.findById(userId)
                .orElseThrow(() -> {
                    log.error("用户不存在，userId: {}", userId);
                    return new WxLoginException("用户不存在");
                });
    }
}