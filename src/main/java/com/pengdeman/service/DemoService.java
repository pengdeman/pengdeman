package com.pengdeman.service;

import com.pengdeman.dto.DemoRequest;
import com.pengdeman.dto.DemoResponse;
import com.pengdeman.exception.ResourceNotFoundException;
import com.pengdeman.model.DemoEntity;
import com.pengdeman.repository.DemoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 业务逻辑服务层
 */
@Service
@Transactional
public class DemoService {

    private final DemoRepository demoRepository;

    public DemoService(DemoRepository demoRepository) {
        this.demoRepository = demoRepository;
    }

    /**
     * 获取所有实体
     */
    @Transactional(readOnly = true)
    public List<DemoResponse> getAllDemos() {
        List<DemoEntity> entities = demoRepository.findAll();
        List<DemoResponse> responses = new ArrayList<>();
        for (DemoEntity entity : entities) {
            responses.add(DemoResponse.fromEntity(entity));
        }
        return responses;
    }

    /**
     * 根据ID获取实体
     */
    @Transactional(readOnly = true)
    public DemoResponse getDemoById(Long id) {
        DemoEntity entity = demoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + id + " 的记录"));
        return DemoResponse.fromEntity(entity);
    }

    /**
     * 创建新的实体
     */
    public DemoResponse createDemo(DemoRequest request) {
        DemoEntity entity = new DemoEntity();
        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        DemoEntity savedEntity = demoRepository.save(entity);
        return DemoResponse.fromEntity(savedEntity);
    }

    /**
     * 更新现有实体
     */
    public DemoResponse updateDemo(Long id, DemoRequest request) {
        DemoEntity entity = demoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("未找到ID为 " + id + " 的记录"));

        entity.setName(request.getName());
        entity.setDescription(request.getDescription());

        DemoEntity updatedEntity = demoRepository.save(entity);
        return DemoResponse.fromEntity(updatedEntity);
    }

    /**
     * 删除实体
     */
    public void deleteDemo(Long id) {
        if (!demoRepository.existsById(id)) {
            throw new ResourceNotFoundException("未找到ID为 " + id + " 的记录");
        }
        demoRepository.deleteById(id);
    }

    /**
     * 搜索实体
     */
    @Transactional(readOnly = true)
    public List<DemoResponse> searchDemos(String keyword) {
        List<DemoEntity> entities = demoRepository.findByNameContaining(keyword);
        List<DemoResponse> responses = new ArrayList<>();
        for (DemoEntity entity : entities) {
            responses.add(DemoResponse.fromEntity(entity));
        }
        return responses;
    }
}