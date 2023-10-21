package net.slayer.effects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.slayer.SanguinareMain;

public class SolarImmunity extends StatusEffect {
    public SolarImmunity() {
        super(StatusEffectCategory.BENEFICIAL, 0xE9AB13);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        if (!entity.getWorld().isClient) {

            if (!(entity.getType() == EntityType.PLAYER)) {
                entity.removeStatusEffect(SanguinareEffects.CLEANSING);
                return;
            }

            ServerPlayerEntity player = ((PlayerEntity) entity).getServer().getPlayerManager().getPlayer(entity.getUuid());


            if (!SanguinareMain.getSanguinareStatus(player)) {
                entity.removeStatusEffect(SanguinareEffects.SOLAR_IMMUNITY);
            }
        }
    }
}