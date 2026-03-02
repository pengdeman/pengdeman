#!/bin/bash

# 快速启动Spring Boot应用程序脚本

echo "启动Spring Boot应用程序..."

# 检查是否已经编译
if [ ! -f "target/classes/com/pengdeman/Application.class" ]; then
    echo "正在编译项目..."
    mvn compile
fi

# 启动应用程序
echo "应用程序启动中，请访问 http://localhost:8080"
mvn spring-boot:run