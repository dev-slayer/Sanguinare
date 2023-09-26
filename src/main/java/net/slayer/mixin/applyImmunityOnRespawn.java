package net.slayer.mixin;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.server.network.ServerPlayerEntity;
import net.slayer.SanguinareMain;
import net.slayer.effects.SanguinareEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEvents.class)
public class applyImmunityOnRespawn {
    @Inject(at = @At("HEAD"), method = "lambda$static$2")
    private static void applySolarImmunityOnSanguinareRespawn(ServerPlayerEvents.AfterRespawn[] callbacks, ServerPlayerEntity oldPlayer, ServerPlayerEntity newPlayer, boolean alive, CallbackInfo ci) {
        if (SanguinareMain.getSanguinareStatus(newPlayer)) {
            newPlayer.addStatusEffect(new StatusEffectInstance(SanguinareEffects.SOLAR_IMMUNITY, 1200));
        }
    }
}
