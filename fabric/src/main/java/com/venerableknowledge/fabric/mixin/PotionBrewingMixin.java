package com.venerableknowledge.fabric.mixin;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void venerableknowledge$isIngredient(ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (ingredient != null && ingredient.is(Items.ENCHANTED_BOOK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void venerableknowledge$hasMix(ItemStack source, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (VenerableKnowledgeCommon.isValidBookForBrewing(source, ingredient)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void venerableknowledge$mix(ItemStack ingredient, ItemStack source, CallbackInfoReturnable<ItemStack> cir) {
        if (VenerableKnowledgeCommon.isValidBookForBrewing(source, ingredient)) {
            ItemStack upgraded = VenerableKnowledgeCommon.getUpgradedPotionStack(source);
            if (!upgraded.isEmpty()) {
                cir.setReturnValue(upgraded);
            }
        }
    }
}
