package net.slayer.mixin;

import com.llamalad7.mixinextras.injector.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.slayer.SanguinareClient;
import net.slayer.SanguinareMain;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(HungerManager.class)
public class SanguinareNormalFeatureDisabling {

    @WrapWithCondition(
            method = "update",
            at = @At(value = "INVOKE", target = "net/minecraft/entity/player/PlayerEntity.heal (F)V")
    )
    private boolean disableRegenIfSanguinare(PlayerEntity instance, float amount) {
        return !SanguinareMain.getSanguinareStatus((ServerPlayerEntity) instance);
    }

    @Inject(method = "eat", at = @At(value = "HEAD"))
    private void tellStatusBarsSanguinareStatus(Item item, ItemStack stack, CallbackInfo ci, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        argRef.set(SanguinareClient.getSanguinareBooleans("sanguinareStatus"));
    }
    @WrapWithCondition(
            method = "eat",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/HungerManager;add(IF)V")
    )
    private boolean disableEatingIfSanguinare(HungerManager manager, int food, float saturationValue, Item item, ItemStack stack, @Share("sanguinareStatus") LocalBooleanRef argRef) {
        return !argRef.get();
    }
}
