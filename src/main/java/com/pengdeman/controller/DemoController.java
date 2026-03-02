package com.pengdeman.controller;

import com.pengdeman.dto.DemoRequest;
import com.pengdeman.dto.DemoResponse;
import com.pengdeman.service.DemoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * REST API控制器
 * 提供HTTP端点用于演示功能
 */
@RestController
@RequestMapping("/api/demo")
@CrossOrigin(origins = "*")
public class DemoController {

    private final DemoService demoService;

    public DemoController(DemoService demoService) {
        this.demoService = demoService;
    }

    /**
     * 欢迎端点 - 对应原Main.java的功能
     */
    @GetMapping("/welcome")
    public ResponseEntity<String> welcome() {
        String welcomeMessage = "Hello and welcome to Spring Boot Application!";
        return ResponseEntity.ok(welcomeMessage);
    }

    /**
     * 循环计数端点 - 对应原Main.java的循环功能
     */
    @GetMapping("/count/{number}")
    public ResponseEntity<List<Integer>> countNumbers(@PathVariable int number) {
        if (number <= 0 || number > 100) {
            return ResponseEntity.badRequest().build();
        }

        List<Integer> numbers = new java.util.ArrayList<>();
        for (int i = 1; i <= number; i++) {
            numbers.add(i);
        }

        return ResponseEntity.ok(numbers);
    }

    /**
     * 获取所有演示数据
     */
    @GetMapping
    public ResponseEntity<List<DemoResponse>> getAllDemos() {
        List<DemoResponse> demos = demoService.getAllDemos();
        return ResponseEntity.ok(demos);
    }

    /**
     * 根据ID获取演示数据
     */
    @GetMapping("/{id}")
    public ResponseEntity<DemoResponse> getDemoById(@PathVariable Long id) {
        DemoResponse demo = demoService.getDemoById(id);
        return ResponseEntity.ok(demo);
    }

    /**
     * 创建新的演示数据
     */
    @PostMapping
    public ResponseEntity<DemoResponse> createDemo(@Valid @RequestBody DemoRequest request) {
        DemoResponse createdDemo = demoService.createDemo(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDemo);
    }

    /**
     * 更新演示数据
     */
    @PutMapping("/{id}")
    public ResponseEntity<DemoResponse> updateDemo(
            @PathVariable Long id,
            @Valid @RequestBody DemoRequest request) {
        DemoResponse updatedDemo = demoService.updateDemo(id, request);
        return ResponseEntity.ok(updatedDemo);
    }

    /**
     * 删除演示数据
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDemo(@PathVariable Long id) {
        demoService.deleteDemo(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * 搜索演示数据
     */
    @GetMapping("/search")
    public ResponseEntity<List<DemoResponse>> searchDemos(@RequestParam String keyword) {
        List<DemoResponse> demos = demoService.searchDemos(keyword);
        return ResponseEntity.ok(demos);
    }
}