package com.venerableknowledge.forge;

import com.venerableknowledge.VenerableKnowledgeCommon;
import com.venerableknowledge.effect.VenerableKnowledgeEffect;
import com.venerableknowledge.forge.recipe.VenerableBrewingRecipe;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.brewing.BrewingRecipeRegistry;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.brewing.PotionBrewEvent;
import net.minecraftforge.event.entity.living.LivingExperienceDropEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod(VenerableKnowledgeCommon.MOD_ID)
public class VenerableKnowledgeForge {

    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, VenerableKnowledgeCommon.MOD_ID);

    public static final RegistryObject<MobEffect> VENERABLE_KNOWLEDGE =
            MOB_EFFECTS.register("venerable_knowledge", VenerableKnowledgeEffect::new);

    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, VenerableKnowledgeCommon.MOD_ID);

    public static final RegistryObject<Potion> VENERABLE_KNOWLEDGE_POTION =
            POTIONS.register("venerable_knowledge",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 2400, 0)));

    public static final RegistryObject<Potion> LONG_VENERABLE_KNOWLEDGE_POTION =
            POTIONS.register("long_venerable_knowledge",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 6000, 0)));

    public static final RegistryObject<Potion> VENERABLE_KNOWLEDGE_2_POTION =
            POTIONS.register("venerable_knowledge_2",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 2400, 1)));

    public static final RegistryObject<Potion> LONG_VENERABLE_KNOWLEDGE_2_POTION =
            POTIONS.register("long_venerable_knowledge_2",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 6000, 1)));

    public static final RegistryObject<Potion> VENERABLE_KNOWLEDGE_3_POTION =
            POTIONS.register("venerable_knowledge_3",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 2400, 2)));

    public static final RegistryObject<Potion> LONG_VENERABLE_KNOWLEDGE_3_POTION =
            POTIONS.register("long_venerable_knowledge_3",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 6000, 2)));

    public static final RegistryObject<Potion> VENERABLE_KNOWLEDGE_4_POTION =
            POTIONS.register("venerable_knowledge_4",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 2400, 3)));

    public static final RegistryObject<Potion> LONG_VENERABLE_KNOWLEDGE_4_POTION =
            POTIONS.register("long_venerable_knowledge_4",
                    () -> new Potion("venerable_knowledge", new MobEffectInstance(VENERABLE_KNOWLEDGE.get(), 6000, 3)));

    public VenerableKnowledgeForge() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        MOB_EFFECTS.register(modEventBus);
        POTIONS.register(modEventBus);

        modEventBus.addListener(this::commonSetup);

        MinecraftForge.EVENT_BUS.addListener(this::onLivingExperienceDrop);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerTick);
        MinecraftForge.EVENT_BUS.addListener(this::onPotionBrewPre);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_EFFECT = VENERABLE_KNOWLEDGE.get();
            VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE = VENERABLE_KNOWLEDGE_POTION.get();
            VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE = LONG_VENERABLE_KNOWLEDGE_POTION.get();
            VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_2 = VENERABLE_KNOWLEDGE_2_POTION.get();
            VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_2 = LONG_VENERABLE_KNOWLEDGE_2_POTION.get();
            VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_3 = VENERABLE_KNOWLEDGE_3_POTION.get();
            VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_3 = LONG_VENERABLE_KNOWLEDGE_3_POTION.get();
            VenerableKnowledgeCommon.VENERABLE_KNOWLEDGE_4 = VENERABLE_KNOWLEDGE_4_POTION.get();
            VenerableKnowledgeCommon.LONG_VENERABLE_KNOWLEDGE_4 = LONG_VENERABLE_KNOWLEDGE_4_POTION.get();

            BrewingRecipeRegistry.addRecipe(new VenerableBrewingRecipe());
            VenerableKnowledgeCommon.setupPotionRingConfig(FMLPaths.CONFIGDIR.get());
        });
    }

    public void onLivingExperienceDrop(LivingExperienceDropEvent event) {
        Player player = event.getAttackingPlayer();
        if (player != null && event.getDroppedExperience() > 0) {
            int amp = VenerableKnowledgeCommon.getEffectiveAmplifier(player);
            if (amp >= 0) {
                event.setDroppedExperience(event.getDroppedExperience() * VenerableKnowledgeCommon.calculateMultiplier(amp));
            }
        }
    }

    public void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) {
            return;
        }

        Player player = event.player;
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

    public void onPotionBrewPre(PotionBrewEvent.Pre event) {
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
