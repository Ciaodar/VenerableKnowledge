package com.venerableknowledge.mixin;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.PotionContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(PotionBrewing.class)
public class PotionBrewingMixin {

    @Inject(method = "isIngredient", at = @At("HEAD"), cancellable = true)
    private void venerableknowledge$isIngredient(ItemStack stack, CallbackInfoReturnable<Boolean> cir) {
        if (stack.is(Items.ENCHANTED_BOOK)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "hasMix", at = @At("HEAD"), cancellable = true)
    private void venerableknowledge$hasMix(ItemStack container, ItemStack ingredient, CallbackInfoReturnable<Boolean> cir) {
        if (ingredient.is(Items.ENCHANTED_BOOK) && VenerableKnowledgeCommon.isValidBookForBrewing(container, ingredient)) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "mix", at = @At("HEAD"), cancellable = true)
    private void venerableknowledge$mix(ItemStack ingredient, ItemStack container, CallbackInfoReturnable<ItemStack> cir) {
        if (ingredient.is(Items.ENCHANTED_BOOK) && VenerableKnowledgeCommon.isValidBookForBrewing(container, ingredient)) {
            PotionContents contents = container.get(DataComponents.POTION_CONTENTS);
            if (contents != null && contents.potion().isPresent()) {
                Optional<ResourceKey<Potion>> keyOpt = contents.potion().get().unwrapKey();
                if (keyOpt.isPresent()) {
                    ResourceLocation potionId = keyOpt.get().location();
                    if (VenerableKnowledgeCommon.MOD_ID.equals(potionId.getNamespace())) {
                        String path = potionId.getPath();
                        String targetPath = null;
                        if ("venerable_knowledge_2".equals(path)) {
                            targetPath = "venerable_knowledge_3";
                        } else if ("long_venerable_knowledge_2".equals(path)) {
                            targetPath = "long_venerable_knowledge_3";
                        } else if ("venerable_knowledge_3".equals(path)) {
                            targetPath = "venerable_knowledge_4";
                        } else if ("long_venerable_knowledge_3".equals(path)) {
                            targetPath = "long_venerable_knowledge_4";
                        }

                        if (targetPath != null) {
                            Optional<Holder.Reference<Potion>> targetHolderOpt = BuiltInRegistries.POTION.getHolder(
                                    ResourceLocation.fromNamespaceAndPath(VenerableKnowledgeCommon.MOD_ID, targetPath)
                            );
                            if (targetHolderOpt.isPresent()) {
                                cir.setReturnValue(PotionContents.createItemStack(container.getItem(), targetHolderOpt.get()));
                            }
                        }
                    }
                }
            }
        }
    }
}
