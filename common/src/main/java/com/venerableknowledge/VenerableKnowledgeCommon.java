package com.venerableknowledge;

import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import org.slf4j.Logger;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VenerableKnowledgeCommon {
    public static final String MOD_ID = "venerable_knowledge";
    public static final Logger LOGGER = LogUtils.getLogger();

    public static final int EFFECT_COLOR = 0x9400D3; // 9699539
    public static final int STANDARD_DURATION = 2400; // 2 minutes = 2400 ticks
    public static final int LONG_DURATION = 6000; // 5 minutes = 6000 ticks

    public static MobEffect VENERABLE_KNOWLEDGE_EFFECT;

    public static Potion VENERABLE_KNOWLEDGE;
    public static Potion LONG_VENERABLE_KNOWLEDGE;
    public static Potion VENERABLE_KNOWLEDGE_2;
    public static Potion LONG_VENERABLE_KNOWLEDGE_2;
    public static Potion VENERABLE_KNOWLEDGE_3;
    public static Potion LONG_VENERABLE_KNOWLEDGE_3;
    public static Potion VENERABLE_KNOWLEDGE_4;
    public static Potion LONG_VENERABLE_KNOWLEDGE_4;

    public static final List<ResourceLocation> FOREIGN_EFFECT_IDS = List.of(
        new ResourceLocation("apotheosis", "ancient_knowledge"),
        new ResourceLocation("apothic_attributes", "ancient_knowledge"),
        new ResourceLocation("apothic_attributes", "knowledge"),
        new ResourceLocation("zenith", "ancient_knowledge")
    );

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }

    public static void init() {
        LOGGER.info("Initialized Venerable Knowledge common core for 1.20.x");
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
            MobEffect foreignEffect = BuiltInRegistries.MOB_EFFECT.get(foreignId);
            if (foreignEffect != null && entity.hasEffect(foreignEffect)) {
                MobEffectInstance ext = entity.getEffect(foreignEffect);
                if (ext != null) {
                    entity.removeEffect(foreignEffect);
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
            MobEffect foreignEffect = BuiltInRegistries.MOB_EFFECT.get(foreignId);
            if (foreignEffect != null && player.hasEffect(foreignEffect)) {
                MobEffectInstance inst = player.getEffect(foreignEffect);
                if (inst != null) {
                    maxAmplifier = Math.max(maxAmplifier, inst.getAmplifier());
                }
            }
        }

        return maxAmplifier;
    }

    /**
     * Checks if the potion input stack is one of the Venerable Knowledge potion tiers that brew using enchanted books.
     */
    public static boolean isVenerableKnowledgeBookBrewingPotion(ItemStack inputStack) {
        if (inputStack == null || inputStack.isEmpty()) {
            return false;
        }
        Potion potion = PotionUtils.getPotion(inputStack);
        if (potion == null) {
            return false;
        }
        ResourceLocation id = BuiltInRegistries.POTION.getKey(potion);
        if (id == null || !MOD_ID.equals(id.getNamespace())) {
            return false;
        }
        String path = id.getPath();
        return path.equals("venerable_knowledge_2") || path.equals("long_venerable_knowledge_2")
            || path.equals("venerable_knowledge_3") || path.equals("long_venerable_knowledge_3");
    }

    /**
     * Validates whether an enchanted book reagent has the required enchantments to brew into Level 3 or Level 4.
     * - Level 2 -> Level 3 requires Fortune >= 2.
     * - Level 3 -> Level 4 requires Fortune >= 3 AND Mending >= 1.
     * (Other enchantments on the book are also allowed).
     */
    public static boolean isValidBookForBrewing(ItemStack inputStack, ItemStack reagentStack) {
        if (inputStack == null || reagentStack == null || !reagentStack.is(Items.ENCHANTED_BOOK)) {
            return false;
        }

        Potion potion = PotionUtils.getPotion(inputStack);
        if (potion == null) {
            return false;
        }

        ResourceLocation id = BuiltInRegistries.POTION.getKey(potion);
        if (id == null || !MOD_ID.equals(id.getNamespace())) {
            return false;
        }

        String path = id.getPath();
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(reagentStack);

        int fortune = enchantments.getOrDefault(Enchantments.BLOCK_FORTUNE, 0);
        int mending = enchantments.getOrDefault(Enchantments.MENDING, 0);

        if (path.equals("venerable_knowledge_2") || path.equals("long_venerable_knowledge_2")) {
            return fortune >= 2;
        }

        if (path.equals("venerable_knowledge_3") || path.equals("long_venerable_knowledge_3")) {
            return fortune >= 3 && mending >= 1;
        }

        return false;
    }

    /**
     * Returns the target potion when brewing an enchanted book into Level 3 or Level 4.
     */
    public static Potion getNextPotionForBookBrewing(ItemStack inputStack) {
        if (inputStack == null || inputStack.isEmpty()) {
            return null;
        }
        Potion potion = PotionUtils.getPotion(inputStack);
        if (potion == null) {
            return null;
        }
        ResourceLocation id = BuiltInRegistries.POTION.getKey(potion);
        if (id == null || !MOD_ID.equals(id.getNamespace())) {
            return null;
        }
        String path = id.getPath();
        return switch (path) {
            case "venerable_knowledge_2" -> VENERABLE_KNOWLEDGE_3;
            case "long_venerable_knowledge_2" -> LONG_VENERABLE_KNOWLEDGE_3;
            case "venerable_knowledge_3" -> VENERABLE_KNOWLEDGE_4;
            case "long_venerable_knowledge_3" -> LONG_VENERABLE_KNOWLEDGE_4;
            default -> null;
        };
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

    /**
     * Appends client-side tooltip enhancements for Ring of Venerable Knowledge.
     * Adds description, Level 1 &amp; Level 2 multiplier information, and mod attribution.
     * Deduplicates trailing components if duplicate mod names or identical trailing lines exist.
     *
     * @param tooltip the list of tooltip components
     */
    public static void appendRingTooltip(List<Component> tooltip) {
        if (tooltip == null) {
            return;
        }

        if (tooltip.size() >= 2 && tooltip.get(tooltip.size() - 1).getString().equals(tooltip.get(tooltip.size() - 2).getString())) {
            tooltip.remove(tooltip.size() - 1);
        }

        tooltip.add(Component.translatable("tooltip.venerable_knowledge.ring_description").withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.venerable_knowledge.ring_level_1").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.translatable("tooltip.venerable_knowledge.ring_level_2").withStyle(ChatFormatting.BLUE));
        tooltip.add(Component.literal("§dEffect of Venerable Knowledge"));

        if (tooltip.size() >= 2 && tooltip.get(tooltip.size() - 1).getString().equals(tooltip.get(tooltip.size() - 2).getString())) {
            tooltip.remove(tooltip.size() - 1);
        }
    }
}
