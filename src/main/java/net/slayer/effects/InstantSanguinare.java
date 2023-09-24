package net.slayer.effects;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;
import net.slayer.SanguinareMain;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Map;

public class InstantSanguinare extends StatusEffect {
    public InstantSanguinare() {
        super(StatusEffectCategory.HARMFUL, 0x9b0026);
    }

    @Override
    public boolean isInstant() {
        return true;
    }
}
