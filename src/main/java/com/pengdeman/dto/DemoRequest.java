package com.pengdeman.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 请求数据传输对象
 */
public class DemoRequest {

    @NotBlank(message = "名称不能为空")
    @Size(max = 100, message = "名称长度不能超过100个字符")
    private String name;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

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
}