package net.slayer.mixin.client;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.util.Identifier;
import net.slayer.SanguinareClient;
import net.slayer.SanguinareMain;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(InGameHud.class)
public class SanguinareHudReplacements {

    @Shadow private int scaledWidth;
    @Shadow private int scaledHeight;

    @Shadow @Final private static Identifier CROSSHAIR_TEXTURE;

    // all hail LlamaLad7 bro..

    @Inject(method = "renderStatusBars", at = @At(value = "INVOKE_STRING", args = {"ldc=food"}, target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V"))
    private void tellStatusBarsSanguinareStatus(DrawContext context, CallbackInfo ci, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        argRef.set(SanguinareClient.getSanguinareBooleans("sanguinareStatus"));
        if (argRef.get()) {
            float targetHealth = SanguinareClient.getTargetHealth();
            Identifier bloodAmount =
                (targetHealth >= .8) ? SanguinareClient.FANGS_BLOOD_0 :
                (targetHealth >= .6) ? SanguinareClient.FANGS_BLOOD_1 :
                (targetHealth >= .4) ? SanguinareClient.FANGS_BLOOD_2 :
                (targetHealth >= .2) ? SanguinareClient.FANGS_BLOOD_3 :
                (targetHealth > 0) ? SanguinareClient.FANGS_BLOOD_4 : null;
            if (SanguinareClient.getSanguinareBooleans("isTargeting") && bloodAmount != null) {
                context.drawGuiTexture(bloodAmount, (this.scaledWidth - 15) / 2, (this.scaledHeight - 15) / 2, 15, 15);
            }
        }
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIII)V"),
            slice = @Slice(from = @At(value = "INVOKE_STRING", args = {"ldc=food"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V"), to = @At(value = "INVOKE_STRING", args = {"ldc=air"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V")))
    private Identifier changeEmptyHungerTexturesToBlood(Identifier texture, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        if (argRef.get()) {
            if (SanguinareClient.getSanguinareBooleans("hasBloodthirst")) {
                return SanguinareClient.BLOODTHIRST_BLOOD_EMPTY_TEXTURE;
            } else {
                return SanguinareClient.BLOOD_EMPTY_TEXTURE;
            }
        }
        return texture;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 1, target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIII)V"),
            slice = @Slice(from = @At(value = "INVOKE_STRING", args = {"ldc=food"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V"), to = @At(value = "INVOKE_STRING", args = {"ldc=air"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V")))
    private Identifier changeHalfHungerTexturesToBlood(Identifier texture, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        if (argRef.get()) {
            if (SanguinareClient.getSanguinareBooleans("hasBloodthirst")) {
                return SanguinareClient.BLOODTHIRST_BLOOD_FULL_TEXTURE;
            } else {
                return SanguinareClient.BLOOD_FULL_TEXTURE;
            }
        }
        return texture;
    }

    @ModifyArg(method = "renderStatusBars", at = @At(value = "INVOKE", ordinal = 2, target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIII)V"),
            slice = @Slice(from = @At(value = "INVOKE_STRING", args = {"ldc=food"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V"), to = @At(value = "INVOKE_STRING", args = {"ldc=air"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V")))
    private Identifier changeFullHungerTexturesToBlood(Identifier texture, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        if (argRef.get()) {
            if (SanguinareClient.getSanguinareBooleans("hasBloodthirst")) {
                return SanguinareClient.BLOODTHIRST_BLOOD_HALF_TEXTURE;
            } else {
                return SanguinareClient.BLOOD_HALF_TEXTURE;
            }
        }
        return texture;
    }

    @WrapWithCondition(method = "renderStatusBars", at = @At(value = "INVOKE", target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIII)V"),
            slice = @Slice(from = @At(value = "INVOKE_STRING", args = {"ldc=air"},
                    target = "net/minecraft/util/profiler/Profiler.swap (Ljava/lang/String;)V")))
    private boolean removeDrowningifSanguinare(DrawContext context, Identifier texture, int x, int y, int width, int height, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        return !argRef.get();
    }

    @ModifyArg(method = "renderCrosshair", at = @At(value = "INVOKE", ordinal = 0, target = "net/minecraft/client/gui/DrawContext.drawGuiTexture (Lnet/minecraft/util/Identifier;IIII)V"))
    private Identifier changeCrosshairToFangs(Identifier texture, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        if (SanguinareClient.getSanguinareBooleans("sanguinareStatus") && SanguinareClient.getSanguinareBooleans("isTargeting")) {
            return SanguinareClient.BACKGROUND_FANGS_TEXTURE;
        }
        return texture;
    }
}