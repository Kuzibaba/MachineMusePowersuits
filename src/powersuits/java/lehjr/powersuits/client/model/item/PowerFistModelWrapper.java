package lehjr.powersuits.client.model.item;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraftforge.client.model.BakedModelWrapper;

/**
 * Allows use of updated version of original model
 */
public class PowerFistModelWrapper extends BakedModelWrapper {
    public PowerFistModelWrapper(BakedModel originalModel) {
        super(originalModel);
    }

    @Override
    public BakedModel applyTransform(ItemTransforms.TransformType cameraTransformType, PoseStack poseStack, boolean applyLeftHandTransform) {
        if (cameraTransformType == ItemTransforms.TransformType.GUI) {
            return originalModel.applyTransform(cameraTransformType, poseStack, applyLeftHandTransform);
        }
        originalModel.getTransforms().getTransform(cameraTransformType).apply(applyLeftHandTransform, poseStack);
        return this;
    }
    @Override
    public boolean isCustomRenderer() {
        return true;
    }
}