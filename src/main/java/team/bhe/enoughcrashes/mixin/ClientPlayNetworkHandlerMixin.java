package team.bhe.enoughcrashes.mixin;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.client.EnoughCrashesClient;
import team.bhe.enoughcrashes.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.GameStateChangeS2CPacket;
import net.minecraft.network.packet.s2c.play.ItemPickupAnimationS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerRespawnS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityStatusS2CPacket;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityDamageS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    // 1. 拾取物品 (pickupItemChance)
    @Inject(method = "onItemPickupAnimation", at = @At("HEAD"))
    private void onItemPickupAnimation(ItemPickupAnimationS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null) return;
        Entity entity = client.world.getEntityById(packet.getEntityId());
        Entity collector = client.world.getEntityById(packet.getCollectorEntityId());
        
        // 如果收集者是当前玩家
        if (collector == client.player) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到拾取物品动画: entityId=" + packet.getEntityId());
            }
            float chance = ModConfig.getConfig().pickupItemChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.pickup_item"));
            }
        }
    }

    // 2. 切换游戏模式 (switchGameModeChance) 和 天气变化 (weatherChangeChance)
    @Inject(method = "onGameStateChange", at = @At("HEAD"))
    private void onGameStateChange(GameStateChangeS2CPacket packet, CallbackInfo ci) {
        GameStateChangeS2CPacket.Reason reason = packet.getReason();
        float value = packet.getValue();

        // 切换游戏模式 (Reason 3)
        if (reason == GameStateChangeS2CPacket.GAME_MODE_CHANGED) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到切换游戏模式: value=" + value);
            }
            float chance = ModConfig.getConfig().switchGameModeChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.switch_gamemode"));
            }
        }
        
        // 天气变化 (Reason 1, 2, 7, 8)
        if (reason == GameStateChangeS2CPacket.RAIN_STARTED || 
            reason == GameStateChangeS2CPacket.RAIN_STOPPED || 
            reason == GameStateChangeS2CPacket.RAIN_GRADIENT_CHANGED || 
            reason == GameStateChangeS2CPacket.THUNDER_GRADIENT_CHANGED) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到天气变化: reason=" + reason + " value=" + value);
            }
            float chance = ModConfig.getConfig().weatherChangeChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.weather_change"));
            }
        }
    }

    // 3. 进入新维度 (enterDimensionChance)
    @Inject(method = "onPlayerRespawn", at = @At("HEAD"))
    private void onPlayerRespawn(PlayerRespawnS2CPacket packet, CallbackInfo ci) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world != null) {
             // 尝试使用 commonPlayerSpawnInfo() (Record) 或者 getCommonPlayerSpawnInfo()
             // 如果是 1.21，应该是 commonPlayerSpawnInfo()
             boolean dimensionChanged = !packet.commonPlayerSpawnInfo().dimension().equals(client.world.getRegistryKey());
             
             if (dimensionChanged) {
                 if (ModConfig.debug) {
                     EnoughCrashesCommon.LOGGER.info("Mixin检测到进入新维度: " + packet.commonPlayerSpawnInfo().dimension().getValue());
                 }
                 float chance = ModConfig.getConfig().enterDimensionChance / 100.0f;
                 if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                     EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.enter_dimension"));
                 }
             }
        }
    }

    // 4. 驯服生物 (tameEntityChance) 和 繁殖生物 (breedEntityChance)
    @Inject(method = "onEntityStatus", at = @At("HEAD"))
    private void onEntityStatus(EntityStatusS2CPacket packet, CallbackInfo ci) {
        if (packet.getStatus() == 7 || packet.getStatus() == 18) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到生物事件: status=" + packet.getStatus());
            }
            float tameChance = ModConfig.getConfig().tameEntityChance / 100.0f;
            float breedChance = ModConfig.getConfig().breedEntityChance / 100.0f;
            float chance = (packet.getStatus() == 7) ? tameChance : breedChance;
            
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                String action = (packet.getStatus() == 7) ? "action.enoughcrashes.tame_entity" : "action.enoughcrashes.breed_entity";
                EnoughCrashesClient.triggerCrash(I18n.translate(action));
            }
        }
    }
    
    // 5. 昼夜交替 (dayNightCycleChance)
    @Inject(method = "onWorldTimeUpdate", at = @At("HEAD"))
    private void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        // 使用 timeOfDay() (Record component)
        long time = packet.timeOfDay() % 24000;
        if (time < 0) time += 24000;
        
        if ((time >= 0 && time < 20) || (time >= 13000 && time < 13020)) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到昼夜交替: time=" + time);
            }
            float chance = (ModConfig.getConfig().dayNightCycleChance / 100.0f) / 20.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.day_night_cycle"));
            }
        }
    }
    
    // 6. 传送 (teleportChance)
    @Inject(method = "onPlayerPositionLook", at = @At("HEAD"))
    private void onPlayerPositionLook(PlayerPositionLookS2CPacket packet, CallbackInfo ci) {
        // 服务器强制设置位置（通常是传送）
        if (ModConfig.debug) {
            EnoughCrashesCommon.LOGGER.info("Mixin检测到传送/位置重置");
        }
        float chance = ModConfig.getConfig().teleportChance / 100.0f;
        if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
            EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.teleport"));
        }
    }
    
    // 7. 被生物攻击 (beAttackedByEntityChance)
    @Inject(method = "onEntityDamage", at = @At("HEAD"))
    private void onEntityDamage(EntityDamageS2CPacket packet, CallbackInfo ci) {
        // 检查受害者是否是玩家
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player != null && packet.entityId() == client.player.getId()) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到玩家受伤: entityId=" + packet.entityId());
            }
            float chance = ModConfig.getConfig().beAttackedByEntityChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.be_attacked"));
            }
        }
    }
}
