package team.bhe.enoughcrashes.mixin;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.client.EnoughCrashesClient;
import team.bhe.enoughcrashes.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ChatScreen;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftClient.class)
public class MinecraftClientMixin {

    @Inject(method = "setScreen", at = @At("HEAD"))
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (screen == null) return;
        
        // 1. 打开聊天框 (openChatChance)
        if (screen instanceof ChatScreen) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到打开聊天框");
            }
            float chance = ModConfig.getConfig().openChatChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.open_chat"));
            }
        }
        
        // 2. 打开容器/背包 (openContainerChance)
        // HandledScreen 是所有带槽位界面的基类 (背包、箱子、熔炉、工作台等)
        if (screen instanceof HandledScreen) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到打开容器界面: " + screen.getClass().getSimpleName());
            }
            float chance = ModConfig.getConfig().openContainerChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.open_container"));
            }
        }
    }
}
