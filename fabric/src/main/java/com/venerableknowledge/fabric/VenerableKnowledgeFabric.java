package com.venerableknowledge.fabric;

import com.venerableknowledge.VenerableKnowledgeCommon;
import com.venerableknowledge.effect.VenerableKnowledgeEffect;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.registry.FabricBrewingRecipeRegistryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Ingredient;

public class VenerableKnowledgeFabric implements ModInitializer {

    public static Holder<Potion> VENERABLE_KNOWLEDGE;
    public static Holder<Potion> LONG_VENERABLE_KNOWLEDGE;
    public static Holder<Potion> VENERABLE_KNOWLEDGE_2;
    public static Holder<Potion> LONG_VENERABLE_KNOWLEDGE_2;
    public static Holder<Potion> VENERABLE_KNOWLEDGE_3;
    public static Holder<Potion> LONG_VENERABLE_KNOWLEDGE_3;
    public static Holder<Potion> VENERABLE_KNOWLEDGE_4;
    public static Holder<Potion> LONG_VENERABLE_KNOWLEDGE_4;

    @Override
    public void onInitialize() {
        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_EFFECT = Registry.registerForHolder(
                BuiltInRegistries.MOB_EFFECT,
                VenerableKnowledgeCommon.id("venerable_knowledge"),
                new VenerableKnowledgeEffect()
        );

        VENERABLE_KNOWLEDGE = registerPotion("venerable_knowledge", 2400, 0);
        LONG_VENERABLE_KNOWLEDGE = registerPotion("long_venerable_knowledge", 6000, 0);
        VENERABLE_KNOWLEDGE_2 = registerPotion("venerable_knowledge_2", 2400, 1);
        LONG_VENERABLE_KNOWLEDGE_2 = registerPotion("long_venerable_knowledge_2", 6000, 1);
        VENERABLE_KNOWLEDGE_3 = registerPotion("venerable_knowledge_3", 2400, 2);
        LONG_VENERABLE_KNOWLEDGE_3 = registerPotion("long_venerable_knowledge_3", 6000, 2);
        VENERABLE_KNOWLEDGE_4 = registerPotion("venerable_knowledge_4", 2400, 3);
        LONG_VENERABLE_KNOWLEDGE_4 = registerPotion("long_venerable_knowledge_4", 6000, 3);

        FabricBrewingRecipeRegistryBuilder.BUILD.register(builder -> {
            builder.registerPotionRecipe(Potions.AWKWARD, Ingredient.of(Items.EXPERIENCE_BOTTLE), VENERABLE_KNOWLEDGE);
            builder.registerPotionRecipe(VENERABLE_KNOWLEDGE, Ingredient.of(Items.LAPIS_BLOCK), VENERABLE_KNOWLEDGE_2);
            builder.registerPotionRecipe(LONG_VENERABLE_KNOWLEDGE, Ingredient.of(Items.LAPIS_BLOCK), LONG_VENERABLE_KNOWLEDGE_2);
            builder.registerPotionRecipe(VENERABLE_KNOWLEDGE, Ingredient.of(Items.REDSTONE), LONG_VENERABLE_KNOWLEDGE);
            builder.registerPotionRecipe(VENERABLE_KNOWLEDGE_2, Ingredient.of(Items.REDSTONE), LONG_VENERABLE_KNOWLEDGE_2);
            builder.registerPotionRecipe(VENERABLE_KNOWLEDGE_3, Ingredient.of(Items.REDSTONE), LONG_VENERABLE_KNOWLEDGE_3);
            builder.registerPotionRecipe(VENERABLE_KNOWLEDGE_4, Ingredient.of(Items.REDSTONE), LONG_VENERABLE_KNOWLEDGE_4);
        });

        VenerableKnowledgeCommon.setupPotionRingConfig(FabricLoader.getInstance().getConfigDir());
    }

    private static Holder<Potion> registerPotion(String name, int duration, int amplifier) {
        return Registry.registerForHolder(
                BuiltInRegistries.POTION,
                VenerableKnowledgeCommon.id(name),
                new Potion("venerable_knowledge", new MobEffectInstance(VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_EFFECT, duration, amplifier))
        );
    }
}
