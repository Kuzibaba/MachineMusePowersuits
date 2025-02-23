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

package lehjr.numina.common.capabilities.render;

import lehjr.numina.common.constants.TagConstants;
import lehjr.numina.common.tags.TagUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class ModelSpecStorage implements IModelSpec, INBTSerializable<CompoundTag> {
    ItemStack itemStack;

    public ModelSpecStorage(@Nonnull ItemStack itemStackIn){
        this.itemStack = itemStackIn;
    }

    @Nonnull
    @Override
    public ItemStack getItemStack() {
        return this.itemStack;
    }

    @Override
    public CompoundTag setRenderTag(CompoundTag renderDataIn, String tagName) {
        CompoundTag itemTag = TagUtils.getMuseModularItemTag(itemStack);
//        NuminaLogger.logDebug("ItemTag before: " + itemTag);
//        NuminaLogger.logDebug("full tag data before: " + itemStack.serializeNBT());
        if (tagName != null) {
            if (Objects.equals(tagName, TagConstants.RENDER)) {
//                NuminaLogger.logger.debug("Removing render tag");
                itemTag.remove(TagConstants.RENDER);
                if (!renderDataIn.isEmpty()) {
//                    NuminaLogger.logger.debug("Adding tag " + TagConstants.RENDER + " : " + renderDataIn);
                    itemTag.put(TagConstants.RENDER, renderDataIn);
                } else {
                    itemTag.put(TagConstants.RENDER, new CompoundTag());
                    setColorArray(new int[]{-1});
                }
            } else {
                CompoundTag renderTag;
                if (!itemTag.contains(TagConstants.RENDER)) {
                    renderTag = new CompoundTag();
                } else {
                    renderTag = itemTag.getCompound(TagConstants.RENDER);
                }
                if (renderDataIn.isEmpty()) {
//                    NuminaLogger.logger.debug("Removing tag " + tagName);
                    renderTag.remove(tagName);
                    renderTag.remove(tagName.replace(".", ""));
                } else {
//                    NuminaLogger.logger.debug("Adding tag " + tagName + " : " + renderDataIn);
                    renderTag.put(tagName, renderDataIn);
                }

                itemTag.put(TagConstants.RENDER, renderTag);
            }
        }
        CompoundTag stackTag = itemStack.getTag();
        stackTag.put(TagConstants.TAG_ITEM_PREFIX, itemTag);
        itemStack.setTag(stackTag);
        return getRenderTag();
    }

    @Override
    @Nullable
    public CompoundTag getRenderTag() {
        CompoundTag itemTag = TagUtils.getMuseModularItemTag(itemStack);
        return itemTag.getCompound(TagConstants.RENDER);
    }

    @Override
    public CompoundTag getDefaultRenderTag() {
        return new CompoundTag();
    }

    /**
     * When dealing with possibly multiple specs and color lists, new list needs to be created, since there is only one list per item.
     */
    @Override
    public List<Integer> addNewColorstoList(List<Integer> colors, List<Integer> colorsToAdd) {
        for (Integer i : colorsToAdd) {
            if (!colors.contains(i))
                colors.add(i);
        }
        return colors;
    }

    @Override
    public int[] getColorArray() {
        return  getRenderTag().getIntArray(TagConstants.COLORS);
    }


    /**
     * new array means setting a new array index for the same getValue
     */
    @Override
    public int getNewColorIndex(List<Integer> colors, List<Integer> oldColors, Integer index) {
        return colors.indexOf(oldColors.get(index != null ? index : 0));
    }

    @Override
    public CompoundTag setColorArray(int[] colors) {
        getRenderTag().putIntArray(TagConstants.COLORS, colors);
        return getRenderTag();
    }

    // INBTSerializable<CompoundTag> ----------------------------------------------------------------------------------
    @Override
    public CompoundTag serializeNBT() {
        return getRenderTag();
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        setRenderTag(nbt, TagConstants.RENDER);
    }
}