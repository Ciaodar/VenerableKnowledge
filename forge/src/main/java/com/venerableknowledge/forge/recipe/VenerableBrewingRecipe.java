package com.venerableknowledge.forge.recipe;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.common.brewing.IBrewingRecipe;

public class VenerableBrewingRecipe implements IBrewingRecipe {

    @Override
    public boolean isIngredient(ItemStack ingredient) {
        if (ingredient == null || ingredient.isEmpty()) {
            return false;
        }
        return ingredient.is(Items.EXPERIENCE_BOTTLE)
                || ingredient.is(Items.LAPIS_BLOCK)
                || ingredient.is(Items.REDSTONE)
                || ingredient.is(Items.ENCHANTED_BOOK);
    }

    @Override
    public boolean isInput(ItemStack input) {
        if (input == null || input.isEmpty()) {
            return false;
        }
        Potion potion = PotionUtils.getPotion(input);
        return potion == Potions.AWKWARD
                || potion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE
                || potion == VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE
                || potion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2
                || potion == VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2
                || potion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_3
                || potion == VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_3
                || potion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_4;
    }

    @Override
    public ItemStack getOutput(ItemStack input, ItemStack ingredient) {
        if (!isInput(input) || !isIngredient(ingredient)) {
            return ItemStack.EMPTY;
        }

        Potion inPotion = PotionUtils.getPotion(input);

        // Awkward + Experience Bottle -> Level 1
        if (inPotion == Potions.AWKWARD && ingredient.is(Items.EXPERIENCE_BOTTLE)) {
            return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE);
        }

        // Level 1 + Lapis Block -> Level 2
        if (inPotion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE && ingredient.is(Items.LAPIS_BLOCK)) {
            return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2);
        }

        // Long 1 + Lapis Block -> Long 2
        if (inPotion == VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE && ingredient.is(Items.LAPIS_BLOCK)) {
            return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2);
        }

        // Redstone upgrades (1 -> Long 1, 2 -> Long 2, 3 -> Long 3, 4 -> Long 4)
        if (ingredient.is(Items.REDSTONE)) {
            if (inPotion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE) {
                return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE);
            }
            if (inPotion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2) {
                return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2);
            }
            if (inPotion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_3) {
                return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_3);
            }
            if (inPotion == VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_4) {
                return PotionUtils.setPotion(new ItemStack(input.getItem()), VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_4);
            }
        }

        // Enchanted Book upgrades
        if (ingredient.is(Items.ENCHANTED_BOOK) && VenerableKnowledgeCommon.isValidBookForBrewing(input, ingredient)) {
            Potion next = VenerableKnowledgeCommon.getNextPotionForBookBrewing(input);
            if (next != null) {
                return PotionUtils.setPotion(new ItemStack(input.getItem()), next);
            }
        }

        return ItemStack.EMPTY;
    }
}
