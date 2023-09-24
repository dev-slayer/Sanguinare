package net.slayer.item;

import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.item.FoodComponent;
import net.minecraft.util.math.random.Random;
import net.slayer.SanguinareMain;
import net.slayer.effects.SanguinareEffects;

public class FoodComponents {
    public static final FoodComponent ACTIVATED_ANCIENT_BLOOD = new FoodComponent.Builder().hunger(0).saturationModifier(0).statusEffect(new StatusEffectInstance(SanguinareEffects.SCARLET_SICKNESS, Random.createLocal().nextBetweenExclusive(36000, 72000)), 1f).build();
}
