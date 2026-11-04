package com.venerableknowledge.neoforge;

import com.venerableknowledge.VenerableKnowledgeCommon;
import com.venerableknowledge.effect.VenerableKnowledgeEffect;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLPaths;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.brewing.PotionBrewEvent;
import net.neoforged.neoforge.event.entity.living.LivingExperienceDropEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(VenerableKnowledgeCommon.MOD_ID)
public class VenerableKnowledgeNeoForge {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(Registries.MOB_EFFECT, VenerableKnowledgeCommon.MOD_ID);

    public static final DeferredHolder<MobEffect, MobEffect> VENERABLE_KNOWLEDGE =
            MOB_EFFECTS.register("venerable_knowledge",
                    VenerableKnowledgeEffect::new);

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(Registries.POTION, VenerableKnowledgeCommon.MOD_ID);

    public static final DeferredHolder<Potion, Potion> VENERABLE_KNOWLEDGE_POTION =
            POTIONS.register("venerable_knowledge",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 2400, 0)));

    public static final DeferredHolder<Potion, Potion> LONG_VENERABLE_KNOWLEDGE_POTION =
            POTIONS.register("long_venerable_knowledge",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 6000, 0)));

    public static final DeferredHolder<Potion, Potion> VENERABLE_KNOWLEDGE_2_POTION =
            POTIONS.register("venerable_knowledge_2",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 2400, 1)));

    public static final DeferredHolder<Potion, Potion> LONG_VENERABLE_KNOWLEDGE_2_POTION =
            POTIONS.register("long_venerable_knowledge_2",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 6000, 1)));

    public static final DeferredHolder<Potion, Potion> VENERABLE_KNOWLEDGE_3_POTION =
            POTIONS.register("venerable_knowledge_3",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 2400, 2)));

    public static final DeferredHolder<Potion, Potion> LONG_VENERABLE_KNOWLEDGE_3_POTION =
            POTIONS.register("long_venerable_knowledge_3",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 6000, 2)));

    public static final DeferredHolder<Potion, Potion> VENERABLE_KNOWLEDGE_4_POTION =
            POTIONS.register("venerable_knowledge_4",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 2400, 3)));

    public static final DeferredHolder<Potion, Potion> LONG_VENERABLE_KNOWLEDGE_4_POTION =
            POTIONS.register("long_venerable_knowledge_4",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE, 6000, 3)));

    static {
        VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_EFFECT = VENERABLE_KNOWLEDGE;
    }

    public VenerableKnowledgeNeoForge(IEventBus modEventBus) {
        MOB_EFFECTS.register(modEventBus);
        POTIONS.register(modEventBus);

        VenerableKnowledgeCommon.setupPotionRingConfig(FMLPaths.CONFIGDIR.get());

        NeoForge.EVENT_BUS.addListener(VenerableKnowledgeNeoForge::onLivingExperienceDrop);
        NeoForge.EVENT_BUS.addListener(VenerableKnowledgeNeoForge::onPlayerTick);
        NeoForge.EVENT_BUS.addListener(VenerableKnowledgeNeoForge::onPotionBrewPre);
    }

    public static void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null && event.getDroppedExperience() > 0) {
            int amp = VenerableKnowledgeCommon.getEffectiveAmplifier(player);
            if (amp >= 0) {
                event.setDroppedExperience(event.getDroppedExperience() * VenerableKnowledgeCommon.calculateMultiplier(amp));
            }
        }
    }

    public static void onPlayerTick(PlayerTickEvent.Post event) {
        Player player = event.getEntity();
        if (player.level().isClientSide()) {
            return;
        }

        if (player.tickCount % 10 == 0) {
            if (ModList.get().isLoaded("apotheosis")
                    || ModList.get().isLoaded("apothic_attributes")
                    || ModList.get().isLoaded("zenith")) {
                VenerableKnowledgeCommon.replaceApotheosisEffects(player);
            }
        }
    }

    public static void onPotionBrewPre(PotionBrewEvent.Pre event) {
        ItemStack reagent = event.getItem(3);
        if (reagent.is(Items.ENCHANTED_BOOK)) {
            boolean hasVenerablePotion = false;
            boolean hasValidVenerablePotion = false;

            for (int i = 0; i < 3; i++) {
                ItemStack slotStack = event.getItem(i);
                if (VenerableKnowledgeCommon.isVenerableKnowledgeBookBrewingPotion(slotStack)) {
                    hasVenerablePotion = true;
                    if (VenerableKnowledgeCommon.isValidBookForBrewing(slotStack, reagent)) {
                        hasValidVenerablePotion = true;
                    }
                }
            }

            if (hasVenerablePotion && !hasValidVenerablePotion) {
                event.setCanceled(true);
            }
        }
    }
}
