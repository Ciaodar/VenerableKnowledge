package com.venerableknowledge.mixin;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.BrewingInput;
import net.minecraft.world.item.crafting.BrewingRecipe;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BrewingRecipe.class)
public class BrewingRecipeMixin {

    @Inject(
        method = "matches(Lnet/minecraft/world/item/crafting/BrewingInput;Lnet/minecraft/world/level/Level;)Z",
        at = @At("RETURN"),
        cancellable = true
    )
    private void venerableknowledge$validateEnchantedBookWithLevel(
        BrewingInput input,
        Level level,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValueZ() && input.reagent().is(Items.ENCHANTED_BOOK)) {
            if (VenerableKnowledgeCommon.isVenerableKnowledgeBookBrewingPotion(input.input())) {
                if (!VenerableKnowledgeCommon.isValidBookForBrewing(input.input(), input.reagent())) {
                    cir.setReturnValue(false);
                }
            }
        }
    }

    @Inject(
        method = "matches(Lnet/minecraft/world/item/crafting/BrewingInput;)Z",
        at = @At("RETURN"),
        cancellable = true
    )
    private void venerableknowledge$validateEnchantedBook(
        BrewingInput input,
        CallbackInfoReturnable<Boolean> cir
    ) {
        if (cir.getReturnValueZ() && input.reagent().is(Items.ENCHANTED_BOOK)) {
            if (VenerableKnowledgeCommon.isVenerableKnowledgeBookBrewingPotion(input.input())) {
                if (!VenerableKnowledgeCommon.isValidBookForBrewing(input.input(), input.reagent())) {
                    cir.setReturnValue(false);
                }
            }
        }
    }
}
