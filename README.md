# Pengdeman Spring Boot 项目

这是一个基于现代Spring Boot框架重构的Java后端应用程序。

## 项目特性

- **Spring Boot 2.7.17** - 兼容Java 8的稳定版本
- **Java 8** - 当前系统环境版本
- **Maven** - 项目构建管理工具
- **Spring Data JPA** - 数据持久化层
- **H2内存数据库** - 开发测试数据库
- **RESTful API** - 标准REST接口设计
- **全局异常处理** - 统一的错误响应格式
- **参数验证** - 使用Bean Validation进行输入校验
- **Actuator监控** - 应用健康检查和监控
- **Lombok** - 减少样板代码

## 项目结构

```
src/main/java/com/pengdeman/
├── Application.java              # Spring Boot主应用类
├── controller/                   # 控制器层
│   └── DemoController.java      # REST API控制器
├── service/                      # 服务层
│   └── DemoService.java         # 业务逻辑服务
├── repository/                   # 数据访问层
│   └── DemoRepository.java      # JPA Repository接口
├── model/                        # 实体模型
│   └── DemoEntity.java          # JPA实体类
├── dto/                          # 数据传输对象
│   ├── DemoRequest.java         # 请求DTO
│   └── DemoResponse.java        # 响应DTO
├── config/                       # 配置类
│   └── WebConfig.java           # Web配置
└── exception/                    # 异常处理
    ├── ResourceNotFoundException.java  # 自定义异常
    └── GlobalExceptionHandler.java     # 全局异常处理器
```

## API端点

### 基础功能端点
- `GET /api/demo/welcome` - 欢迎信息（对应原Main.java的欢迎功能）
- `GET /api/demo/count/{number}` - 数字计数（对应原Main.java的循环功能）

### CRUD操作端点
- `GET /api/demo` - 获取所有演示数据
- `GET /api/demo/{id}` - 根据ID获取演示数据
- `POST /api/demo` - 创建新的演示数据
- `PUT /api/demo/{id}` - 更新演示数据
- `DELETE /api/demo/{id}` - 删除演示数据
- `GET /api/demo/search?keyword={keyword}` - 搜索演示数据

### 监控端点
- `GET /actuator/health` - 应用健康状态
- `GET /actuator/info` - 应用信息
- `GET /actuator/metrics` - 应用指标

### 开发工具
- `GET /h2-console` - H2数据库控制台（开发环境）

## 环境准备

由于当前系统使用Java 8，项目已调整为兼容Java 8的配置：
- 使用Spring Boot 2.7.17版本
- 移除了Jakarta EE依赖，使用传统的Java EE注解
- 调整了部分Java 8不支持的语法特性

如果需要升级到Java 17+，请：
1. 安装Java 17或更高版本
2. 将pom.xml中的Spring Boot版本升级到3.x
3. 恢复jakarta包的导入

### 环境要求
- Java 8 或更高版本
- **无需安装Maven** - 项目已包含Maven Wrapper

### 构建和运行

#### 方法1：使用Maven Wrapper（推荐）
```bash
# 清理并编译
./mvnw clean compile

# 运行应用
./mvnw spring-boot:run

# 打包应用（跳过测试）
./mvnw clean package -DskipTests
```

#### 方法2：使用构建脚本
```bash
# 构建项目
./build.sh

# 运行项目
./run.sh
```

#### 方法3：运行打包后的JAR
```bash
# 构建JAR包
./mvnw clean package -DskipTests

# 运行JAR包
java -jar target/pengdeman-1.0.0.jar
```

> **Windows用户**：使用 `mvnw.cmd` 代替 `./mvnw`

## 测试API

应用启动后，默认运行在 `http://localhost:8080`

### 使用curl测试

```bash
# 测试欢迎端点
curl http://localhost:8080/api/demo/welcome

# 测试计数功能
curl http://localhost:8080/api/demo/count/5

# 创建新记录
curl -X POST http://localhost:8080/api/demo \
  -H "Content-Type: application/json" \
  -d '{"name":"测试名称","description":"测试描述"}'

# 获取所有记录
curl http://localhost:8080/api/demo

# 根据ID获取记录
curl http://localhost:8080/api/demo/1
```

### 使用浏览器测试

访问以下URL进行测试：
- 主页：http://localhost:8080/api/demo/welcome
- 计数：http://localhost:8080/api/demo/count/10
- H2控制台：http://localhost:8080/h2-console
- 健康检查：http://localhost:8080/actuator/health

## 配置说明

主要配置文件：`src/main/resources/application.properties`

关键配置项：
- `server.port=8080` - 服务器端口
- `spring.datasource.*` - 数据库连接配置
- `spring.jpa.*` - JPA/Hibernate配置
- `logging.level.*` - 日志级别配置

## 开发说明

### 添加新功能
1. 在`model`包中创建实体类
2. 在`repository`包中创建Repository接口
3. 在`service`包中实现业务逻辑
4. 在`controller`包中创建REST端点
5. 添加相应的DTO类

