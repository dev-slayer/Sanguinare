package net.slayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.slayer.effects.SanguinareEffects;
import net.slayer.item.SanguinareItems;
import org.lwjgl.glfw.GLFW;

public class SanguinareClient implements ClientModInitializer {

	public static PlayerData playerData = new PlayerData();

	static boolean sanguinareStatusSync = false;
	static boolean isTargeting = false;
	static float targetHealth = 0;
	static boolean hasBloodthirst = false;

	public static final Identifier BLOOD_EMPTY_TEXTURE = new Identifier(SanguinareMain.MOD_ID, "hud/blood/empty");
	public static final Identifier BLOOD_HALF_TEXTURE = new Identifier(SanguinareMain.MOD_ID, "hud/blood/half");
	public static final Identifier BLOOD_FULL_TEXTURE = new Identifier(SanguinareMain.MOD_ID, "hud/blood/full");

	public static final Identifier BLOODTHIRST_BLOOD_EMPTY_TEXTURE = new Identifier(SanguinareMain.MOD_ID, "hud/blood/bloodthirst/empty");
	public static final Identifier BLOODTHIRST_BLOOD_HALF_TEXTURE = new Identifier(SanguinareMain.MOD_ID, "hud/blood/bloodthirst/half");
	public static final Identifier BLOODTHIRST_BLOOD_FULL_TEXTURE = new Identifier(SanguinareMain.MOD_ID, "hud/blood/bloodthirst/full");

	public static final Identifier BACKGROUND_FANGS_TEXTURE = new Identifier(SanguinareMain.MOD_ID,"hud/fangs/background");
	public static final Identifier FANGS_BLOOD_0 = new Identifier(SanguinareMain.MOD_ID, "hud/fangs/blood_0");
	public static final Identifier FANGS_BLOOD_1 = new Identifier(SanguinareMain.MOD_ID, "hud/fangs/blood_1");
	public static final Identifier FANGS_BLOOD_2 = new Identifier(SanguinareMain.MOD_ID, "hud/fangs/blood_2");
	public static final Identifier FANGS_BLOOD_3 = new Identifier(SanguinareMain.MOD_ID, "hud/fangs/blood_3");
	public static final Identifier FANGS_BLOOD_4 = new Identifier(SanguinareMain.MOD_ID, "hud/fangs/blood_4");


	private static KeyBinding suckKey;
	private static KeyBinding toggleNightVisionKey;

	private int suckCooldown = 0;
	private static boolean nightVisionToggle = false;

	public void onInitializeClient() {

		suckKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.sanguinare.suck",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_V,
				"category.sanguinare"));
		toggleNightVisionKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
				"key.sanguinare.toggle_night_vision",
				InputUtil.Type.KEYSYM,
				GLFW.GLFW_KEY_N,
				"category.sanguinare"));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			if (client.player != null) {
				if (client.player.hasStatusEffect(SanguinareEffects.BLOODTHIRST)) {
					hasBloodthirst = true;
				}
				if (client.crosshairTarget != null) {
					if (getSanguinareBooleans("sanguinareStatus") && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
						EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
						Entity entity = entityHit.getEntity();
						isTargeting = entity.getType().isIn(SanguinareMain.ACCEPTABLE_VICTIMS) && entity.isAlive();
						if (isTargeting) {
							targetHealth = (((LivingEntity) entity).getHealth()) / (((LivingEntity) entity).getMaxHealth());
						} else {
							targetHealth = 0;
						}
					}
					if (client.crosshairTarget.getType() == HitResult.Type.BLOCK || client.crosshairTarget.getType() == HitResult.Type.MISS) {
						isTargeting = false;
					}
				}
			}
			if (suckKey.isPressed()) {
				if (client.crosshairTarget != null && client.player != null) {
					if (getSanguinareBooleans("sanguinareStatus") && client.crosshairTarget.getType() == HitResult.Type.ENTITY) {
						EntityHitResult entityHit = (EntityHitResult) client.crosshairTarget;
						Entity entity = entityHit.getEntity();
						if (entity.getType().isIn(SanguinareMain.ACCEPTABLE_VICTIMS) && entity.isAlive()) {
							this.suckCooldown--;
							if (this.suckCooldown <= 0) {
								this.suckCooldown = Config.drinkCooldown;
								PacketByteBuf data = PacketByteBufs.create();
								data.writeUuid(entity.getUuid());
								if (client.player.getHungerManager().getFoodLevel() < 20) {
									ClientPlayNetworking.send(SanguinareMain.SUCKING_ID, data);
								} else if (client.player.getStackInHand(Hand.MAIN_HAND).isOf(Items.GLASS_BOTTLE) || client.player.getStackInHand(Hand.MAIN_HAND).isOf(SanguinareItems.BLOOD_BOTTLE)) {
									ClientPlayNetworking.send(SanguinareMain.BOTTLING_ID, data);}
							}
						}
					}
				}
			}
			if (toggleNightVisionKey.wasPressed()) {
				nightVisionToggle = !nightVisionToggle;
				if (Config.debug) {
					client.player.sendMessage(Text.literal("night vision: " + nightVisionToggle));
				}
			}
		});

		ClientPlayNetworking.registerGlobalReceiver(SanguinareMain.SANGUINARE_UPDATED,
				(client, handler, buf, responseSender) -> {
					boolean sanguinareStatus = buf.readBoolean();
					sanguinareStatusSync = sanguinareStatus;
				});

		ClientPlayNetworking.registerGlobalReceiver(SanguinareMain.INITIAL_SYNC,
				(client, handler, buf, responseSender) -> {
					playerData.sanguinareStatus = buf.readBoolean();
				});
	}

	public static boolean getSanguinareBooleans(String prop) {
		return switch (prop) {
			case "sanguinareStatus" -> sanguinareStatusSync;
			case "isTargeting" -> isTargeting;
			case "hasBloodthirst" -> hasBloodthirst;
			default -> false;
		};
	}

	public static float getTargetHealth() {
		return targetHealth;
	}

	public static boolean getVisionToggle() {
		return nightVisionToggle;
	}
}