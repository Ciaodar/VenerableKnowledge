package com.venerableknowledge.fabric.mixin;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private static void venerableknowledge$isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && stack.is(Items.ENCHANTED_BOOK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private static void venerableknowledge$hasMix(ItemStack potion, ItemStack reagent, CallbackInfoReturnable<Boolean> cir) {
        if (reagent != null && reagent.is(Items.ENCHANTED_BOOK) && VenerableKnowledgeCommon.isVenerableKnowledgeBookBrewingPotion(potion)) {
            if (VenerableKnowledgeCommon.isValidBookForBrewing(potion, reagent)) {
                cir.setReturnValue(true);
            }
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private static void venerableknowledge$mix(ItemStack reagent, ItemStack potion, CallbackInfoReturnable<ItemStack> cir) {
        if (reagent != null && reagent.is(Items.ENCHANTED_BOOK) && VenerableKnowledgeCommon.isVenerableKnowledgeBookBrewingPotion(potion)) {
            if (VenerableKnowledgeCommon.isValidBookForBrewing(potion, reagent)) {
                Potion next = VenerableKnowledgeCommon.getNextPotionForBookBrewing(potion);
                if (next != null) {
                    cir.setReturnValue(PotionUtils.setPotion(new ItemStack(potion.getItem()), next));
                }
            }
        }
    }
}
