package com.venerableknowledge.forge.client;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = VenerableKnowledgeCommon.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class VenerableKnowledgeForgeClient {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && "potionring".equals(id.getNamespace()) && "ring_of_venerable_knowledge".equals(id.getPath())) {
            VenerableKnowledgeCommon.appendRingTooltip(event.getToolTip());
        }
    }
}
