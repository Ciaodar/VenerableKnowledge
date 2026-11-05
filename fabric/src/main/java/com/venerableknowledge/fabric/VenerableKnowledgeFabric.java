package com.venerableknowledge.fabric;

import com.venerableknowledge.VenerableKnowledgeCommon;
import com.venerableknowledge.effect.VenerableKnowledgeEffect;
import com.venerableknowledge.fabric.mixin.PotionBrewingInvoker;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;

public class VenerableKnowledgeFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_EFFECT = Registry.register(
                BuiltInRegistries.MOB_EFFECT,
                VenerableKnowledgeCommon.id("venerable_knowledge"),
                new VenerableKnowledgeEffect()
        );

        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE = registerPotion("venerable_knowledge", 2400, 0);
        VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE = registerPotion("long_venerable_knowledge", 6000, 0);
        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2 = registerPotion("venerable_knowledge_2", 2400, 1);
        VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2 = registerPotion("long_venerable_knowledge_2", 6000, 1);
        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_3 = registerPotion("venerable_knowledge_3", 2400, 2);
        VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_3 = registerPotion("long_venerable_knowledge_3", 6000, 2);
        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_4 = registerPotion("venerable_knowledge_4", 2400, 3);
        VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_4 = registerPotion("long_venerable_knowledge_4", 6000, 3);

        registerBrewingRecipes();

        VenerableKnowledgeCommon.setupPotionRingConfig(FabricLoader.getInstance().getConfigDir());
    }

    private static Potion registerPotion(String name, int duration, int amplifier) {
        return Registry.register(
                BuiltInRegistries.POTION,
                VenerableKnowledgeCommon.id(name),
                new Potion("venerable_knowledge", new MobEffectInstance(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_EFFECT, duration, amplifier))
        );
    }

    private static void registerBrewingRecipes() {
        // Awkward + Experience Bottle -> Level 1
        PotionBrewingInvoker.invokeAddMix(Potions.AWKWARD, Items.EXPERIENCE_BOTTLE, VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE);

        // Level 1 + Lapis Block -> Level 2
        PotionBrewingInvoker.invokeAddMix(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE, Items.LAPIS_BLOCK, VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2);

        // Long 1 + Lapis Block -> Long 2
        PotionBrewingInvoker.invokeAddMix(VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE, Items.LAPIS_BLOCK, VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2);

        // Redstone upgrades (1 -> Long 1, 2 -> Long 2, 3 -> Long 3, 4 -> Long 4)
        PotionBrewingInvoker.invokeAddMix(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE, Items.REDSTONE, VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE);
        PotionBrewingInvoker.invokeAddMix(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2, Items.REDSTONE, VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2);
        PotionBrewingInvoker.invokeAddMix(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_3, Items.REDSTONE, VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_3);
        PotionBrewingInvoker.invokeAddMix(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_4, Items.REDSTONE, VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_4);
    }
}
