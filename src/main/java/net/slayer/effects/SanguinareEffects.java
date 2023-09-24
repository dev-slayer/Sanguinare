package net.slayer.effects;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectCategory;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.slayer.SanguinareMain;

import java.util.Objects;

public class SanguinareEffects {

    public static final StatusEffect SCARLET_SICKNESS = new ScarletSickness();
    public static final StatusEffect BLOODTHIRST = new Bloodthirst();
    public static final StatusEffect INSTANT_SANGUINARE = new InstantSanguinare();

    public static void registerStatusEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "scarlet_sickness"), SCARLET_SICKNESS);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "bloodthirst"), BLOODTHIRST);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "instant_sanguinare"), INSTANT_SANGUINARE);
    }
}
