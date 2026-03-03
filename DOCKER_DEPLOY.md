# Docker 部署指南

## 前置准备

### 1. 阿里云服务器要求

- 操作系统：Ubuntu 20.04+ / CentOS 7+
- 配置：1核2G以上（推荐2核4G）
- 开放端口：8080（安全组中配置）

### 2. 服务器安装 Docker 和 Docker Compose

#### Ubuntu/Debian 系统：

```bash
# 更新包索引
sudo apt-get update

# 安装依赖
sudo apt-get install -y apt-transport-https ca-certificates curl software-properties-common

# 添加 Docker 官方 GPG 密钥
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -

# 添加 Docker 仓库
sudo add-apt-repository "deb [arch=amd64] https://download.docker.com/linux/ubuntu $(lsb_release -cs) stable"

# 安装 Docker
sudo apt-get update
sudo apt-get install -y docker-ce docker-ce-cli containerd.io

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 启动 Docker 并设置开机自启
sudo systemctl start docker
sudo systemctl enable docker

# 验证安装
docker --version
docker-compose --version
```

#### CentOS 系统：

```bash
# 安装 Docker
sudo yum install -y yum-utils
sudo yum-config-manager --add-repo https://download.docker.com/linux/centos/docker-ce.repo
sudo yum install -y docker-ce docker-ce-cli containerd.io

# 启动 Docker
sudo systemctl start docker
sudo systemctl enable docker

# 安装 Docker Compose
sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose
sudo chmod +x /usr/local/bin/docker-compose

# 验证安装
docker --version
docker-compose --version
```

---

## 部署方式

### 方式一：在服务器上构建（推荐）

#### 1. 上传代码到服务器

```bash
# 在本地执行，打包并上传
git archive --format=tar.gz --output=pengdeman.tar.gz master
scp pengdeman.tar.gz root@你的服务器IP:/root/
```

或者从 GitHub 克隆：
```bash
# 在服务器上执行
cd /root
git clone https://github.com/pengdeman/pengdeman.git
cd pengdeman
```

#### 2. 构建并启动

```bash
cd /root/pengdeman

# 构建镜像并启动容器
docker-compose up -d --build

# 查看日志
docker-compose logs -f

# 查看容器状态
docker-compose ps
```

---

### 方式二：本地构建镜像，推送到阿里云容器镜像服务

#### 1. 开通阿里云容器镜像服务

- 访问：https://cr.console.aliyun.com/
- 创建命名空间（如：pengdeman）
- 创建镜像仓库（如：pengdeman-app）

#### 2. 本地构建并推送

```bash
# 登录阿里云镜像仓库（替换为你的信息）
docker login --username=你的用户名 registry.cn-你的区域.aliyuncs.com

# 构建镜像
docker build -t pengdeman-app:latest .

# 打标签
docker tag pengdeman-app:latest registry.cn-你的区域.aliyuncs.com/pengdeman/pengdeman-app:latest

# 推送
docker push registry.cn-你的区域.aliyuncs.com/pengdeman/pengdeman-app:latest
```

#### 3. 服务器上拉取并运行

创建服务器上的 `docker-compose.yml`：

```yaml
version: '3.8'

services:
  pengdeman:
    image: registry.cn-你的区域.aliyuncs.com/pengdeman/pengdeman-app:latest
    container_name: pengdeman-app
    restart: always
    ports:
      - "8080:8080"
    environment:
      - JAVA_OPTS=-Xms256m -Xmx512m
      - TZ=Asia/Shanghai
```

启动：
```bash
docker-compose up -d
docker-compose logs -f
```

---

## 常用命令

```bash
# 启动服务
docker-compose up -d

# 停止服务
docker-compose down

# 重启服务
docker-compose restart

# 查看日志
docker-compose logs -f

# 查看最近100行日志
docker-compose logs --tail=100

# 进入容器
docker exec -it pengdeman-app sh

# 更新代码后重新构建
docker-compose up -d --build

# 查看容器状态
docker ps

# 查看资源使用
docker stats
```

---

## 配置 Nginx 反向代理（可选）

如果需要用域名访问，安装 Nginx 并配置：

```bash
# 安装 Nginx
sudo apt-get install nginx  # Ubuntu
# 或
sudo yum install nginx      # CentOS

# 创建配置文件
sudo vim /etc/nginx/conf.d/pengdeman.conf
```

配置内容：
```nginx
server {
    listen 80;
    server_name 你的域名.com;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}
```

重启 Nginx：
```bash
sudo nginx -t
sudo systemctl restart nginx
```

---

## 配置 HTTPS（可选）

使用 Let's Encrypt 免费证书：

```bash
# 安装 Certbot
sudo apt-get install certbot python3-certbot-nginx  # Ubuntu
# 或
sudo yum install certbot python3-certbot-nginx      # CentOS

# 获取证书
sudo certbot --nginx -d 你的域名.com

# 自动续期
sudo certbot renew --dry-run
```

---

## 安全建议

1. **修改默认配置**
   - 修改 application.properties 中的 JWT secret
   - 不要将敏感信息提交到 Git

2. **使用环境变量**
   - 创建 `.env` 文件存储敏感配置
   - 在 docker-compose.yml 中引用

3. **定期备份**
   - 备份数据库（如果使用外部数据库）
   - 备份日志文件

4. **监控和日志**
   - 定期检查应用日志
   - 配置日志轮转