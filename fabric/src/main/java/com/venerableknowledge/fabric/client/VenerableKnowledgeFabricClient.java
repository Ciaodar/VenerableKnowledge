package com.venerableknowledge.fabric.client;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;

public class VenerableKnowledgeFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        ItemTooltipCallback.EVENT.register((stack, context, type, lines) -> {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            if (id != null && "potionring".equals(id.getNamespace()) && "ring_of_venerable_knowledge".equals(id.getPath())) {
                VenerableKnowledgeCommon.appendRingTooltip(lines);
            }
        });
    }
}
