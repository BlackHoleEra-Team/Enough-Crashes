package team.bhe.enoughcrashes.mixin;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.client.EnoughCrashesClient;
import team.bhe.enoughcrashes.config.ModConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerEntity.class)
public class ClientPlayerEntityMixin {
    private int lastSelectedSlot = -1;

    // 1. 切换物品栏 (switchHotbarChance)
    @Inject(method = "tick", at = @At("HEAD"))
    private void onTick(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity)(Object)this;
        // 使用 Accessor 获取 private 字段 selectedSlot
        int currentSlot = ((PlayerInventoryAccessor)player.getInventory()).getSelectedSlotValue();
        
        if (lastSelectedSlot == -1) {
            lastSelectedSlot = currentSlot;
            return;
        }

        if (currentSlot != lastSelectedSlot) {
            lastSelectedSlot = currentSlot;
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到切换快捷栏: " + currentSlot);
            }
            float chance = ModConfig.getConfig().switchHotbarChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.switch_hotbar"));
            }
        }
    }

    // 2. 丢弃物品 (dropItemChance)
    @Inject(method = "dropSelectedItem", at = @At("HEAD"))
    private void onDropSelectedItem(boolean entireStack, CallbackInfoReturnable<Boolean> cir) {
        if (ModConfig.debug) {
            EnoughCrashesCommon.LOGGER.info("Mixin检测到丢弃物品: entireStack=" + entireStack);
        }
        float chance = ModConfig.getConfig().dropItemChance / 100.0f;
        if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
            EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.drop_item"));
        }
    }
}
