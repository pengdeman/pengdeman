package com.pengdeman.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * 主页控制器
 * 处理根路径请求，重定向到首页
 */
@Controller
public class HomeController {

    /**
     * 访问根路径时重定向到首页 index.html
     * @return 重定向到 index.html
     */
    @GetMapping("/")
    public String index() {
        return "forward:/index.html";
    }
}