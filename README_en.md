# Enough Crashes 💥

> **A reverse meme "crash optimization" mod - Making crashes fun!**


### Mod Download Links
- [Modrinth](https://modrinth.com/mod/enough-crashes) (English)

## 📖 Introduction

**Enough Crashes** is a reverse parody mod of [Not Enough Crashes (NEC)](https://modrinth.com/mod/notenoughcrashes). If NEC is meant to prevent crashes, then Enough Crashes is for **"planned crashes"**!

This mod triggers real game crashes at preset probabilities during specific game operations, each time displaying a humorous message, making crashes a "featured experience".

## 🎭 Project Philosophy

> "Since crashes are inevitable, why not embrace them actively!"

## ✨ Features

### 🎯 Multiple Crash Triggers
- **Basic operations**: Mining blocks (1%), Placing blocks (1.5%)
- **Combat system**: Attacking friendly mobs (5%), neutral mobs (3%), hostile mobs (2%)
- **Life activities**: Eating, crafting, opening containers, and 30+ other operations
- **Idle detection**: 1 minute (10%) → 5 minutes (50%) → 10 minutes (70%), increasing probability

### 😂 50+ Humorous Crash Messages
Each crash randomly displays a carefully designed crash prompt, for example:
- "Life is like Minecraft, you get used to crashing."
- "Your save is fine, it just wants a break."
- "This is not a Bug, it's a Feature!"

### 🏮 Classical Chinese Easter Egg
Complete translation in Classical Chinese (lzh.json), experience the charm of ancient-style crashes:
- "崩愈頻，戲愈精。" (The more it crashes, the more exciting it gets.)
- "此非疵瑕，實乃特徵！" (This is not a defect, it's a feature!)

### 🌍 Multi-language Support
- Simplified Chinese (Modern vernacular)
- Classical Chinese (Ancient elegance)
- English
- Deutsch
- Español
- More.....

### ⚙️ Flexible Configuration System

Just an example:
```json
{
  "current_level": "sometimes",
  "sometimes": { "mineBlockChance": 1.0 },
  "rarely": { "mineBlockChance": 0.1 },
  "often": { "mineBlockChance": 5.0 }
}
```

Custom configurations are allowed, but the structure must comply with JSON specifications and have complete key-value pairs.

**Three preset levels**:
- **rarely** (Almost never crashes) - Extremely low probability, suitable for cautious players
- **sometimes** (Sometimes crashes) - Default level, recommended for experience
- **often** (Often crashes) - High probability, for those seeking excitement

## 📥 Installation

### Prerequisites
**Fabric API** (Required)

1. Download the latest version of Enough Crashes mod
   - **Recommended download source**: [Modrinth](https://modrinth.com/mod/enoughcrashes)
2. Place the `.jar` file into the Minecraft `mods` folder
3. **⚠️ Important: Backup your saves!**
4. Launch the game

## ⚠️ Important Warnings

### 🚨 High-risk Tips
- **This is a real crash mod**, not a simulated crash screen
- **It will really cause the game to close**, unsaved progress may be lost, but the mod will try to save your data before crashing
- **It is recommended to use on important saves**
- **Be sure to back up saves regularly**, the author is not responsible for data loss

### ✅ Safe Usage Suggestions
1. Use with automatic backup mods
2. Only use in single-player or entertainment servers
3. Start with low probability levels
4. Check save integrity after crashes

## 🔧 Usage

### In-game Commands
```
/enoughcrashes level <rarely|sometimes|often>   # Switch crash level
/enoughcrashes debug <on|off>                    # Enable/disable debug mode
/enoughcrashes reload                            # Reload configuration file
```

### Configuration File
Configuration file location: `<Minecraft version folder>/config/enoughcrashes.json`

You can:
1. Directly edit the configuration file to adjust probabilities
2. Create custom configuration levels
3. Enable debug mode to view detailed logs

## 🛠️ Development & Building(But we recommend to go to Modrinth to Download this Mod)

### Environment Requirements
- JDK 21
- Gradle 9.2.1 (Recommended, development version of this project)

### Build Steps
```bash
# Clone the project
git clone https://github.com/BlackHoleEra-Team/enough-crashes.git
cd enough-crashes

# Build the mod
./gradlew build

# Run in development environment
./gradlew runClient
```

## 🤝 Contribution Guidelines

Contributions are welcome! You can:

1. **Report issues**: Submit bugs or suggestions in [Issues](https://github.com/BlackHoleEra-Team/enough-crashes/issues)
2. **Improve translations**: Help improve multi-language support
3. **Add features**: Implement new crash triggers or messages
4. **Optimize code**: Improve existing implementations

### Contribution Requirements
- Code style should be consistent with existing code
- Test with `./gradlew build` before submitting
- Use meaningful commit messages
- Update relevant documentation

## 📄 License

This project is licensed under the **GNU General Public License v3.0**.

## 🌟 Acknowledgements

### Inspiration Sources
- [Not Enough Crashes (NEC)](https://modrinth.com/mod/notenoughcrashes) - Reverse inspiration

## 📞 Contact & Support

### Issue Reporting
- [GitHub Issues](https://github.com/BlackHoleEra-Team/enough-crashes/issues)
- Please include: Minecraft version, mod version, crash logs, reproduction steps

## ⚡ Quick Start

If you want:
- **Light experience**: Use `rarely` level
- **Standard experience**: Use the default `sometimes` level
- **Extreme challenge**: Use `often` level + spear attacks

Remember: **Crashes are not bugs, they are features!** 😉

---

<div align="center">
  
**Before downloading, make sure you have enough patience, backed up your saves, and really want to have some fun!**

</div>
