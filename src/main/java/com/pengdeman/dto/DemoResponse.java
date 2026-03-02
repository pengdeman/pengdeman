package com.pengdeman.dto;

import com.pengdeman.model.DemoEntity;
import java.time.LocalDateTime;

/**
 * 响应数据传输对象
 */
public class DemoResponse {

    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;

    public DemoResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public static DemoResponse fromEntity(DemoEntity entity) {
        DemoResponse response = new DemoResponse();
        response.setId(entity.getId());
        response.setName(entity.getName());
        response.setDescription(entity.getDescription());
        response.setCreatedAt(entity.getCreatedAt());
        return response;
    }
}