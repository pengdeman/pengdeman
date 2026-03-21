package com.pengdeman.controller;

import com.pengdeman.dto.JdParseRequest;
import com.pengdeman.dto.JdParseResponse;
import com.pengdeman.service.JdUnionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * 京东联盟API控制器
 * 处理京东链接解析转换
 */
@RestController
@RequestMapping("/api/jd")
public class JdController {

    private final JdUnionService jdUnionService;

    public JdController(JdUnionService jdUnionService) {
        this.jdUnionService = jdUnionService;
    }

    /**
     * 解析京东链接，生成CPS推广链接，预估返利
     */
    @PostMapping("/parse")
    public ResponseEntity<?> parseUrl(@RequestBody JdParseRequest request) {
        try {
            JdParseResponse response = jdUnionService.parseUrl(request.getOriginalUrl());
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IOException e) {
            return ResponseEntity.status(500).body("调用京东API失败: " + e.getMessage());
        }
    }
}
