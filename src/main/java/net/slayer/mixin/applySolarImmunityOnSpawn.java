package net.slayer.mixin;

import com.mojang.authlib.GameProfile;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import net.slayer.SanguinareMain;
import net.slayer.effects.SanguinareEffects;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class applySolarImmunityOnSpawn extends PlayerEntity {
    public applySolarImmunityOnSpawn(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Inject(at = @At("HEAD"), method = "onSpawn")
    private void applySolarImmunity(CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            ServerPlayerEntity player = ((PlayerEntity) this).getServer().getPlayerManager().getPlayer(this.getUuid());
            if (SanguinareMain.getSanguinareStatus(player)) {
                player.addStatusEffect(new StatusEffectInstance(SanguinareEffects.SOLAR_IMMUNITY, 1200));
            }
        }
    }

    @Override
    public boolean isSpectator() {
        ServerPlayerEntity player = ((PlayerEntity) this).getServer().getPlayerManager().getPlayer(this.getUuid());
        return player.interactionManager.getGameMode() == GameMode.SPECTATOR;
    }

    @Override
    public boolean isCreative() {
        ServerPlayerEntity player = ((PlayerEntity) this).getServer().getPlayerManager().getPlayer(this.getUuid());
        return player.interactionManager.getGameMode() == GameMode.CREATIVE;
    }
}
