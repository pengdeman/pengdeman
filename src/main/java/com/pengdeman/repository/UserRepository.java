package com.pengdeman.repository;

import com.pengdeman.model.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 用户数据访问接口
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {

    /**
     * 根据openid查找用户
     */
    Optional<UserEntity> findByOpenid(String openid);

    /**
     * 根据unionid查找用户
     */
    Optional<UserEntity> findByUnionid(String unionid);

    /**
     * 根据用户名查找（管理员登录）
     */
    Optional<UserEntity> findByUsername(String username);

    /**
     * 判断openid是否存在
     */
    boolean existsByOpenid(String openid);

    /**
     * 判断用户名是否存在
     */
    boolean existsByUsername(String username);
}