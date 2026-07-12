package com.aqupd.customgui.mixin;

import com.aqupd.customgui.CustomHandGui;
import com.aqupd.customgui.util.Configuration;
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
 * NOTE (compatibility): the exact method name/signature of
 * HeldItemRenderer#renderFirstPersonItem below matches Yarn mappings for
 * Minecraft 1.21.1 (yarn 1.21.1+build.3). If Fabric Loom reports a
 * "method not found" error when you compile this, open
 * HeldItemRenderer.class in your IDE (with mappings applied) and adjust
 * the @Inject `method` string to match - Minecraft occasionally renames
 * this method between minor versions.
 */
@Mixin(HeldItemRenderer.class)
public class MixinHeldItemRenderer {

    @Inject(
        method = "renderFirstPersonItem(Lnet/minecraft/client/network/AbstractClientPlayerEntity;FFLnet/minecraft/util/Hand;FLnet/minecraft/item/ItemStack;FLnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
        at = @At("HEAD")
    )
    private void customgui$renderFirstPersonItem(
            AbstractClientPlayerEntity player, float tickDelta, float pitch, Hand hand,
            float swingProgress, ItemStack item, float equipProgress,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            CallbackInfo ci) {

        // Work out which physical arm is being rendered (left or right side
        // of the screen), same distinction the original mod made via
        // EnumHandSide.
        Arm mainArm = player.getMainArm();
        boolean renderingLeftArm = (hand == Hand.MAIN_HAND)
                ? mainArm == Arm.LEFT
                : mainArm != Arm.LEFT;

        CustomHandGui.isLeftHand = renderingLeftArm;

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
}
