package net.slayer;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class SanguinareClient implements ClientModInitializer {

	public static PlayerData playerData = new PlayerData();

	static boolean sanguinareStatusSync = false;

	public static final Identifier bloodbar = new Identifier(SanguinareMain.MOD_ID, "textures/blood/icons.png");

	@Override
	public void onInitializeClient() {

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
					playerData.sanguinareStatus = buf.readBoolean();

					client.execute(() -> {
						client.player.sendMessage(Text.literal("Client-Side sanguinareStatus: " + playerData.sanguinareStatus));
					});
				});
	}

	public static boolean getSanguinareBooleans(String prop) {
		switch (prop) {
			case "sanguinareStatus":
				return sanguinareStatusSync;
		}
		return false;
	}
}