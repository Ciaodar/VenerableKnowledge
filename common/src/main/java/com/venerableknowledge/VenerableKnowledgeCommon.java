package com.venerableknowledge;

import com.mojang.logging.LogUtils;
import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VenerableKnowledgeCommon {
    public static final String MOD_ID = "venerable_knowledge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int EFFECT_COLOR = 0x9400D3; // 9699539
    public static final int STANDARD_DURATION = 2400; // 2 minutes = 2400 ticks
    public static final int LONG_DURATION = 6000; // 5 minutes = 6000 ticks

    public static Holder<MobEffect> VENERABLE_KNOWLEDGE_EFFECT;

    public static final List<ResourceLocation> FOREIGN_EFFECT_IDS = List.of(
        ResourceLocation.fromNamespaceAndPath("apotheosis", "ancient_knowledge"),
        ResourceLocation.fromNamespaceAndPath("apothic_attributes", "ancient_knowledge"),
        ResourceLocation.fromNamespaceAndPath("apothic_attributes", "knowledge"),
        ResourceLocation.fromNamespaceAndPath("zenith", "ancient_knowledge")
    );

    public static ResourceLocation id(String path) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }

    public static void init() {
        LOGGER.info("Initialized Venerable Knowledge common core");
    }

    /**
     * Multiplier Formula:
     * - If amplifier &lt; 0 return 1.
     * - Formula: {@code 1 << (Math.min(amplifier, 10) + 1)}
     *   Amplifier 0 (Level I)   = 2x
     *   Amplifier 1 (Level II)  = 4x
     *   Amplifier 2 (Level III) = 8x
     *   Amplifier 3 (Level IV)  = 16x
     *
     * @param amplifier the effect amplifier
     * @return the calculated experience multiplier
     */
    public static int calculateMultiplier(int amplifier) {
        if (amplifier < 0) {
            return 1;
        }
        return 1 << (Math.min(amplifier, 10) + 1);
    }

    /**
     * Replaces Apotheosis / Zenith / Apothic Attributes ancient knowledge effects with Venerable Knowledge.
     */
    public static void replaceApotheosisEffects(LivingEntity entity) {
        if (entity == null || VENERABLE_KNOWLEDGE_EFFECT == null) {
            return;
        }

        for (ResourceLocation foreignId : FOREIGN_EFFECT_IDS) {
            Optional<Holder.Reference<MobEffect>> holderOpt = BuiltInRegistries.MOB_EFFECT.getHolder(foreignId);
            if (holderOpt.isPresent()) {
                Holder.Reference<MobEffect> foreignHolder = holderOpt.get();
                if (entity.hasEffect(foreignHolder)) {
                    MobEffectInstance ext = entity.getEffect(foreignHolder);
                    if (ext != null) {
                        entity.removeEffect(foreignHolder);
                        entity.addEffect(new MobEffectInstance(
                            VENERABLE_KNOWLEDGE_EFFECT,
                            ext.getDuration(),
                            ext.getAmplifier(),
                            ext.isAmbient(),
                            ext.isVisible(),
                            ext.showIcon()
                        ));
                    }
                }
            }
        }
    }

    /**
     * Determines the effective amplifier on a player or entity:
     * First replaces any foreign Apotheosis / Zenith effects, then inspects active effects.
     * Returns -1 if no effect is active.
     */
    public static int getEffectiveAmplifier(LivingEntity player) {
        if (player == null) {
            return -1;
        }

        replaceApotheosisEffects(player);

        int maxAmplifier = -1;
        if (VENERABLE_KNOWLEDGE_EFFECT != null && player.hasEffect(VENERABLE_KNOWLEDGE_EFFECT)) {
            MobEffectInstance inst = player.getEffect(VENERABLE_KNOWLEDGE_EFFECT);
            if (inst != null) {
                maxAmplifier = Math.max(maxAmplifier, inst.getAmplifier());
            }
        }

        for (ResourceLocation foreignId : FOREIGN_EFFECT_IDS) {
            Optional<Holder.Reference<MobEffect>> holderOpt = BuiltInRegistries.MOB_EFFECT.getHolder(foreignId);
            if (holderOpt.isPresent()) {
                Holder.Reference<MobEffect> foreignHolder = holderOpt.get();
                if (player.hasEffect(foreignHolder)) {
                    MobEffectInstance inst = player.getEffect(foreignHolder);
                    if (inst != null) {
                        maxAmplifier = Math.max(maxAmplifier, inst.getAmplifier());
                    }
                }
            }
        }

        return maxAmplifier;
    }

    /**
     * Retrieves the enchantment level for a given enchantment key from ItemEnchantments.
     */
    public static int getEnchantmentLevel(ItemEnchantments enchantments, ResourceKey<Enchantment> key) {
        if (enchantments == null || enchantments.isEmpty()) {
            return 0;
        }
        for (var entry : enchantments.entrySet()) {
            if (entry.getKey().is(key)) {
                return entry.getIntValue();
            }
        }
        return 0;
    }

    /**
     * Checks if the potion input stack is one of the Venerable Knowledge potion tiers that brew using enchanted books.
     */
    public static boolean isVenerableKnowledgeBookBrewingPotion(ItemStack inputStack) {
        if (inputStack == null || inputStack.isEmpty()) {
            return false;
        }
        PotionContents contents = inputStack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isEmpty()) {
            return false;
        }
        return contents.potion().get().unwrapKey()
            .map(key -> {
                ResourceLocation potionId = key.location();
                if (!MOD_ID.equals(potionId.getNamespace())) {
                    return false;
                }
                String path = potionId.getPath();
                return path.equals("venerable_knowledge_2") || path.equals("long_venerable_knowledge_2")
                    || path.equals("venerable_knowledge_3") || path.equals("long_venerable_knowledge_3");
            })
            .orElse(false);
    }

    /**
     * Validates whether an enchanted book reagent has the required enchantments to brew into Level 3 or Level 4.
     */
    public static boolean isValidBookForBrewing(ItemStack inputStack, ItemStack reagentStack) {
        if (inputStack == null || reagentStack == null || !reagentStack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        PotionContents contents = inputStack.get(DataComponents.POTION_CONTENTS);
        if (contents == null || contents.potion().isEmpty()) {
            return false;
        }

        Optional<ResourceKey<Potion>> keyOpt = contents.potion().get().unwrapKey();
        if (keyOpt.isEmpty()) {
            return false;
        }

        ResourceLocation potionId = keyOpt.get().location();
        if (!MOD_ID.equals(potionId.getNamespace())) {
            return false;
        }

        String path = potionId.getPath();
        ItemEnchantments enchantments = EnchantmentHelper.getEnchantmentsForCrafting(reagentStack);

        if (path.equals("venerable_knowledge_2") || path.equals("long_venerable_knowledge_2")) {
            return getEnchantmentLevel(enchantments, Enchantments.FORTUNE) >= 2;
        }

        if (path.equals("venerable_knowledge_3") || path.equals("long_venerable_knowledge_3")) {
            return getEnchantmentLevel(enchantments, Enchantments.FORTUNE) >= 3
                && getEnchantmentLevel(enchantments, Enchantments.MENDING) >= 1;
        }

        return false;
    }

    /**
     * PotionRing Config Helper:
     * Safely checks configDir.resolve("potionring.txt").
     * Ensures venerable_knowledge:venerable_knowledge#9699539 is present (and replaces ancient_knowledge:ancient_knowledge#9699539 if found).
     */
    public static void setupPotionRingConfig(Path configDir) {
        if (configDir == null) {
            return;
        }

        try {
            Path configFile = configDir.resolve("potionring.txt");
            if (!Files.exists(configFile)) {
                return;
            }

            List<String> lines = Files.readAllLines(configFile, StandardCharsets.UTF_8);
            String targetEntry = "venerable_knowledge:venerable_knowledge#9699539";
            String oldEntry = "ancient_knowledge:ancient_knowledge#9699539";
            boolean present = false;
            boolean modified = false;

            List<String> newLines = new ArrayList<>();
            for (String line : lines) {
                if (line.contains(oldEntry)) {
                    newLines.add(line.replace(oldEntry, targetEntry));
                    modified = true;
                    present = true;
                } else {
                    if (line.trim().equals(targetEntry)) {
                        present = true;
                    }
                    newLines.add(line);
                }
            }

            if (!present) {
                newLines.add(targetEntry);
                modified = true;
            }

            if (modified) {
                Files.write(configFile, newLines, StandardCharsets.UTF_8);
                LOGGER.info("Updated potionring.txt configuration with {}", targetEntry);
            }
        } catch (Exception e) {
            LOGGER.warn("Failed to update potionring.txt configuration: {}", e.getMessage());
        }
    }
}
