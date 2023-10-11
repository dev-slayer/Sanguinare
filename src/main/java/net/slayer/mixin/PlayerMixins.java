package net.slayer.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.slayer.Config;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;
import net.slayer.effects.SanguinareEffects;
import net.slayer.item.SanguinareItems;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixins extends LivingEntity {

	@Shadow
	protected HungerManager hungerManager;
	@Shadow
	public abstract boolean damage(DamageSource source, float amount);

	@Shadow public abstract HungerManager getHungerManager();

	@Shadow public abstract void addExhaustion(float exhaustion);

	@Shadow public abstract boolean isPlayer();

	protected PlayerMixins(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	private boolean isWeak() {
		return this.getHealth() <= 1 && this.hungerManager.getFoodLevel() == 0;
	}

	@Unique private int time = 0;

	@Unique private int regen = 0;
	@Inject(at = @At("HEAD"), method = "isInvulnerableTo", cancellable = true)
	private void sanguinareDrowningInvulnerability(DamageSource damageSource, CallbackInfoReturnable<Boolean> cir) {
		if (!this.getWorld().isClient) {
			ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(this.getUuid());
			if (SanguinareMain.getSanguinareStatus(player)) {
				cir.setReturnValue(damageSource.isIn(DamageTypeTags.IS_DROWNING) || damageSource.isIn(DamageTypeTags.IS_FREEZING));
			}
		}
	}
	@Inject(at = @At("HEAD"), method = "tick")
	private void sanguinareTick(CallbackInfo info) {
		if (!this.getWorld().isClient) {

			ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(this.getUuid());

			if (player == null) {
				return;
			}

			boolean sanguinareStatus = SanguinareMain.getSanguinareStatus(player);

			if (age < 2) {
				SanguinareMain.setSanguinareStatus(this.getWorld(), player, sanguinareStatus);
			}

			if (sanguinareStatus) {

				float sanguinareModifier = (player.getHungerManager().getFoodLevel() - Config.bloodModifierTurnoverPoint) / Config.bloodSpeedModifier;
				EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
				if (entityAttributeInstance != null) {
					if (entityAttributeInstance.getModifier(SanguinareMain.SANGUINARE_SPEED_BOOST_ID) != null) {
						entityAttributeInstance.removeModifier(SanguinareMain.SANGUINARE_SPEED_BOOST_ID);
					}
				}
				entityAttributeInstance.addTemporaryModifier(new EntityAttributeModifier(SanguinareMain.SANGUINARE_SPEED_BOOST_ID, "Sanguinare Blood Speed boost", sanguinareModifier, EntityAttributeModifier.Operation.ADDITION));

				this.setAir(300);
				this.setFrozenTicks(0);

				if (this.hasStatusEffect(StatusEffects.HUNGER)) {
					int hungerDuration = this.getStatusEffect(StatusEffects.HUNGER).getDuration();
					int hungerAmplifier = this.getStatusEffect(StatusEffects.HUNGER).getAmplifier();
					boolean hungerAmbient = this.getStatusEffect(StatusEffects.HUNGER).isAmbient();
					boolean hungerParticles = this.getStatusEffect(StatusEffects.HUNGER).shouldShowParticles();

					this.addStatusEffect(new StatusEffectInstance(SanguinareEffects.BLOODTHIRST, hungerDuration, hungerAmplifier, hungerAmbient, true));
					this.removeStatusEffect(StatusEffects.HUNGER);
				}

				time++;
				if (time >= Config.sunDamageRate) {
					time = 0;
					if (getSunExposure(player) && player.interactionManager.getGameMode().isSurvivalLike()) {
						for(int i = 0; i < 4; ++i) {
							SmokingParticles();
						}

						if (!this.hasStatusEffect(SanguinareEffects.SOLAR_IMMUNITY) && !this.isHolding(SanguinareItems.UMBRELLA)) {
							this.damage(SanguinareDamageTypes.of(this.getWorld(), SanguinareDamageTypes.HOLY_BURN), 1.0f);
							this.timeUntilRegen = 0;
							for(int i = 0; i < 4; ++i) {
								BurningParticles();
							}
						}
					}
				}

				regen++;
				if (regen >= this.getHealth() / Config.regenRate) {
					regen = 0;
					if (this.getHealth() < this.getMaxHealth() && this.getHungerManager().getFoodLevel() > 0 && !getSunExposure(player)) {
						this.addExhaustion(3);
						this.heal(1f);
					}
				}
			}
		}
	}

	@WrapWithCondition(
			method = "tickMovement",
			at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/PlayerEntity;setMovementSpeed(F)V")
	)
	private boolean disableNormalMovementSetIfSanguinare(PlayerEntity instance, float movementSpeed) {
		if (this.getWorld().isClient) {
			return true;
		} else return !SanguinareMain.getSanguinareStatus((ServerPlayerEntity) instance);
	}

	@Unique private void SmokingParticles() {
		double a = this.random.nextGaussian() * 0.01;
		double b = this.random.nextGaussian() * 0.01;
		double c = this.random.nextGaussian() * 0.01;
		((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMOKE, this.getParticleX(1), this.getRandomBodyY(), this.getParticleZ(1), 0, a, b, c, 1);
	}
	@Unique private void BurningParticles() {
		double a = this.random.nextGaussian() * 0.01;
		double b = this.random.nextGaussian() * 0.01;
		double c = this.random.nextGaussian() * 0.01;
		((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMALL_FLAME, this.getParticleX(1), this.getRandomBodyY(), this.getParticleZ(1), 0, a * 2, b * 2, c * 2, 1);
	}

	@Unique private boolean getSunExposure(PlayerEntity player) {
		if (player.getWorld().isDay()) {
			float f = player.getBrightnessAtEyes();
			BlockPos blockPos = BlockPos.ofFloored(player.getX(), player.getEyeY(), player.getZ());
			boolean bl = player.isWet() || player.inPowderSnow || player.wasInPowderSnow;
			return f > 0.5F && !bl && player.getWorld().isSkyVisible(blockPos);
		} else {
			return false;
		}
	}
}