package net.slayer.mixin.client;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.client.render.LightmapTextureManager;
import net.slayer.Config;
import net.slayer.SanguinareClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(LightmapTextureManager.class)
public class LightmapLunarSight {

    @ModifyReturnValue(method = "getBrightness", at = @At("RETURN"))
    private static float changeBrightnessifToggled(float original) {
        if (SanguinareClient.getSanguinareBooleans("sanguinareStatus") && SanguinareClient.getVisionToggle()) {
            return (original + Config.lunarEyesValue);
        } else {
            return original;
        }
    }
}
