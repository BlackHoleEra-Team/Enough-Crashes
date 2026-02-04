package team.bhe.enoughcrashes.mixin;

import team.bhe.enoughcrashes.EnoughCrashesCommon;
import team.bhe.enoughcrashes.client.EnoughCrashesClient;
import team.bhe.enoughcrashes.config.ModConfig;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerInteractionManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.item.Items;
import net.minecraft.screen.slot.SlotActionType;
import net.minecraft.screen.CraftingScreenHandler;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.EnchantmentScreenHandler;
import net.minecraft.screen.PlayerScreenHandler;
import net.minecraft.client.resource.language.I18n;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ClientPlayerInteractionManager.class)
public class ClientPlayerInteractionManagerMixin {
    
    @Shadow @Final private MinecraftClient client;

    @Inject(method = "attackEntity", at = @At("HEAD"))
    private void onAttackEntity(PlayerEntity player, Entity target, CallbackInfo ci) {
        if (ModConfig.debug) {
            EnoughCrashesCommon.LOGGER.info("Mixin检测到攻击实体: " + target.getName().getString() + " 使用物品: " + player.getMainHandStack().getItem());
        }
        
        // 强制检测长矛攻击
        if (EnoughCrashesClient.isSpear(player.getMainHandStack().getItem())) {
            float spearChance = ModConfig.getConfig().spearAttackChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < spearChance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.spear_attack"));
            }
        }
    }

    @Inject(method = "interactItem", at = @At("HEAD"))
    private void onInteractItem(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (EnoughCrashesClient.isSpear(player.getStackInHand(hand).getItem())) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到长矛使用(右键): " + player.getStackInHand(hand).getItem());
            }
            
            float spearChance = ModConfig.getConfig().spearAttackChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < spearChance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.spear_attack"));
            }
        }
    }

    @Inject(
        method = "interactBlock",
        at = @At("HEAD")
    )
    private void onInteractBlock(CallbackInfoReturnable<ActionResult> cir) {
        // 这里的检测包括放置方块、使用物品、打开方块容器等
        // 使用配置文件的概率
        if (ModConfig.debug) {
            EnoughCrashesCommon.LOGGER.info("Mixin检测到放置/交互方块");
        }
        float chance = ModConfig.getConfig().placeBlockChance / 100.0f;
        if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
            EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.place_block"));
        }
    }
    
    // 1. 命名生物 (nameEntityChance)
    @Inject(method = "interactEntity", at = @At("HEAD"))
    private void onInteractEntity(PlayerEntity player, Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> cir) {
        if (player.getStackInHand(hand).getItem() == Items.NAME_TAG) {
            if (ModConfig.debug) {
                EnoughCrashesCommon.LOGGER.info("Mixin检测到命名牌命名生物: " + entity.getName().getString());
            }
            float chance = ModConfig.getConfig().nameEntityChance / 100.0f;
            if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.name_entity"));
            }
        }
    }
    
    // 2. 合成物品 (craftItemChance) 和 修理物品 (repairItemChance)
    @Inject(method = "clickSlot", at = @At("HEAD"))
    private void onClickSlot(int syncId, int slotId, int button, SlotActionType actionType, PlayerEntity player, CallbackInfo ci) {
        if (this.client.player == null || this.client.player.currentScreenHandler == null) return;
        
        if (syncId == this.client.player.currentScreenHandler.syncId) {
            // 合成 (Crafting / Player)
            if (this.client.player.currentScreenHandler instanceof CraftingScreenHandler ||
                this.client.player.currentScreenHandler instanceof PlayerScreenHandler) {
                // Slot 0 is result
                // 只有在取出物品时才算（PICKUP 或 QUICK_MOVE）
                if (slotId == 0 && (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE)) {
                     if (ModConfig.debug) {
                         EnoughCrashesCommon.LOGGER.info("Mixin检测到合成取出结果: actionType=" + actionType);
                     }
                     float chance = ModConfig.getConfig().craftItemChance / 100.0f;
                     if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                         EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.craft_item"));
                     }
                }
            }
            
            // 修理 (Anvil)
            if (this.client.player.currentScreenHandler instanceof AnvilScreenHandler) {
                // Slot 2 is result
                if (slotId == 2 && (actionType == SlotActionType.PICKUP || actionType == SlotActionType.QUICK_MOVE)) {
                     if (ModConfig.debug) {
                         EnoughCrashesCommon.LOGGER.info("Mixin检测到铁砧取出结果: actionType=" + actionType);
                     }
                     float chance = ModConfig.getConfig().repairItemChance / 100.0f;
                     if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                         EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.repair_item"));
                     }
                }
            }
        }
    }
    
    // 3. 附魔物品 (enchantItemChance)
    @Inject(method = "clickButton", at = @At("HEAD"))
    private void onClickButton(int syncId, int buttonId, CallbackInfo ci) {
         if (this.client.player == null || this.client.player.currentScreenHandler == null) return;
         
         if (syncId == this.client.player.currentScreenHandler.syncId) {
             // 附魔 (Enchanting)
             if (this.client.player.currentScreenHandler instanceof EnchantmentScreenHandler) {
                 if (ModConfig.debug) {
                     EnoughCrashesCommon.LOGGER.info("Mixin检测到点击附魔按钮: " + buttonId);
                 }
                 float chance = ModConfig.getConfig().enchantItemChance / 100.0f;
                 if (EnoughCrashesCommon.RANDOM.nextFloat() < chance) {
                     EnoughCrashesClient.triggerCrash(I18n.translate("action.enoughcrashes.enchant_item"));
                 }
             }
         }
    }
}
