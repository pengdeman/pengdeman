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
- Maven 3.6 或更高版本（可选，可以使用IDE内置Maven）

### 构建和运行

#### 方法1：使用构建脚本
```bash
# 构建项目
./build.sh

# 运行项目
./run.sh
```

#### 方法2：使用Maven命令
```bash
# 清理并编译
mvn clean compile

# 运行应用
mvn spring-boot:run

# 打包应用
mvn package
```

#### 方法3：运行打包后的JAR
```bash
# 构建JAR包
mvn package

# 运行JAR包
java -jar target/pengdeman-1.0.0.jar
```

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

## 后续优化建议

1. **安全性**：添加Spring Security认证授权
2. **缓存**：集成Redis缓存
3. **日志**：配置更完善的日志系统
4. **测试**：添加单元测试和集成测试
5. **文档**：集成Swagger API文档
6. **容器化**：添加Docker支持
7. **CI/CD**：配置自动化构建部署流程