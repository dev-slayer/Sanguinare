package net.slayer.effects;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.slayer.SanguinareMain;

public class SanguinareEffects {

    public static final StatusEffect SCARLET_SICKNESS = new ScarletSickness();
    public static final StatusEffect BLOODTHIRST = new Bloodthirst();
    public static final StatusEffect CLEANSING = new Cleansing();
    public static final StatusEffect SOLAR_IMMUNITY = new SolarImmunity();

    public static void registerStatusEffects() {
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "scarlet_sickness"), SCARLET_SICKNESS);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "bloodthirst"), BLOODTHIRST);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "cleansing"), CLEANSING);
        Registry.register(Registries.STATUS_EFFECT, new Identifier(SanguinareMain.MOD_ID, "solar_immunity"), SOLAR_IMMUNITY);

    }
}
