package net.idothehax.playertrackercompass.mixin;

import net.idothehax.playertrackercompass.Playertrackercompass;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "dropItem(Lnet/minecraft/item/ItemStack;ZZ)Lnet/minecraft/entity/ItemEntity;",
            at = @At("HEAD"), cancellable = true)
    private void onDropItem(ItemStack stack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> cir) {
        if (stack.getItem() instanceof Playertrackercompass.TrackingCompass) {
            PlayerEntity player = (PlayerEntity)(Object)this;

            // Put the item back in the player's inventory
            player.getInventory().insertStack(stack.copy());

            // Optional: Notify the player that the compass can't be dropped
            if (player instanceof ServerPlayerEntity serverPlayer) {
                serverPlayer.sendMessage(Text.literal("The Tracker Compass Cannot be Dropped!")
                        .formatted(Formatting.RED), true);
            }

            // Return null to prevent the original drop
            cir.setReturnValue(null);
        }
    }
}