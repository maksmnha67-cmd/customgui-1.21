package com.shyz.customgui.mixin;

import com.shyz.customgui.CustomHandGui;
import com.shyz.customgui.util.Configuration;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.math.RotationAxis;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Ported from the original 1.12.2 MixinItemRenderer.
 *
 * The offset/rotation is wrapped in its own push()/pop() pair so it can
 * never leak into the render call for the other hand (this was the cause
 * of moving one hand also affecting the other).
 */
@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    private static final String TARGET =
        "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V";

    @Inject(method = TARGET, at = @At("HEAD"))
    private void customgui$pushAndOffset(
            AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
            float swingProgress, ItemStack item, float equipProgress,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            CallbackInfo ci) {

        Arm mainArm = player.getMainArm();
        boolean renderingLeftArm = (hand == Hand.MAIN_HAND)
                ? mainArm == Arm.LEFT
                : mainArm != Arm.LEFT;

        CustomHandGui.isLeftHand = renderingLeftArm;

        matrices.push();

        if (renderingLeftArm) {
            matrices.translate(Configuration.x2Gui, Configuration.y2Gui, Configuration.z2Gui);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Configuration.x2Rot));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Configuration.y2Rot));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Configuration.z2Rot));
        } else {
            matrices.translate(Configuration.x1Gui, Configuration.y1Gui, Configuration.z1Gui);
            matrices.multiply(RotationAxis.POSITIVE_X.rotationDegrees(Configuration.x1Rot));
            matrices.multiply(RotationAxis.POSITIVE_Y.rotationDegrees(Configuration.y1Rot));
            matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(Configuration.z1Rot));
        }
    }

    @Inject(method = TARGET, at = @At("TAIL"))
    private void customgui$pop(
            AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
            float swingProgress, ItemStack item, float equipProgress,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            CallbackInfo ci) {
        matrices.pop();
    }
}
