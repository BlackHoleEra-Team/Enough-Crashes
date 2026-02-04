package team.bhe.enoughcrashes.mixin;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.client.EnoughCrashesClient;
import team.bhe.enoughcrashes.config.ModConfig;
import net.minecraft.client.network.ClientAdvancementManager;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.advancement.AdvancementProgress;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientAdvancementManager.class)
public class ClientAdvancementManagerMixin {

    @Inject(method = "onAdvancements", at = @At("TAIL"))
    private void onAdvancements(AdvancementUpdateS2CPacket packet, CallbackInfo ci) {
        // 如果是初始化包（清除当前），则忽略，避免进服崩溃
        if (packet.shouldClearCurrent()) return;
        
        // 检查是否有新完成的进度
        boolean hasNewAdvancement = false;
        for (AdvancementProgress progress : packet.getAdvancementsToProgress().values()) {
            if (progress.isDone()) {
                hasNewAdvancement = true;
                break;
            }
        }
        
        if (ModConfig.debug) {
            EnoughCrashesCommon.LOGGER.info("Mixin检测到成就更新: progress=" + packet.getAdvancementsToProgress().size());
        }
        
        if (hasNewAdvancement) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到获得新成就/进度");
            }
            float chance = ModConfig.getConfig().getAdvancementChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.get_advancement"));
            }
        }
    }
}
