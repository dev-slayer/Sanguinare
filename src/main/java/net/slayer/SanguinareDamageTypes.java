package net.slayer;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SanguinareDamageTypes {
    public static final RegistryKey<DamageType> SCARLET = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sanguinare", "scarlet"));
    public static final RegistryKey<DamageType> HOLY_BURN = RegistryKey.of(RegistryKeys.DAMAGE_TYPE, new Identifier("sanguinare", "holy_burn"));

    public static DamageSource of(World world, RegistryKey<DamageType> key) {
        return new DamageSource(world.getRegistryManager().get(RegistryKeys.DAMAGE_TYPE).entryOf(key));
    }
}
