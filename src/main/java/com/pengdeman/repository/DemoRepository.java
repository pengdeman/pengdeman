package com.pengdeman.repository;

import com.pengdeman.model.DemoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

/**
 * 数据访问层接口
 * 继承JpaRepository获得基本的CRUD操作
 */
@Repository
public interface DemoRepository extends JpaRepository<DemoEntity, Long> {
    
    /**
     * 根据名称查找实体
     */
    List<DemoEntity> findByNameContaining(String name);
    
    /**
     * 根据描述查找实体
     */
    List<DemoEntity> findByDescriptionContaining(String description);
}