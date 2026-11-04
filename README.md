# Effect of Venerable Knowledge (Minecraft 26.3)

[![Minecraft 26.3](https://img.shields.io/badge/Minecraft-26.3-brightgreen.svg)](https://minecraft.net/)
[![Fabric](https://img.shields.io/badge/Loader-Fabric-lightgrey.svg)](https://fabricmc.net/)
[![NeoForge](https://img.shields.io/badge/Loader-NeoForge-orange.svg)](https://neoforged.net/)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

A high-performance, elegant, and modular **Multi-Loader (Fabric & NeoForge)** Minecraft mod that introduces the **Effect of Venerable Knowledge** and an extensive tier-based potion progression system that multiplies mob experience drops (2x to 16x) without altering vanilla loot drops.

---

## ⚡ Core Mechanics & Formula

When an entity with the **Venerable Knowledge** effect active defeats a mob, dropped experience points are multiplied according to the exponential formula:

$$\text{Multiplier} = 2^{\min(\text{amplifier}, 10) + 1}$$

| Tier | Potion Level | In-Game Amplifier | Experience Multiplier | Standard Duration | Extended Duration |
| :--- | :--- | :--- | :--- | :--- | :--- |
| **Tier I** | Level I | `0` | **2x** Experience | 2:00 (2,400 ticks) | 5:00 (6,000 ticks) |
| **Tier II** | Level II | `1` | **4x** Experience | 2:00 (2,400 ticks) | 5:00 (6,000 ticks) |
| **Tier III** | Level III | `2` | **8x** Experience | 2:00 (2,400 ticks) | 5:00 (6,000 ticks) |
| **Tier IV** | Level IV | `3` | **16x** Experience | 2:00 (2,400 ticks) | 5:00 (6,000 ticks) |

> [!NOTE]
> **Pure Experience Multiplication**: Entity loot tables, equipment drops, and standard drops remain completely unmodified. Only the experience points (XP) dropped upon defeat are multiplied.

---

## 🧪 Potion Progression & Brewing Tree

The mod features a tiered brewing progression with **49 data-driven recipes**:

1. **Tier I (2x XP)**:
   - `Awkward Potion` + `Bottle o' Enchanting` $\rightarrow$ **Potion of Venerable Knowledge I** (2:00)
2. **Tier II (4x XP)**:
   - `Potion of Venerable Knowledge I` + `Lapis Lazuli Block` $\rightarrow$ **Potion of Venerable Knowledge II** (2:00)
3. **Tier III (8x XP)**:
   - `Potion of Venerable Knowledge II` + `Enchanted Book (Fortune ≥ II)` $\rightarrow$ **Potion of Venerable Knowledge III** (2:00)
4. **Tier IV (16x XP)**:
   - `Potion of Venerable Knowledge III` + `Enchanted Book (Fortune ≥ III & Mending)` $\rightarrow$ **Potion of Venerable Knowledge IV** (2:00)

### Modifiers
- **Redstone Dust**: Extends duration from standard **2:00** to **5:00** (works across all 4 tiers).
- **Gunpowder**: Converts any potion bottle into a **Splash Potion**.
- **Dragon's Breath**: Converts any splash potion into a **Lingering Potion**.
- **Tipped Arrows**: Craftable using 8 Arrows surrounding any lingering potion tier.

---

## 💍 Integration & Cross-Mod Compatibility

### 1. Potion Rings
Pre-configured configuration for Potion Rings Reforged:
- Color code: `#9699539` (`0x9400D3` - Dark Violet)
- Entry in `config/potionring.txt`:
  ```text
  venerable_knowledge:venerable_knowledge#9699539
  ```

### 2. Apotheosis & Zenith Compatibility
Automatic deduplication and normalization: active foreign effects (`apotheosis:ancient_knowledge`, `apothic_attributes:ancient_knowledge`, `zenith:ancient_knowledge`) are smoothly mapped and migrated to `venerable_knowledge:venerable_knowledge`.

---

## 🌐 Localization

Full in-game translations provided:
- **English (`en_us`)**: Venerable Knowledge
- **Turkish (`tr_tr`)**: Ulu Bilgelik

Includes roman numeral extensions (`III`, `IV`) for extended potion potency display.

---

## 📁 Project Architecture

The project utilizes a modern Gradle composite multi-loader architecture:
```
VenerableKnowledge/
├── common/        # Shared core logic, mixins, effects, recipes, and assets
│   ├── src/main/java/com/venerableknowledge/
│   ├── src/main/resources/data/venerable_knowledge/recipe/brewing/
│   └── src/test/java/com/venerableknowledge/VenerableKnowledgeTest.java
├── fabric/        # Fabric loader specific entrypoint and mixin bindings
├── neoforge/      # NeoForge loader specific entrypoint and event handlers
└── config/        # Default configuration files
```

---

## 🔨 Building & Testing

### Requirements
- **Java 25+** (configured via Gradle Toolchain)
- **Gradle 9.7+** (included Gradle Wrapper `gradlew`)

### Running Unit Tests
```bash
./gradlew test
```

### Building Mod JARs
```bash
./gradlew build
```

Built artifacts will be generated in:
- Fabric: `fabric/build/libs/venerable_knowledge-fabric-1.0.0.jar`
- NeoForge: `neoforge/build/libs/venerable_knowledge-neoforge-1.0.0.jar`

---

## 📄 License
This mod is released under the open-source [MIT License](LICENSE).
