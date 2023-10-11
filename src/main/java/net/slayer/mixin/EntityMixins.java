package net.slayer.mixin;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.slayer.Config;
import net.slayer.SanguinareMain;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class EntityMixins {

    @Shadow
    @Nullable
    public abstract EntityAttributeInstance getAttributeInstance(EntityAttribute attribute);

    @Inject(at = @At("RETURN"), method = "getAttributeValue(Lnet/minecraft/entity/attribute/EntityAttribute;)D", cancellable = true)
    public void getAttributeValue(EntityAttribute attribute, CallbackInfoReturnable<Double> cir) {
        LivingEntity entity = (LivingEntity) (Object) this;
        if (entity.isPlayer()) {
            if (!entity.getWorld().isClient) {
                ServerPlayerEntity player = entity.getServer().getPlayerManager().getPlayer(entity.getUuid());
                if (SanguinareMain.getSanguinareStatus((ServerPlayerEntity) entity)) {
                    // SanguinareMain.LOGGER.info("Adding: " + String.valueOf(sanguinareSpeedModifier));
                    // SanguinareMain.LOGGER.info("Total: " + String.valueOf(entity.getAttributes().getValue(attribute) + sanguinareSpeedModifier));
                    if (attribute == EntityAttributes.GENERIC_ATTACK_DAMAGE) {
                        float sanguinareModifier = (player.getHungerManager().getFoodLevel() - Config.bloodModifierTurnoverPoint) / Config.bloodStrengthModifier;
                        cir.setReturnValue(entity.getAttributes().getValue(attribute) + sanguinareModifier);
                    }
                }
            }
        }
    }
}