### 数据库访问
- 使用H2内存数据库进行开发测试
- 数据库控制台：http://localhost:8080/h2-console
- JDBC URL: jdbc:h2:mem:testdb
- 用户名: sa
- 密码: (空)

## 与原项目的对比

| 特性 | 原项目 | 重构后项目 |
|------|--------|------------|
| 框架 | 纯Java | Spring Boot |
| 架构 | 单类应用 | 分层架构 |
| 数据存储 | 无 | H2数据库 |
| API | 无 | RESTful API |
| 部署 | 需要手动编译运行 | 支持JAR包部署 |
| 监控 | 无 | Actuator监控 |
| 异常处理 | 无 | 全局异常处理 |

## 部署到阿里云服务器

### 前置要求
- 阿里云ECS服务器（已安装Java 8或更高版本）
- 服务器已开放8080端口（或其他指定端口）

### 部署步骤

#### 1. 本地构建JAR包
```bash
# 清理并打包（使用Maven Wrapper，无需安装Maven）
./mvnw clean package -DskipTests

# 生成的JAR包位于: target/pengdeman-1.0.0.jar
```

#### 2. 上传JAR包到服务器
```bash
# 使用scp上传
scp target/pengdeman-1.0.0.jar root@your-server-ip:/opt/pengdeman/

# 或使用sftp工具上传
```

#### 3. 服务器上运行

**方式一：直接运行（前台）**
```bash
cd /opt/pengdeman
java -jar pengdeman-1.0.0.jar
```

**方式二：后台运行（推荐）**
```bash
cd /opt/pengdeman
nohup java -jar pengdeman-1.0.0.jar > app.log 2>&1 &
```

**方式三：使用启动脚本**
```bash
# 创建启动脚本
cat > /opt/pengdeman/start.sh << 'EOF'
#!/bin/bash
APP_NAME=pengdeman-1.0.0.jar
LOG_FILE=/opt/pengdeman/app.log

# 检查是否已运行
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "应用已在运行，PID: $PID"
    exit 1
fi

# 启动应用
nohup java -jar /opt/pengdeman/$APP_NAME > $LOG_FILE 2>&1 &
echo "应用启动成功"
EOF

chmod +x /opt/pengdeman/start.sh

# 创建停止脚本
cat > /opt/pengdeman/stop.sh << 'EOF'
#!/bin/bash
APP_NAME=pengdeman-1.0.0.jar

PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -z "$PID" ]; then
    echo "应用未运行"
    exit 1
fi

kill -15 $PID
echo "正在停止应用..."
sleep 5

# 检查是否停止成功
PID=$(ps -ef | grep $APP_NAME | grep -v grep | awk '{print $2}')
if [ -n "$PID" ]; then
    echo "强制停止应用"
    kill -9 $PID
fi
echo "应用已停止"
EOF

chmod +x /opt/pengdeman/stop.sh

# 使用脚本
./start.sh   # 启动
./stop.sh    # 停止
```

#### 4. 配置systemd服务（生产环境推荐）
```bash
# 创建服务文件
cat > /etc/systemd/system/pengdeman.service << 'EOF'
[Unit]
Description=Pengdeman Spring Boot Application
After=network.target

[Service]
Type=simple
User=root
WorkingDirectory=/opt/pengdeman
ExecStart=/usr/bin/java -jar /opt/pengdeman/pengdeman-1.0.0.jar
Restart=on-failure
RestartSec=10
StandardOutput=journal
StandardError=journal

[Install]
WantedBy=multi-user.target
EOF

# 启用并启动服务
systemctl daemon-reload
systemctl enable pengdeman
systemctl start pengdeman

# 查看状态
systemctl status pengdeman

# 查看日志
journalctl -u pengdeman -f
```

#### 5. 验证部署
```bash
# 检查端口是否监听
netstat -tlnp | grep 8080

# 本地测试
curl http://localhost:8080/actuator/health

# 外部访问（替换为你的服务器IP）
curl http://your-server-ip:8080/api/demo/welcome
```

### JVM参数优化
```bash
# 生产环境建议添加JVM参数
java -Xms512m -Xmx1024m -jar pengdeman-1.0.0.jar

# 或在启动脚本中配置
nohup java -Xms512m -Xmx1024m -XX:+UseG1GC -jar pengdeman-1.0.0.jar > app.log 2>&1 &
```

### 常用操作
```bash
# 查看应用日志
tail -f /opt/pengdeman/app.log

# 查看应用进程
ps -ef | grep pengdeman

# 实时监控资源使用
top -p $(pgrep -f pengdeman-1.0.0.jar)
```

## 后续优化建议

1. **安全性**：添加Spring Security认证授权
2. **缓存**：集成Redis缓存
3. **日志**：配置更完善的日志系统
4. **测试**：添加单元测试和集成测试
5. **文档**：集成Swagger API文档
6. **CI/CD**：配置自动化构建部署流程