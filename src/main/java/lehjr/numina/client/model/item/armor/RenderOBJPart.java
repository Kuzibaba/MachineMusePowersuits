/*
 * Copyright (c) 2021. MachineMuse, Lehjr
 *  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *      Redistributions of source code must retain the above copyright notice, this
 *      list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 *  ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package lehjr.numina.client.model.item.armor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import com.mojang.math.Vector4f;
import lehjr.numina.common.capabilities.render.modelspec.ObjlPartSpec;
import lehjr.numina.common.math.Color;
import lehjr.numina.common.math.MathUtils;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.TransformationHelper;
import org.lwjgl.system.MemoryStack;

import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 4:16 AM, 29/04/13
 * <p>
 * Ported to Java by lehjr on 11/6/16.
 */
@OnlyIn(Dist.CLIENT)
public class RenderOBJPart extends ModelPart {
    public float xOffset = 0;
    public float yOffset = 0;
    public float zOffset = 0;
    public float xRotOffset = 0;
    public float yRotOffset = 0;
    public float zRotOffset = 0;
    final int FULL_BRIGHTNESS = 0XF000F0; // 15728880 also used in vanilla rendering item in gui

    // replace division operation with multiplication
    final float div255 = 0.003921569F;
    ModelPart parent;

    /***
     * 1.16.5 used the Consumer interface to set the parent which in turn was used to set the cubes
     * @param base
     * @param parent
     */
    public RenderOBJPart(Model base, ModelPart parent) {
        super(new ArrayList<>(), new HashMap<>());
        this.parent = parent;
    }

    @Override
    public void copyFrom(ModelPart pModelPart) {
        this.xScale = pModelPart.xScale;
        this.yScale = pModelPart.yScale;
        this.zScale = pModelPart.zScale;
        this.xRot = pModelPart.xRot + this.xRotOffset;
        this.yRot = pModelPart.yRot + this.yRotOffset;
        this.zRot = pModelPart.zRot + this.zRotOffset;
        this.x = pModelPart.x + this.xOffset;
        this.y = pModelPart.y + this.yOffset;
        this.z = pModelPart.z + this.zOffset;
    }

    public void render(ObjlPartSpec partSpec, CompoundTag tag, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, Color color) {
        partSpec.getPart().ifPresent(objBakedPart -> {
            if (this.visible) {
                ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();
                RandomSource random = RandomSource.create(42L);
                builder.addAll(objBakedPart.getQuads(null, null, random));

                poseStack.pushPose();
                translateAndRotate(poseStack);

                renderQuads(poseStack.last(),
                        buffer,
                        builder.build(),
                        packedLight,
                        packedOverlay,
                        color.getARGBInt());
                poseStack.popPose();
            }
        });
    }

    @Override
    public void translateAndRotate(PoseStack matrixStackIn) {
        matrixStackIn.translate(0,  - yOffset * MathUtils.DIV_16F, 0);
        super.translateAndRotate(matrixStackIn);
        matrixStackIn.mulPose(TransformationHelper.quatFromXYZ(new Vector3f(180, 0, 0), true));
        matrixStackIn.translate(0,  - yOffset * MathUtils.DIV_16F, 0);
    }

    public void renderQuads(PoseStack.Pose entry,
                            VertexConsumer bufferIn,
                            List<BakedQuad> quadsIn,
                            int combinedLightIn,
                            int combinedOverlayIn,
                            int color) {
        float a = (float) (color >> 24 & 255) * div255;
        float r = (float) (color >> 16 & 255) * div255;
        float g = (float) (color >> 8 & 255) * div255;
        float b = (float) (color & 255) * div255;

        for (BakedQuad bakedquad : quadsIn) {
            addVertexData(bufferIn, entry, bakedquad, combinedLightIn, combinedOverlayIn, r, g, b, a);
        }
    }

    // Copy of addQuad with alpha support
    void addVertexData(VertexConsumer bufferIn,
                       PoseStack.Pose matrixEntry,
                       BakedQuad bakedQuad,
                       int lightmapCoordIn,
                       int overlayCoords, float red, float green, float blue, float alpha) {
        int[] aint = bakedQuad.getVertices();
        Vec3i faceNormal = bakedQuad.getDirection().getNormal();
        Vector3f normal = new Vector3f((float) faceNormal.getX(), (float) faceNormal.getY(), (float) faceNormal.getZ());
        Matrix4f matrix4f = matrixEntry.pose();// same as TexturedQuad renderer
        normal.transform(matrixEntry.normal()); // normals different here

        float scale = 0.0625F;

        int intSize = DefaultVertexFormat.BLOCK.getIntegerSize();
//        int intSize = DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP.getIntegerSize();

        int vertexCount = aint.length / intSize;

        try (MemoryStack memorystack = MemoryStack.stackPush()) {
            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.BLOCK.getVertexSize());
//            ByteBuffer bytebuffer = memorystack.malloc(DefaultVertexFormat.POSITION_COLOR_TEX_LIGHTMAP.getVertexSize());
            IntBuffer intbuffer = bytebuffer.asIntBuffer();

            for (int vert = 0; vert < vertexCount; ++vert) {
                ((Buffer) intbuffer).clear();
                intbuffer.put(aint, vert * 8, 8);
                float x = bytebuffer.getFloat(0);
                float y = bytebuffer.getFloat(4);
                float z = bytebuffer.getFloat(8);
                int lightmapCoord = bufferIn.applyBakedLighting(lightmapCoordIn, bytebuffer);
                float u = bytebuffer.getFloat(16);
                float v = bytebuffer.getFloat(20);

                /** scaled like TexturedQuads, but using multiplication instead of division due to speed advantage.  */
                Vector4f pos = new Vector4f(x * scale, y * scale, z * scale, 1.0F); // scales to 1/16 like the TexturedQuads but with multiplication (faster than division)
                pos.transform(matrix4f);
                bufferIn.applyBakedNormals(normal, bytebuffer, matrixEntry.normal());
                bufferIn.vertex(pos.x(), pos.y(), pos.z(), red, green, blue, alpha, u, v, overlayCoords, lightmapCoord, normal.x(), normal.y(), normal.z());
            }
        }
    }

    @Override
    public void render(PoseStack pPoseStack, VertexConsumer pVertexConsumer, int pPackedLight, int pPackedOverlay) {
    }

    @Override
    public void render(PoseStack matrixStackIn, VertexConsumer bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
    }
}