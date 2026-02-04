package team.bhe.enoughcrashes.mixin;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.client.EnoughCrashesClient;
import team.bhe.enoughcrashes.config.ModConfig;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin {
    
    @Inject(method = "consumeItem", at = @At("HEAD"))
    private void onConsumeItem(CallbackInfo ci) {
        LivingEntity entity = (LivingEntity)(Object)this;
        // 仅在客户端且是当前玩家时触发
        // 1.21.11 环境下 entity.getWorld() 编译报错，且 entity instanceof ClientPlayerEntity 已隐含客户端条件
        if (entity instanceof ClientPlayerEntity) {
             ItemStack stack = entity.getActiveItem();
             if (stack.contains(DataComponentTypes.FOOD)) {
                 if (ModConfig.debug) {
                     EnoughCrashesCommon.LOGGER.info("Mixin检测到吃东西: " + stack.getItem());
                 }
                 
                 float chance = ModConfig.getConfig().eatFoodChance / 100.0f;
                 if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                     EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.eat_food", stack.getName().getString()));
                 }
             }
        }
    }
}
