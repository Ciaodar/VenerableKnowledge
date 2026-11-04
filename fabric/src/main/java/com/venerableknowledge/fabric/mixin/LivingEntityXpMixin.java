package com.venerableknowledge.fabric.mixin;

import com.venerableknowledge.VenerableKnowledgeCommon;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(LivingEntity.class)
public abstract class LivingEntityXpMixin {

    @Shadow
    public abstract @Nullable Player getLastHurtByPlayer();

    @ModifyArg(
        method = "dropExperience(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/Entity;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/ExperienceOrb;award(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/phys/Vec3;I)V"
        ),
        index = 2
    )
    private int venerableknowledge$modifyDroppedExperience(int droppedExperience) {
        if (droppedExperience <= 0) {
            return droppedExperience;
        }

        Player player = this.getLastHurtByPlayer();
        if (player != null) {
            int amp = VenerableKnowledgeCommon.getEffectiveAmplifier(player);
            if (amp >= 0) {
                return droppedExperience * VenerableKnowledgeCommon.calculateMultiplier(amp);
            }
        }

        return droppedExperience;
    }
}
