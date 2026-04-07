# RSSHub Web Gateway & Aggregator

[![License: MIT](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
[![PRs Welcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/godyjk/RSSHub-Web-Gateway-Aggregator/pulls)

一个为自托管 RSSHub 实例设计的 Web 可视化前端与统一 API 网关。通过简洁的界面和标准化的接口，让使用 RSSHub 抓取多平台（知乎、微博、Twitter 等）数据变得轻而易举。

> **提示**：本项目是 RSSHub 的上层封装，数据抓取能力完全依赖于 [https://github.com/DIYgod/RSSHub](https://github.com/DIYgod/RSSHub)
> 在使用前，请确保您的环境按照下方教程进行配置

## ✨ 特性

- 🎯 **统一可视化操作**：提供 Web 界面，点击按钮即可获取数据，无需记忆复杂路由命令
- 🔧 **标准化 API 网关**：通过 Java 中间件提供稳定的 RESTful API，便于其他系统集成
- 🌐 **多平台支持**：封装知乎、微博、Twitter 等主流平台的 RSSHub 路由
- ⚙️ **集中配置管理**：在网关层统一管理请求、超时和错误处理
- 🚀 **一键部署**：提供启动脚本，自动运行 Java 网关、前端服务和 RSSHub 实例

## 🏗️ 系统架构

```
用户浏览器
    |
    ↓ (点击按钮)
[前端 Web 界面] (HTML/JS) - 端口:3000
    |
    ↓ (API 调用)
[Java 统一网关] (端口:8081) - 请求转发与协议转换
    |
    ↓ (调用路由/脚本)
[自托管 RSSHub] (端口:1200) - 实际数据抓取引擎
    |
    ↓ (返回 RSS/JSON 数据)
[Java 统一网关] - 结果处理与格式化
    |
    ↓
[前端 Web 界面] - 可视化展示
    |
    ↓
用户
```

## 🚀 快速开始

### 第一步：环境准备

#### 1.1 安装运行环境

```bash
# 1. 安装 Node.js (>= 16)
# 如果尚未安装 Chocolatey 包管理器，请先安装：
Set-ExecutionPolicy Bypass -Scope Process -Force; [System.Net.ServicePointManager]::SecurityProtocol = [System.Net.ServicePointManager]::SecurityProtocol -bor 3072; iex ((New-Object System.Net.WebClient).DownloadString('https://community.chocolatey.org/install.ps1'))
# 安装 Node.js LTS 版本
choco install nodejs-lts -y

# 2. 安装 Java JDK (>= 8)
choco install temurin17 -y

# 3. 安装依赖版本的 RSSHub 插件
npm install DIYgod/RSSHub#a7ca7c5 --save

# 4. 验证安装
node --version
java --version
```

#### 1.2 克隆项目

```bash
git clone https://github.com/your-username/rsshub-web-gateway.git
cd rsshub-web-gateway
```

### 第二步：配置环境变量

在项目根目录创建 `.env` 文件，配置以下环境变量：
> **提示**：请确保您安装了可运行.env项目的插件

#### 环境变量配置说明

| 变量名 | 说明 | 用途 | 获取方法 |
|--------|------|------|----------|
| `TWITTER_AUTH_TOKEN` | Twitter API 认证令牌 | Twitter 所有功能 | 1. 登录 Twitter 网页版<br>2. 按 F12 打开开发者工具<br>3. 在 Network 标签中查找任意请求<br>4. 复制 `authorization: Bearer AAAA...` 的值 |
| `ZHIHU_COOKIES` | 知乎 Cookie | 知乎用户信息获取 | 1. 登录知乎网页版<br>2. 按 F12 → Application → Cookies<br>3. 复制 `z_c0` 等关键 Cookie |
| `WEIBO_COOKIES` | 微博 Cookie | 微博用户信息获取 | 1. 登录微博网页版<br>2. 按 F12 → Application → Cookies<br>3. 复制 `SUB` 和 `SUBP` 值 |
| `BILIBILI_COOKIE_` | B站 Cookie | B站相关功能（如有） | 1. 登录 B站网页版<br>2. 按 F12 → Application → Cookies<br>3. 复制 `SESSDATA` 值 |
| `HTTP_PROXY` | HTTP 代理 | 需要代理访问的平台 | 代理服务器地址，如 `http://127.0.0.1:7890` |
| `HTTPS_PROXY` | HTTPS 代理 | 需要代理访问的平台 | 同 HTTP_PROXY，如 `http://127.0.0.1:7890` |
| `PUPPETEER_EXECUTABLE_PATH` | Chrome 路径 | 需要浏览器渲染的功能 | 指定 Chrome 可执行文件路径，如 `C:\Program Files\Google\Chrome\Application\chrome.exe` |

#### 快速获取配置的方法

**获取 Twitter 认证令牌：**

```bash
# 方法1：通过浏览器开发者工具
# 访问 https://twitter.com → F12 → Network → 刷新页面 → 查看任意请求的 Headers → 复制 authorization 值

# 方法2：如果已安装 curl
# curl -s "https://twitter.com" -H "User-Agent: Mozilla/5.0" | grep -o '"bearerToken":"[^"]*"' | head -1
```

**获取 Cookie 的通用方法：**
1. 使用浏览器无痕模式登录目标网站
2. 按 F12 打开开发者工具
3. 进入 Application → Storage → Cookies → 对应网站
4. 复制关键 Cookie 值（格式为 `key1=value1; key2=value2`）

#### 写入.env
将配置好的环境变量写入.env中，格式范例如下：
```bash
TWITTER_AUTH_TOKEN=33rgwegewiu283jg
ZHIHU_COOKIES=gi43wiehrir712hu
HTTP_PROXY="http://127.0.0.1:114514"
HTTPS_PROXY="http://127.0.0.1:114514"
```

### 第三步：运行项目

#### 3.1 启动所有服务（推荐）

```bash
# 一键启动
# 用控制台进入安装目录，输入
node start.js
```

此脚本会自动：
1. 启动 RSSHub 服务（端口 1200）
2. 启动 Java 网关服务（端口 8081）
3. 启动前端 Web 服务（端口 3000）
4. 自动打开浏览器访问 http://localhost:3000

#### 3.2 手动启动（可选）

```bash
# 1. 启动 RSSHub（需要先进入 RSSHub 目录）
cd rsshub
npm start

# 或使用 Docker
docker run -d --name rsshub -p 1200:1200 diygod/rsshub

# 2. 启动 Java 网关
cd application-controller
javac *.java
java interfaceServer

# 3. 启动前端
cd user-UI
node server.js
```

## 📱 使用说明

### Web 界面操作

1. 打开浏览器访问 http://localhost:3000
2. 在左侧边栏选择功能模块：
    - **知乎功能**：获取热榜、用户信息
    - **微博功能**：获取热榜、搜索关键词、用户信息
    - **Twitter 功能**：获取用户信息、搜索关键词、时间线
3. 点击对应按钮，按提示输入参数
4. 查看右侧输出区域的结果

### API 直接调用

Java 网关启动后，可以直接调用 API：

```bash
# 获取知乎热榜
curl -X POST http://localhost:8081/zhihu/hot

# 获取微博用户动态
curl -X POST http://localhost:8081/weibo/user \
  -H "Content-Type: application/json" \
  -d '{"userId": "1195230310"}'

# 搜索 Twitter 关键词
curl -X POST http://localhost:8081/twitter/keyword \
  -H "Content-Type: application/json" \
  -d '{"keyword": "RSSHub"}'
```

## 🐛 常见问题

### Q1: Twitter 功能无法使用

**A:** 确保正确配置了 `TWITTER_AUTH_TOKEN`：
- 检查令牌是否有效（可能已过期）
- 确认网络可以访问 Twitter
- 如果需要代理，确保配置了 `HTTP_PROXY` 和 `HTTPS_PROXY`

### Q2: 知乎/微博返回空数据

**A:** Cookie 可能已过期：
- 重新登录获取最新的 Cookie
- 使用无痕模式避免缓存干扰
- 检查 Cookie 格式是否正确

### Q3: 启动脚本报错

**A:** 检查环境：

```bash
# 验证 Java
java -version

# 验证 Node.js
node --version

# 验证端口占用
netstat -ano | findstr :1200
netstat -ano | findstr :8081
netstat -ano | findstr :3000
```

### Q4: 如何更新 RSSHub

**A:** RSSHub 作为独立服务，可以单独更新：

```bash
cd rsshub
git pull
npm install

# 或如果使用 Docker
docker pull diygod/rsshub:latest
docker-compose down
docker-compose up -d
```

## 🔧 自定义配置

### 修改端口

```java
// 修改 Java 网关端口 (interfaceServer.java)
HttpServer.create(new InetSocketAddress(8081), 0); // 改为其他端口
```

```javascript
// 修改前端端口 (user-UI/server.js)
const port = 3000; // 改为其他端口
```

```bash
# 修改 RSSHub 端口 (通过环境变量)
RSSHUB_PORT=1200
```

### 添加新平台

1. 在 RSSHub 中配置对应路由
2. 在 `router.java` 中添加相应方法
3. 在 `interfaceServer.java` 中注册路由
4. 在前端 `index.html` 中添加按钮

## 📁 项目结构

```
rsshub-web-gateway/
├── .env                    # 环境变量配置文件
├── .env.example           # 环境变量示例
├── start.js               # 一键启动脚本
├── application-controller/ # Java 网关
│   ├── interfaceServer.java
│   └── router.java
├── user-UI/               # Web 前端
│   ├── index.html
│   ├── server.js
│   └── package.json
└── rsshub/                # RSSHub 实例（需单独部署）
    ├── lib/
    ├── routes/
    └── package.json
```

## 🤝 贡献指南

1. Fork 本项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 📄 许可证

本项目基于 LICENSE 开源。

## 🙏 致谢

- 感谢 [https://github.com/DIYgod/RSSHub](https://github.com/DIYgod/RSSHub) 项目提供强大的数据抓取能力
- 感谢所有为本项目提供反馈和贡献的开发者

---

> **提示**: 首次使用请务必仔细配置 `.env` 文件，特别是 Twitter 和 Cookie 相关配置，这是功能正常运行的关键。如有问题，请参考 [https://docs.rsshub.app/](https://docs.rsshub.app/) 或提交 Issue。