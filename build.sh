#!/bin/bash

# Spring Boot应用程序构建和运行脚本

echo "开始构建Spring Boot应用程序..."

# 清理之前的构建
echo "清理项目..."
mvn clean

# 编译项目
echo "编译项目..."
mvn compile

if [ $? -eq 0 ]; then
    echo "编译成功!"
else
    echo "编译失败!"
    exit 1
fi

# 运行测试
echo "运行测试..."
mvn test

if [ $? -eq 0 ]; then
    echo "测试通过!"
else
    echo "测试失败!"
    exit 1
fi

# 打包应用程序
echo "打包应用程序..."
mvn package -DskipTests

if [ $? -eq 0 ]; then
    echo "打包成功!"
else
    echo "打包失败!"
    exit 1
fi

echo "构建完成! 可执行JAR文件位置: target/pengdeman-1.0.0.jar"
echo ""
echo "运行应用程序命令:"
echo "java -jar target/pengdeman-1.0.0.jar"
echo ""
echo "或者直接运行:"
echo "mvn spring-boot:run"