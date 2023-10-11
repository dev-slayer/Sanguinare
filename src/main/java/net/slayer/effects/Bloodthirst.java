package net.slayer.effects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.random.Random;
import net.slayer.SanguinareDamageTypes;

public class Bloodthirst extends StatusEffect {
    public Bloodthirst() {
        super(StatusEffectCategory.HARMFUL, 5797459);
    }

    @Override
    public boolean canApplyUpdateEffect(int duration, int amplifier) {
        return true;
    }

    @Override
    public void applyUpdateEffect(LivingEntity entity, int amplifier) {
        super.applyUpdateEffect(entity, amplifier);
        if (entity instanceof PlayerEntity playerEntity) {
            playerEntity.addExhaustion(0.005F * (float)(amplifier + 1));
        }
    }
}