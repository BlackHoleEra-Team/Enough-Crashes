# Enough Crashes 💥

中文 | [English](https://github.com/BlackHoleEra-Team/Enough-Crashes/tree/main/README_en.md)

> **一个反向整活的"崩溃优化"模组 - 让崩溃变得有趣！**

## 📖 介绍

**Enough Crashes** 是 [Not Enough Crashes (NEC)](https://modrinth.com/mod/notenoughcrashes) 模组的反向恶搞版本。如果说 NEC 是为了防止崩溃，那么 Enough Crashes 就是为了 **"有计划的崩溃"**！

这个模组会在特定游戏操作中按预设概率触发真实的游戏崩溃，每次崩溃都会显示一条幽默的消息，让崩溃成为一种"特色体验"。

## 🎭 项目理念

> "既然崩溃不可避免，不如主动拥抱崩溃！"

## ✨ 特色功能

### 🎯 多重崩溃触发器
- **基础操作**：挖方块(1%)、放置方块(1.5%)
- **战斗系统**：攻击友好生物(5%)、中立生物(3%)、敌对生物(2%)
- **生活活动**：吃东西、合成、打开容器等30+种操作
- **静止检测**：1分钟(10%) → 5分钟(50%) → 10分钟(70%)，概率递增

### 😂 50+幽默崩溃消息
每次崩溃都会随机显示精心设计的崩溃提示，例如：
- "生活就像Minecraft，崩着崩着就习惯了。"
- "你的存档很好，只是它想休息一下。"
- "这不是Bug，是特性！"

### 🏮 文言文彩蛋
完整翻译的文言文版本(lzh.json)，体验古风崩溃韵味：
- "崩愈頻，戲愈精。" (越崩越精彩)
- "此非疵瑕，實乃特徵！" (这不是缺点，这是特色！)

### 🌍 多语言支持
- 简体中文 (现代白话)
- 文言文 (古风雅韵)
- English
- Deutsch
- Español
- More.....

### ⚙️ 灵活配置系统
```json
{
  "current_level": "sometimes",
  "sometimes": { "mineBlockChance": 1.0 },
  "rarely": { "mineBlockChance": 0.1 },
  "often": { "mineBlockChance": 5.0 }
}
```

可自定义配置，结构请符合json规范以及键值完整

**三个预设级别**：
- **rarely** (几乎不崩) - 极低概率，适合谨慎玩家
- **sometimes** (有时崩) - 默认级别，推荐体验
- **often** (经常崩) - 高概率，寻求刺激可选

## 📥 安装方法

### 前置要求
**Fabric API** (必装)

1. 下载最新版本的 Enough Crashes 模组
   - **推荐下载源**：[Modrinth](https://modrinth.com/mod/enoughcrashes)
2. 将 `.jar` 文件放入 Minecraft 的 `mods` 文件夹
3. **⚠️ 重要：备份你的存档！**
4. 启动游戏

## ⚠️ 重要警告

### 🚨 高风险提示
- **这是一个真实的崩溃模组**，不是模拟崩溃界面
- **会真的导致游戏关闭**，未保存进度可能丢失，但Mod会自动在崩溃前尝试帮你保存数据
- **要建议在重要的存档上使用**
- **务必定期备份存档**，作者不对数据丢失负责

### ✅ 安全使用建议
1. 配合自动备份模组使用
2. 仅在单人游戏或娱乐服务器使用
3. 从低概率级别开始体验
4. 崩溃后记得检查存档完整性

## 🔧 使用方法

### 游戏内命令
```
/enoughcrashes level <rarely|sometimes|often>   # 切换崩溃级别
/enoughcrashes debug <on|off>                    # 开启/关闭调试模式
/enoughcrashes reload                            # 重新加载配置文件
```

### 配置文件
配置文件位于：`<Minecraft版本文件夹>/config/enoughcrashes.json`

你可以：
1. 直接编辑配置文件调整概率
2. 创建自定义配置级别
3. 启用调试模式查看详细日志

## 🛠️ 开发与构建

### 环境要求
- JDK 21
- Gradle 9.2.1 (推荐，此项目开发版本)

### 构建步骤
```bash
# 克隆项目
git clone https://github.com/BlackHoleEra-Team/enough-crashes.git
cd enough-crashes

# 构建模组
./gradlew build

# 在开发环境运行
./gradlew runClient
```

## 🤝 贡献指南

欢迎贡献！你可以：

1. **报告问题**：在 [Issues](https://github.com/BlackHoleEra-Team/enough-crashes/issues) 提交Bug或建议
2. **改进翻译**：帮助完善多语言支持
3. **添加功能**：实现新的崩溃触发器或消息
4. **优化代码**：改进现有实现

### 贡献要求
- 代码风格与现有代码保持一致
- 提交前通过 `./gradlew build` 测试
- 使用有意义的提交信息
- 更新相关文档

## 📄 许可证

本项目采用 **GNU General Public License v3.0** 许可证。

## 🌟 致谢

### 灵感来源
- [Not Enough Crashes (NEC)](https://modrinth.com/mod/notenoughcrashes) - 反向灵感
- 所有Minecraft模组开发者社区

## 📞 联系与支持

### 问题反馈
- [GitHub Issues](https://github.com/yourusername/BlackHoleEra-Team/issues)
- 请附上：Minecraft版本、模组版本、崩溃日志、复现步骤

### Mod下载地址
- [Modrinth](https://modrinth.com/mod/enough-crashes) (英文)

## ⚡ 快速开始

如果你想要：
- **轻度体验**：使用 `rarely` 级别
- **标准体验**：使用默认的 `sometimes` 级别  
- **极限挑战**：使用 `often` 级别 + 长矛攻击

记住：**崩溃不是错误，是特色！** 😉

---

<div align="center">
  
**下载前请确保：你有足够耐心、备份了存档、并且真的想找点乐子！**

</div>
