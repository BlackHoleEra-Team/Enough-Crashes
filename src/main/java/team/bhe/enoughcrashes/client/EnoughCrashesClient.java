package team.bhe.enoughcrashes.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.RenderTickCounter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.config.ModConfig;

// 导入客户端专用的事件
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class EnoughCrashesClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("EnoughCrashes-Client");
    
    // 静止检测相关变量
    private Vec3d lastPlayerPos = Vec3d.ZERO;
    private float lastPitch = 0.0f;
    private float lastYaw = 0.0f;
    private int stationaryTicks = 0;
    private boolean isTracking = false;
    
    // 崩溃倒计时变量
    private static int crashCountdown = -1;
    private static String pendingCrashAction = "";
    private static String pendingCrashMessage = "";
    
    // 崩溃统计（客户端本地）
    private static int totalCrashesTriggered = 0;
    private static long lastCrashTime = 0;
    
    @Override
    public void onInitializeClient() {
        LOGGER.info("Enough Crashes 客户端模组已加载 - 准备好崩溃了吗？");
        
        // 注册客户端专用的事件监听器
        
        // 1. 挖方块时崩溃 (1%概率) - 客户端专用事件
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) -> {
            if (ModConfig.debug) {
                LOGGER.info("检测到挖方块操作");
            }
            float chance = ModConfig.getConfig().mineBlockChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                triggerCrash(net.minecraft.client.resource.language.I18n.translate("action.enoughcrashes.mine_block"));
            }
            return ActionResult.PASS;
        });
        
        // 2. 攻击实体时崩溃 - 客户端专用事件
        AttackEntityCallback.EVENT.register((player, world, hand, entity, hitResult) -> {
            if (ModConfig.debug) {
                LOGGER.info("检测到攻击实体操作: " + (entity != null ? getEntityTypeName(entity) : "null"));
            }
            if (entity != null) {
                // 长矛攻击已由 Mixin 处理，此处仅处理普通生物攻击
                // 如果需要双重保险，可以保留，但可能会触发两次
                // 为避免冲突，这里我们不再检测长矛

                float crashChance = 0.0f;
                ModConfig.CrashConfig config = ModConfig.getConfig();
                
                // 判断生物类型
                if (entity instanceof HostileEntity) {
                    crashChance = config.attackHostileChance / 100.0f; // 敌对生物
                } else if (entity instanceof Angerable) {
                    crashChance = config.attackNeutralChance / 100.0f; // 中立生物
                } else if (entity instanceof AnimalEntity || 
                          entity instanceof MerchantEntity ||
                          entity instanceof GolemEntity) {
                    crashChance = config.attackFriendlyChance / 100.0f; // 友好生物
                }
                
                if (crashChance > 0 && EnoughCrashesCommon.RANDOM.nextFloat() < crashChance) {
                    triggerCrash(net.minecraft.client.resource.language.I18n.translate("action.enoughcrashes.attack_entity", getEntityTypeName(entity)));
                }
            }
            return ActionResult.PASS;
        });
        
        // 3. 长矛/三叉戟右键使用检测
        // 已移至 ClientPlayerInteractionManagerMixin 中处理
        // UseItemCallback.EVENT.register(...) 
        
        // 4. 放置方块检测（需要使用Mixin或ClientTick检测）
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // 处理崩溃倒计时
            if (crashCountdown > 0) {
                crashCountdown--;
                if (crashCountdown == 0) {
                    // 时间到，触发真·崩溃
                    throw new EnoughCrashesException(pendingCrashMessage, pendingCrashAction, totalCrashesTriggered);
                }
            }

            if (client.player != null && client.world != null) {
                // 放置方块检测（需要自己实现）
                checkBlockPlacement(client);
                
                // 静止检测
                updateStationaryDetection(client);
                
                // 更新HUD显示（可选）
                updateHud(client);
                
                // 显示静止警告
                showStationaryWarning(client);
            }
        });
        
        // 4. 连接断开时重置状态
        ClientPlayConnectionEvents.DISCONNECT.register((handler, client) -> {
            resetState();
            LOGGER.info("断开连接，重置崩溃检测状态");
        });
        
        // 5. 可选：在HUD上显示警告信息
        // 此事件已过时，但在新版 Fabric API 中仍可用（通常会有替代方案如 HudLayerRegistrationCallback）
        // 这里为了保持简单，我们继续使用它并忽略警告
        @SuppressWarnings("deprecation")
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback callback = this::renderWarningHud;
        net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback.EVENT.register(callback);
        
        LOGGER.info("客户端崩溃触发器已就位 - 祝你游戏愉快（大概）");
    }
    
    /**
     * 放置方块检测（简化实现）
     * 注意：更精确的实现需要Mixin或更复杂的事件监听
     */
    private void checkBlockPlacement(MinecraftClient client) {
        // 这里是一个简化的检测，可能需要根据具体需求调整
        ClientPlayerEntity player = client.player;
        if (player != null) {
            // 检查玩家是否正在使用方块（放置方块）
            // 注意：这只是一个示例，实际检测可能需要更复杂的逻辑
            if (player.isUsingItem() && 
                EnoughCrashesCommon.RANDOM.nextFloat() < 0.015f) {
                if (ModConfig.debug) {
                    LOGGER.info("检测到放置/使用物品行为");
                }
                // 简单的概率检查，但不精确
                float chance = ModConfig.getConfig().placeBlockChance / 100.0f;
                // 这里原本的逻辑有点奇怪，外层已经有一个概率检查了，这里我们简化一下
                // 假设外层检查是性能优化，这里我们直接使用配置的概率
                if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                    triggerCrash(I18n.translate("action.enoughcrashes.place_block"));
                }
            }
        }
    }
    
    /**
     * 静止检测逻辑
     */
    private void updateStationaryDetection(MinecraftClient client) {
        ClientPlayerEntity player = client.player;
        if (player == null) return;
        
        if (!isTracking) {
            // 第一次初始化
            lastPlayerPos = new Vec3d(player.getX(), player.getY(), player.getZ());
            lastPitch = player.getPitch();
            lastYaw = player.getYaw();
            isTracking = true;
            stationaryTicks = 0;
            return;
        }
        
        Vec3d currentPos = new Vec3d(player.getX(), player.getY(), player.getZ());
        float currentPitch = player.getPitch();
        float currentYaw = player.getYaw();
        
        // 计算移动距离和视角变化
        double moveDistance = currentPos.distanceTo(lastPlayerPos);
        double pitchChange = Math.abs(currentPitch - lastPitch);
        double yawChange = Math.abs(currentYaw - lastYaw);
        
        // 判断是否静止（移动距离小于0.01格，视角变化小于0.5度）
        boolean isMoving = moveDistance > 0.01 || pitchChange > 0.5 || yawChange > 0.5;
        
        if (isMoving) {
            // 玩家在移动，重置计时器
            stationaryTicks = 0;
            lastPlayerPos = currentPos;
            lastPitch = currentPitch;
            lastYaw = currentYaw;
        } else {
            // 玩家静止，增加计时器
            stationaryTicks++;
            
            // 检查是否到达崩溃检查点
            checkStationaryCrash();
            
            // 每分钟记录一次日志（可选，调试用）
            if (ModConfig.debug && stationaryTicks % 1200 == 0) {
                int minutes = stationaryTicks / 1200;
                LOGGER.info("玩家已静止 {} 分钟，累计 {} ticks", minutes, stationaryTicks);
            }
        }
    }
    
    /**
     * 根据静止时间检查是否触发崩溃
     */
    private void checkStationaryCrash() {
        float crashChance = 0.0f;
        String timeDesc = "";
        ModConfig.CrashConfig config = ModConfig.getConfig();
        
        // 根据静止时间计算崩溃概率
        if (stationaryTicks >= 12000) { // 10分钟 = 12000 ticks (20*60*10)
            crashChance = config.idle10MinChance / 100.0f;
            timeDesc = I18n.translate("action.enoughcrashes.idle_10m");
        } else if (stationaryTicks >= 6000) { // 5分钟 = 6000 ticks
            crashChance = config.idle5MinChance / 100.0f;
            timeDesc = I18n.translate("action.enoughcrashes.idle_5m");
        } else if (stationaryTicks >= 1200) { // 1分钟 = 1200 ticks
            crashChance = config.idle1MinChance / 100.0f;
            timeDesc = I18n.translate("action.enoughcrashes.idle_1m");
        }
        
        // 如果到达了检查点且随机数满足条件，触发崩溃
        if (crashChance > 0) {
            if (ModConfig.debug) {
                LOGGER.info("检测到静止崩溃检查: {} 概率={}%", timeDesc, crashChance * 100.0f);
            }
        }
        if (crashChance > 0 && EnoughCrashesCommon.RANDOM.nextFloat() < crashChance) {
            // 只在崩溃时重置计时器（因为真的要崩溃了）
            stationaryTicks = 0;
            isTracking = false;
            
            triggerCrash(I18n.translate("action.enoughcrashes.idle_after", timeDesc));
        }
    }
    
    /**
     * 触发崩溃
     */
    public static void triggerCrash(String action) {
        // 如果已经在倒计时，不再重复触发
        if (crashCountdown > 0) return;
        
        totalCrashesTriggered++;
        lastCrashTime = System.currentTimeMillis();
        
        String messageKey = EnoughCrashesCommon.getRandomCrashMessage();
        String message = I18n.translate(messageKey);
        
        // 记录状态供后续使用
        pendingCrashAction = action;
        pendingCrashMessage = message;
        crashCountdown = 100; // 5秒 * 20 ticks/秒 = 100 ticks
        
        LOGGER.error("💥 [Enough Crashes] 在 {} 时触发崩溃 (#{}): {}", 
                    action, totalCrashesTriggered, message);
        
        // 崩溃前的最后信息（显示给玩家）
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null) {
            // 尝试保存存档
            if (client.getServer() != null) {
                // 单人游戏/局域网主机：保存整个服务器数据
                client.getServer().saveAll(false, true, true);
                client.player.sendMessage(Text.literal("§a[Enough Crashes] 已尝试紧急保存存档数据..."), false);
            } else {
                // 多人游戏客户端：无法直接保存世界，但可以保存选项
                client.options.write();
            }

            // 构建详细的警告信息
            String fullWarning = I18n.translate("exception.enoughcrashes.message", message, action, totalCrashesTriggered);
            
            // 使用红色混合字体显示警告
            client.player.sendMessage(
                Text.literal("§c§l" + fullWarning),
                false
            );
            
            // 额外的倒计时提示
            client.player.sendMessage(Text.literal("§4§l⚠ 游戏将在 5 秒后崩溃！"), false);
        }
    }
    
    private String getEntityTypeName(net.minecraft.entity.Entity entity) {
        Identifier id = EntityType.getId(entity.getType());
        if (id != null) {
            return id.getPath(); // 返回实体类型名称
        }
        return "未知生物";
    }
    
    /**
     * 重置状态
     */
    private void resetState() {
        isTracking = false;
        stationaryTicks = 0;
        lastPlayerPos = Vec3d.ZERO;
    }
    
    /**
     * 更新HUD显示（可选）
     */
    private void updateHud(MinecraftClient client) {
        // 这里可以更新一些客户端状态
        // 例如：显示距离下次崩溃检查还有多久
    }
    
    /**
     * 显示静止警告（可选功能）
     */
    private void showStationaryWarning(MinecraftClient client) {
        if (client.player == null) return;
        
        ModConfig.CrashConfig config = ModConfig.getConfig();

        // 只在特定时间点显示警告
        if (stationaryTicks == 600) { // 30秒
            client.player.sendMessage(Text.literal("§e⚠ 你已经静止30秒了，小心崩溃！"), false);
        } else if (stationaryTicks == 1200) { // 1分钟
            client.player.sendMessage(Text.literal(String.format("§c⚠ 你已经静止1分钟了，有%.1f%%概率崩溃！", config.idle1MinChance)), false);
        } else if (stationaryTicks == 3000) { // 2.5分钟
            client.player.sendMessage(Text.literal("§c⚠ 你已经静止2.5分钟了，继续不动会有更高崩溃概率！"), false);
        } else if (stationaryTicks == 6000) { // 5分钟
            client.player.sendMessage(Text.literal(String.format("§4⚠ 你已经静止5分钟了，有%.1f%%概率崩溃！快动起来！", config.idle5MinChance)), false);
        } else if (stationaryTicks == 9000) { // 7.5分钟
            client.player.sendMessage(Text.literal(String.format("§4⚠ 你已经静止7.5分钟了，即将达到%.1f%%崩溃概率！", config.idle10MinChance)), false);
        } else if (stationaryTicks == 12000) { // 10分钟
            client.player.sendMessage(Text.literal(String.format("§4⚠ 你已经静止10分钟了，有%.1f%%概率崩溃！祝你好运！", config.idle10MinChance)), false);
        }
    }

    /**
     * 渲染警告HUD（可选）
     */
    private void renderWarningHud(DrawContext context, RenderTickCounter tickCounter) {
        if (stationaryTicks > 600) { // 静止超过30秒显示警告
            int minutes = stationaryTicks / 1200;
            int seconds = (stationaryTicks % 1200) / 20;
            
            String warning = String.format("§6⚠ 已静止: %d分%d秒", minutes, seconds);
            if (stationaryTicks >= 1200) {
                warning += " §c(有崩溃风险!)";
            }
            
            MinecraftClient client = MinecraftClient.getInstance();
            if (client != null && client.textRenderer != null) {
                int screenWidth = client.getWindow().getScaledWidth();
                int x = screenWidth - client.textRenderer.getWidth(warning) - 10;
                int y = 10;
                
                context.drawTextWithShadow(client.textRenderer, warning, x, y, 0xFFFFFF);
            }
        }
    }
    
    /**
     * 判断物品是否为长矛或三叉戟
     */
    public static boolean isSpear(Item item) {
        Identifier id = Registries.ITEM.getId(item);
        String path = id.getPath();
        return path.contains("spear") || path.contains("trident") || path.contains("lance") || path.contains("halberd");
    }

    /**
     * 自定义崩溃异常类（客户端专用）
     */
    public static class EnoughCrashesException extends RuntimeException {
        public EnoughCrashesException(String message, String action, int crashCount) {
            super(I18n.translate("exception.enoughcrashes.message", message, action, crashCount));
        }
    }
}
