package net.slayer.effects;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.random.Random;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;

import java.util.Iterator;
import java.util.Map;

public class ScarletSickness extends StatusEffect {
    public ScarletSickness() {
        super(StatusEffectCategory.HARMFUL, 0x9b0026);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {

        if (!entity.getWorld().isClient) {

            // if not a player then the sickness fucks off -chungles
            if (!(entity.getType() == EntityType.PLAYER)) {
                entity.removeStatusEffect(SanguinareEffects.SCARLET_SICKNESS);
                return;
            }

            // defines if the entity is a goddamn player or nah -chungles
                // dawg this isn't even true it just takes the entity into a ServerPlayerEntity format -slayer
                    // when did i say all dat???????? -chungles
            ServerPlayerEntity player = ((PlayerEntity) entity).getServer().getPlayerManager().getPlayer(entity.getUuid());


            if (SanguinareMain.getSanguinareStatus(player)) {
                entity.removeStatusEffect(SanguinareEffects.SCARLET_SICKNESS);
                return;
            }

            if (entity.getStatusEffect(SanguinareEffects.SCARLET_SICKNESS).getDuration() < 5) {
                SanguinareMain.setSanguinareStatus(entity.getWorld(), player, true);
                return;
            }
            if (!entity.hasStatusEffect(StatusEffects.STRENGTH)) {
                int rng = Random.createLocal().nextBetweenExclusive(0, 60);
                if (rng == 0 && entity.getHealth() > 1.0F) {
                    entity.damage(SanguinareDamageTypes.of(entity.getWorld(), SanguinareDamageTypes.SCARLET), 1.0f);
                } else if (entity.getWorld().isNight()) {
                    ((PlayerEntity) entity).addExhaustion(0.075F);
                } else {
                    ((PlayerEntity) entity).addExhaustion(0.05F);
                }
            }
        }
    }
}