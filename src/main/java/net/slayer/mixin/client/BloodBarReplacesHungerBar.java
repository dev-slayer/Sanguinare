package net.slayer.mixin.client;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import net.slayer.SanguinareClient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class BloodBarReplacesHungerBar {

    // all hail LlamaLad7 bro..
    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", args = {"ldc=food"}, target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V"))
    private void tellStatusBarsSanguinareStatus(DrawContext context, CallbackInfo ci, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        argRef.set(SanguinareClient.getSanguinareBooleans("sanguinareStatus"));
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/DrawContext;drawTexture(Lnet/minecraft/util/Identifier;IIIIII)V"),
            slice = @Slice(from = @At(value = "INVOKE_STRING", args = {"ldc=food"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V"), to = @At(value = "INVOKE_STRING", args = {"ldc=air"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V")))
    private Identifier changeHungerTexturesToBlood(Identifier texture, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        if (argRef.get()) {
            return SanguinareClient.bloodbar;
        }
        return texture;
    }
}