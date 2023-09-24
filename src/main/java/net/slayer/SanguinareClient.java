package net.slayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.text.Text;
import net.slayer.client.BloodBar;

public class SanguinareClient implements ClientModInitializer {

	public static PlayerData playerData = new PlayerData();

	static int bloodSync = 0;
	static boolean sanguinareStatusSync = false;

	@Override
	public void onInitializeClient() {

		HudRenderCallback.EVENT.register(new BloodBar());
		ClientPlayNetworking.registerGlobalReceiver(SanguinareMain.BLOOD_UPDATED,
				(client, handler, buf, responseSender) -> {
					int blood = buf.readInt();
					bloodSync = blood;
					client.execute(() -> {
					});
				});

		ClientPlayNetworking.registerGlobalReceiver(SanguinareMain.SANGUINARE_UPDATED,
				(client, handler, buf, responseSender) -> {
					boolean sanguinareStatus = buf.readBoolean();
					sanguinareStatusSync = sanguinareStatus;

					client.execute(() -> {
						client.player.sendMessage(Text.literal("status: " + sanguinareStatus));
					});
				});

		ClientPlayNetworking.registerGlobalReceiver(SanguinareMain.INITIAL_SYNC,
				(client, handler, buf, responseSender) -> {
					playerData.blood = buf.readInt();
					playerData.sanguinareStatus = buf.readBoolean();

					client.execute(() -> {
						client.player.sendMessage(Text.literal("Client-Side blood: " + playerData.blood));
						client.player.sendMessage(
								Text.literal("Client-Side sanguinareStatus: " + playerData.sanguinareStatus));
					});
				});
	}

	public static int getSanguinareInts(String prop) {
		switch (prop) {
			case "blood":
				return bloodSync;
		}
		return 0;
	}

	public static boolean getSanguinareBooleans(String prop) {
		switch (prop) {
			case "sanguinareStatus":
				return sanguinareStatusSync;
		}
		return false;
	}
}