# JAR包部署阿里云指南

## 一、本地打包

### 1. 打包命令
```bash
# 使用Maven打包
./mvnw clean package -DskipTests

# 或者如果本地安装了Maven
mvn clean package -DskipTests
```

### 2. 打包产物
打包成功后，会在 `target/` 目录下生成jar包：
```
target/pengdeman-1.0.0.jar
```

## 二、阿里云服务器部署

### 1. 环境要求
- 服务器操作系统：CentOS 7+/Ubuntu 18.04+
- 安装JDK 8或更高版本
- 开放安全组端口：8080（或者你配置的端口）

### 2. 上传jar包到服务器
```bash
# 使用scp上传
scp target/pengdeman-1.0.0.jar root@你的服务器IP:/opt/apps/
```

### 3. 启动应用

#### 方式一：直接启动（临时，关闭终端就停止）
```bash
java -jar pengdeman-1.0.0.jar
```

#### 方式二：后台运行（推荐）
```bash
# 创建日志目录
mkdir -p logs

# 后台启动，输出日志到文件
nohup java -jar pengdeman-1.0.0.jar > logs/start.log 2>&1 &
```

#### 方式三：使用systemd管理服务（生产环境推荐）
创建服务文件 `/etc/systemd/system/pengdeman.service`：
```ini
[Unit]
Description=pengdeman Spring Boot Application
After=network.target

[Service]
User=root
WorkingDirectory=/opt/apps
ExecStart=/usr/bin/java -jar /opt/apps/pengdeman-1.0.0.jar
Restart=always
RestartSec=10
Environment="SPRING_PROFILES_ACTIVE=prod"

[Install]
WantedBy=multi-user.target
```

启动服务：
```bash
# 重新加载systemd配置
systemctl daemon-reload

# 启动服务
systemctl start pengdeman

# 设置开机自启
systemctl enable pengdeman

# 查看服务状态
systemctl status pengdeman

# 查看日志
journalctl -u pengdeman -f
```

## 三、访问应用

### 1. 访问首页
在浏览器输入服务器公网IP:8080即可访问首页：
```
http://你的服务器公网IP:8080
```

### 2. 常用接口
- 健康检查：`http://你的IP:8080/actuator/health`
- API示例：`http://你的IP:8080/api/demo/welcome`
- H2控制台：`http://你的IP:8080/h2-console`

## 四、端口80映射（可选）
如果想直接通过IP访问（不需要加端口号），可以用以下两种方式：

### 方式一：修改application.properties配置
```properties
server.port=80
```

### 方式二：使用Nginx反向代理
安装Nginx后，配置如下：
```nginx
server {
    listen 80;
    server_name 你的域名或IP;

    location / {
        proxy_pass http://127.0.0.1:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
    }
}
```

## 五、注意事项
1. 生产环境建议关闭H2控制台：`spring.h2.console.enabled=false`
2. 生产环境建议修改数据库密码，使用更安全的数据库如MySQL
3. 生产环境建议配置HTTPS
4. 定期备份数据文件（位于./data/目录下）
5. 建议配置日志轮转，避免日志文件过大