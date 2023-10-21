package net.slayer.effects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;

public class Cleansing extends StatusEffect {
    public Cleansing() {
        super(StatusEffectCategory.HARMFUL, 0x9b0026);
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


            if (!SanguinareMain.getSanguinareStatus(player) || entity.hasStatusEffect(SanguinareEffects.SCARLET_SICKNESS)) {
                entity.removeStatusEffect(SanguinareEffects.CLEANSING);
                return;
            }

            if (entity.getStatusEffect(SanguinareEffects.CLEANSING).getDuration() < 5) {
                SanguinareMain.setSanguinareStatus(entity.getWorld(), player, false);
                return;
            }
            if (!entity.hasStatusEffect(StatusEffects.STRENGTH)) {
                int rng = Random.createLocal().nextBetweenExclusive(0, 60);
                if (rng == 0 && entity.getHealth() > 1.0F) {
                    entity.damage(SanguinareDamageTypes.of(entity.getWorld(), SanguinareDamageTypes.HOLY_BURN), 1.0f);
                } else {
                    ((PlayerEntity) entity).addExhaustion(0.05F);
                }
            }
        }
    }
}