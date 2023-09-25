package net.slayer.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntity.class)
public abstract class PlayerMixins extends LivingEntity {

	@Shadow
	protected HungerManager hungerManager;

	@Shadow
	public abstract boolean damage(DamageSource source, float amount);

	@Shadow public abstract HungerManager getHungerManager();

	@Shadow public abstract void addExhaustion(float exhaustion);

	protected PlayerMixins(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	private boolean isWeak() {
		return this.getHealth() <= 1 && this.hungerManager.getFoodLevel() == 0;
	}

	@Unique private int time = 0;

	@Unique private int regen = 0;

	@Inject(at = @At("HEAD"), method = "tick")
	private void sanguinare$tick(CallbackInfo info) {
		if (!this.getWorld().isClient) {

			ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(this.getUuid());

			if (player == null) {
				return;
			}

			boolean sanguinareStatus = SanguinareMain.getSanguinareStatus(player);


			if (sanguinareStatus) {

				time++;
				if (time >= 4) {
					time = 0;
					if (getSunExposure(player)) {
						this.damage(SanguinareDamageTypes.of(this.getWorld(), SanguinareDamageTypes.HOLY_BURN), 1.0f);
						this.timeUntilRegen = 0;
						for(int i = 0; i < 7; ++i) {
							BurningParticles();
						}
					}
				}
			}

			if(sanguinareStatus) {
				regen++;
				if (regen >= this.getHealth() / 4) {
					regen = 0;
					if (this.getHealth() < this.getMaxHealth() && this.getHungerManager().getFoodLevel() > 0 && !getSunExposure(player)) {
						this.addExhaustion(3);
						this.heal(1f);
					}
				}
			}
		}
	}

	@Unique public void BurningParticles() {
		double a = this.random.nextGaussian() * 0.01;
		double b = this.random.nextGaussian() * 0.01;
		double c = this.random.nextGaussian() * 0.01;
		((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMOKE, this.getParticleX(1), this.getRandomBodyY(), this.getParticleZ(1), 0, a, b, c, 1);
		((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMALL_FLAME, this.getParticleX(1), this.getRandomBodyY(), this.getParticleZ(1), 0, a * 2, b * 2, c * 2, 1);
	}

	@Unique public boolean getSunExposure(PlayerEntity player) {
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