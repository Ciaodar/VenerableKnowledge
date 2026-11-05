package com.venerableknowledge.fabric.mixin;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public class PlayerTickMixin {

    @Inject(method = "tick", at = @At("TAIL"))
    private void venerableknowledge$tickApotheosisCheck(CallbackInfo ci) {
        Player self = (Player) (Object) this;
        if (!self.level().isClientSide() && self.tickCount % 10 == 0) {
            VenerableKnowledgeCommon.replaceApotheosisEffects(self);
        }
    }
}
