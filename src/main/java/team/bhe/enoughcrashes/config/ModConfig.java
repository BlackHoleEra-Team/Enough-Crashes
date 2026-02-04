package team.bhe.enoughcrashes.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import team.bhe.enoughcrashes.EnoughCrashesCommon;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = FabricLoader.getInstance().getConfigDir().resolve("enoughcrashes.json").toFile();
    
    // 当前选中的配置级别
    private static String currentLevel = "sometimes";
    
    // 是否开启调试模式
    public static boolean debug = false;
    
    // 当前生效的配置数据
    private static CrashConfig currentConfig;
    
    // 所有可用的配置级别
    private static Map<String, CrashConfig> configLevels = new HashMap<>();

    public static void init() {
        if (!CONFIG_FILE.exists()) {
            createDefaultConfig();
        }
        loadConfig();
    }

    private static void createDefaultConfig() {
        // 1. "sometimes" (有时) - 当前默认值
        CrashConfig sometimes = new CrashConfig();
        sometimes.mineBlockChance = 1.0f; // 1%
        sometimes.placeBlockChance = 1.5f; // 1.5%
        sometimes.attackHostileChance = 2.0f; // 2%
        sometimes.attackNeutralChance = 3.0f; // 3%
        sometimes.attackFriendlyChance = 5.0f; // 5%
        sometimes.spearAttackChance = 10.0f;
        sometimes.eatFoodChance = 10.0f; // 10%
        sometimes.craftItemChance = 2.0f;
        sometimes.openContainerChance = 1.0f;
        sometimes.dropItemChance = 3.0f;
        sometimes.pickupItemChance = 2.0f;
        sometimes.switchHotbarChance = 1.0f;
        sometimes.tameEntityChance = 5.0f;
        sometimes.breedEntityChance = 4.0f;
        sometimes.beAttackedByEntityChance = 3.0f;
        sometimes.nameEntityChance = 6.0f;
        sometimes.teleportChance = 3.0f;
        sometimes.switchGameModeChance = 2.0f;
        sometimes.weatherChangeChance = 1.0f;
        sometimes.dayNightCycleChance = 1.0f;
        sometimes.enterDimensionChance = 4.0f;
        sometimes.getAdvancementChance = 10.0f;
        sometimes.enchantItemChance = 8.0f;
        sometimes.repairItemChance = 7.0f;
        sometimes.openChatChance = 2.0f;
        sometimes.idle1MinChance = 10.0f; // 10%
        sometimes.idle5MinChance = 50.0f; // 50%
        sometimes.idle10MinChance = 70.0f; // 70%

        // 2. "rarely" (几乎不) - 概率极低
        CrashConfig rarely = new CrashConfig();
        rarely.mineBlockChance = 0.1f;
        rarely.placeBlockChance = 0.1f;
        rarely.attackHostileChance = 0.5f;
        rarely.attackNeutralChance = 0.5f;
        rarely.attackFriendlyChance = 1.0f;
        rarely.spearAttackChance = 1.0f;
        rarely.eatFoodChance = 1.0f;
        rarely.craftItemChance = 0.2f;
        rarely.openContainerChance = 0.1f;
        rarely.dropItemChance = 0.3f;
        rarely.pickupItemChance = 0.2f;
        rarely.switchHotbarChance = 0.1f;
        rarely.tameEntityChance = 0.5f;
        rarely.breedEntityChance = 0.4f;
        rarely.beAttackedByEntityChance = 0.3f;
        rarely.nameEntityChance = 0.6f;
        rarely.teleportChance = 0.3f;
        rarely.switchGameModeChance = 0.2f;
        rarely.weatherChangeChance = 0.1f;
        rarely.dayNightCycleChance = 0.1f;
        rarely.enterDimensionChance = 0.4f;
        rarely.getAdvancementChance = 1.0f;
        rarely.enchantItemChance = 0.8f;
        rarely.repairItemChance = 0.7f;
        rarely.openChatChance = 0.2f;
        rarely.idle1MinChance = 1.0f;
        rarely.idle5MinChance = 5.0f;
        rarely.idle10MinChance = 10.0f;

        // 3. "often" (经常) - 概率很高
        CrashConfig often = new CrashConfig();
        often.mineBlockChance = 5.0f;
        often.placeBlockChance = 5.0f;
        often.attackHostileChance = 10.0f;
        often.attackNeutralChance = 15.0f;
        often.attackFriendlyChance = 20.0f;
        often.spearAttackChance = 30.0f;
        often.eatFoodChance = 30.0f;
        often.craftItemChance = 10.0f;
        often.openContainerChance = 5.0f;
        often.dropItemChance = 15.0f;
        often.pickupItemChance = 10.0f;
        often.switchHotbarChance = 5.0f;
        often.tameEntityChance = 25.0f;
        often.breedEntityChance = 20.0f;
        often.beAttackedByEntityChance = 15.0f;
        often.nameEntityChance = 30.0f;
        often.teleportChance = 15.0f;
        often.switchGameModeChance = 10.0f;
        often.weatherChangeChance = 5.0f;
        often.dayNightCycleChance = 5.0f;
        often.enterDimensionChance = 20.0f;
        often.getAdvancementChance = 50.0f;
        often.enchantItemChance = 40.0f;
        often.repairItemChance = 35.0f;
        often.openChatChance = 10.0f;
        often.idle1MinChance = 30.0f;
        often.idle5MinChance = 80.0f;
        often.idle10MinChance = 99.0f;

        JsonObject root = new JsonObject();
        root.addProperty("current_level", "sometimes");
        
        // 直接添加到根对象，保持扁平结构，与 loadConfig/saveConfig 保持一致
        root.add("sometimes", GSON.toJsonTree(sometimes));
        root.add("rarely", GSON.toJsonTree(rarely));
        root.add("often", GSON.toJsonTree(often));

        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(root, writer);
        } catch (IOException e) {
            EnoughCrashesCommon.LOGGER.error("无法创建配置文件", e);
        }
    }

    public static void loadConfig() {
        try (FileReader reader = new FileReader(CONFIG_FILE)) {
            JsonObject root = JsonParser.parseReader(reader).getAsJsonObject();
            
            if (root.has("current_level")) {
                currentLevel = root.get("current_level").getAsString();
            }
            
            if (root.has("debug")) {
                debug = root.get("debug").getAsBoolean();
            }
            
            configLevels.clear();
            
            // 遍历根对象的所有键
            for (Map.Entry<String, JsonElement> entry : root.entrySet()) {
                String key = entry.getKey();
                // 跳过 current_level 和 debug 字段
                if (key.equals("current_level") || key.equals("debug")) {
                    continue;
                }
                
                // 防止读取旧版本配置产生的 "levels" 嵌套对象
                if (key.equals("levels") && entry.getValue().isJsonObject()) {
                    // 如果发现旧的 levels 结构，尝试从中读取配置
                    JsonObject levelsObj = entry.getValue().getAsJsonObject();
                    for (Map.Entry<String, JsonElement> levelEntry : levelsObj.entrySet()) {
                        configLevels.put(levelEntry.getKey(), GSON.fromJson(levelEntry.getValue(), CrashConfig.class));
                    }
                    continue;
                }
                
                CrashConfig config = GSON.fromJson(entry.getValue(), CrashConfig.class);
                configLevels.put(key, config);
            }
            
            // 如果读取后发现有旧数据（即读取到了扁平化的配置），则保存一次以更新文件结构
            // 这里简单判断：如果有配置加载进来了，就保存一次，确保格式统一
            if (!configLevels.isEmpty()) {
                saveConfig();
            }
            
            // 应用当前配置
            updateCurrentConfig();
            
        } catch (Exception e) {
            EnoughCrashesCommon.LOGGER.error("加载配置文件失败", e);
            createDefaultConfig(); // 尝试重建默认配置
            loadConfig(); // 重新加载
        }
    }
    
    private static void updateCurrentConfig() {
        if (configLevels.containsKey(currentLevel)) {
            currentConfig = configLevels.get(currentLevel);
            EnoughCrashesCommon.LOGGER.info("已加载配置级别: " + currentLevel);
        } else {
            EnoughCrashesCommon.LOGGER.warn("找不到配置级别: " + currentLevel + "，使用默认级别 'sometimes'");
            currentLevel = "sometimes";
            if (configLevels.containsKey("sometimes")) {
                currentConfig = configLevels.get("sometimes");
            } else {
                // 极端情况：连sometimes都没了，创建一个默认的
                currentConfig = new CrashConfig();
                currentConfig.mineBlockChance = 1.0f;
                // ... 其他默认值
            }
        }
    }
    
    public static boolean setLevel(String levelName) {
        if (configLevels.containsKey(levelName)) {
            currentLevel = levelName;
            updateCurrentConfig();
            saveConfig();
            return true;
        }
        return false;
    }
    
    public static void setDebug(boolean enabled) {
        debug = enabled;
        saveConfig();
    }
    
    private static void saveConfig() {
        try {
            // 读取现有文件以保留其他可能的自定义级别
            JsonObject root;
            // 我们重新构建root，以确保格式整洁
            root = new JsonObject();
            
            // 更新当前级别
            root.addProperty("current_level", currentLevel);
            
            // 添加所有级别
            for (Map.Entry<String, CrashConfig> entry : configLevels.entrySet()) {
                root.add(entry.getKey(), GSON.toJsonTree(entry.getValue()));
            }
            
            try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
                GSON.toJson(root, writer);
            }
        } catch (IOException e) {
            EnoughCrashesCommon.LOGGER.error("保存配置文件失败", e);
        }
    }

    public static CrashConfig getConfig() {
        if (currentConfig == null) {
            loadConfig();
        }
        return currentConfig;
    }
    
    public static Set<String> getLevelNames() {
        return configLevels.keySet();
    }
    
    public static class CrashConfig {
        // 使用百分比 (0.0 - 100.0)
        public float mineBlockChance = 1.0f;
        public float placeBlockChance = 1.5f;
        public float attackHostileChance = 2.0f;
        public float attackNeutralChance = 3.0f;
        public float attackFriendlyChance = 5.0f;
        public float spearAttackChance = 10.0f; // 长矛攻击概率
        public float eatFoodChance = 10.0f; // 吃东西概率
        public float craftItemChance = 2.0f;
        public float openContainerChance = 1.0f;
        public float dropItemChance = 3.0f;
        public float pickupItemChance = 2.0f;
        public float switchHotbarChance = 1.0f;
        public float tameEntityChance = 5.0f;
        public float breedEntityChance = 4.0f;
        public float beAttackedByEntityChance = 3.0f;
        public float nameEntityChance = 6.0f;
        public float teleportChance = 3.0f;
        public float switchGameModeChance = 2.0f;
        public float weatherChangeChance = 1.0f;
        public float dayNightCycleChance = 1.0f;
        public float enterDimensionChance = 4.0f;
        public float getAdvancementChance = 10.0f;
        public float enchantItemChance = 8.0f;
        public float repairItemChance = 7.0f;
        public float openChatChance = 2.0f;
        public float idle1MinChance = 10.0f;
        public float idle5MinChance = 50.0f;
        public float idle10MinChance = 70.0f;
    }
}
