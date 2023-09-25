package net.slayer.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.slayer.SanguinareMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(HungerManager.class)
public class DisableRegeneration {

    @WrapWithCondition(
            method = "update",
            at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.heal (F)V")
    )
    private boolean disableRegenIfSanguinare(PlayerEntity instance, float amount) {
        return !SanguinareMain.getSanguinareStatus((ServerPlayerEntity) instance);
    }
}
