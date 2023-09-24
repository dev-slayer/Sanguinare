package net.slayer.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.slayer.SanguinareDamageTypes;
import net.slayer.SanguinareMain;
import net.slayer.effects.SanguinareEffects;
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

	protected PlayerMixins(EntityType<? extends LivingEntity> entityType, World world) {
		super(entityType, world);
	}

	@Unique
	private boolean isWeak() {
		return this.getHealth() <= 1 && this.hungerManager.getFoodLevel() == 0;
	}

	@Unique private int time = 0;

	@Inject(at = @At("HEAD"), method = "tick")
	private void sanguinare$tick(CallbackInfo info) {
		if (!this.getWorld().isClient) {

			ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(this.getUuid());

			int blood = SanguinareMain.getBlood(player);
			boolean sanguinareStatus = SanguinareMain.getSanguinareStatus(player);

			if (sanguinareStatus && this.getWorld().isDay()) {
				float f = this.getBrightnessAtEyes();
				BlockPos blockPos = BlockPos.ofFloored(this.getX(), this.getEyeY(), this.getZ());
				boolean bl = this.isWet() || this.inPowderSnow || this.wasInPowderSnow;
				time++;
				if (f > 0.5F && time >= 4 && !bl && this.getWorld().isSkyVisible(blockPos)) {
					time = 0;
					this.damage(SanguinareDamageTypes.of(this.getWorld(), SanguinareDamageTypes.HOLY_BURN), 1.0f);
					this.timeUntilRegen = 0;
					for(int i = 0; i < 7; ++i) {
						DustParticles();
					}
				}
			}
		}
	}

	@Inject(at = @At("HEAD"), method = "requestRespawn")
	public void requestRespawn(CallbackInfo ci) {
		if (!this.getWorld().isClient) {

			ServerPlayerEntity player = this.getServer().getPlayerManager().getPlayer(this.getUuid());
			boolean sanguinareStatus = SanguinareMain.getSanguinareStatus(this.getServer().getPlayerManager().getPlayer(this.getUuid()));

			if (sanguinareStatus) {
				SanguinareMain.changeBlood(this.getWorld(), player, 20, true);
			}
		}
	}

	@Unique public void DustParticles() {
		double a = this.random.nextGaussian() * 0.01;
		double b = this.random.nextGaussian() * 0.01;
		double c = this.random.nextGaussian() * 0.01;
		((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMOKE, this.getParticleX(1), this.getRandomBodyY(), this.getParticleZ(1), 0, a, b, c, 1);
		((ServerWorld) this.getWorld()).spawnParticles(ParticleTypes.SMALL_FLAME, this.getParticleX(1), this.getRandomBodyY(), this.getParticleZ(1), 0, a * 2, b * 2, c * 2, 1);

	}
}