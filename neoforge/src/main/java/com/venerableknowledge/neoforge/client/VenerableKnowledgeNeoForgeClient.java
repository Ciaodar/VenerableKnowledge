package com.venerableknowledge.neoforge.client;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;

@EventBusSubscriber(modid = VenerableKnowledgeCommon.MOD_ID, value = Dist.CLIENT)
public class VenerableKnowledgeNeoForgeClient {

    @SubscribeEvent
    public static void onItemTooltip(ItemTooltipEvent event) {
        ItemStack stack = event.getItemStack();
        Identifier id = BuiltInRegistries.ITEM.getKey(stack.getItem());
        if (id != null && "potionring".equals(id.getNamespace()) && "ring_of_venerable_knowledge".equals(id.getPath())) {
            VenerableKnowledgeCommon.appendRingTooltip(event.getToolTip());
        }
    }
}
