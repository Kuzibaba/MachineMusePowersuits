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

package lehjr.numina.common.capabilities.inventory.modechanging;

import com.mojang.blaze3d.vertex.PoseStack;
import lehjr.numina.client.render.NuminaRenderer;
import lehjr.numina.common.capabilities.NuminaCapabilities;
import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import lehjr.numina.common.capabilities.module.blockbreaking.IBlockBreakingModule;
import lehjr.numina.common.capabilities.module.miningenhancement.IMiningEnhancementModule;
import lehjr.numina.common.capabilities.module.rightclick.IRightClickModule;
import lehjr.numina.common.energy.ElectricItemUtils;
import lehjr.numina.common.math.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.overlay.ForgeGui;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:11 PM, 9/3/13
 *
 * Ported to Java by lehjr on 11/1/16.
 */
public interface IModeChangingItem extends IModularItem {
    @OnlyIn(Dist.CLIENT)
    @Nullable
    BakedModel getInventoryModel();

    @OnlyIn(Dist.CLIENT)
    default void drawModeChangeIcon(LocalPlayer player, int hotbarIndex, ForgeGui gui, Minecraft mc, PoseStack poseStack, float partialTick, int screenWidth, int screenHeight) {
        ItemStack module = getActiveModule();
        if (!module.isEmpty()) {
            double currX;
            double currY;

            int baroffset = 22;
            if (!player.isCreative()) {
                baroffset += 16;
                int totalArmorValue = player.getArmorValue();
                baroffset += 8 * (int) Math.ceil((double)totalArmorValue / 20); // 20 points per row @ 2 armor points per icon
            }
            baroffset = screenHeight - baroffset;
            currX = screenWidth / 2.0 - 89.0 + 20.0 * hotbarIndex;
            currY = baroffset - 18;
            Color.WHITE.setShaderColor();
            if (module.getCapability(NuminaCapabilities.POWER_MODULE).map(pm-> pm.isModuleOnline()).orElse(false)) {
                mc.getItemRenderer().renderGuiItem(module.getCapability(NuminaCapabilities.CHAMELEON).map(iChameleon -> iChameleon.getStackToRender()).orElse(module), (int)currX, (int)currY);
            } else {
                NuminaRenderer.drawModuleAt(poseStack, currX, currY, module.getCapability(NuminaCapabilities.CHAMELEON).map(iChameleon -> iChameleon.getStackToRender()).orElse(module), false);
            }
        }
    }

    List<Integer> getValidModes();

    boolean isValidMode(ResourceLocation mode);

    int getActiveMode();

    ItemStack getActiveModule();

    void setActiveMode(ResourceLocation moduleName);

    void setActiveMode(int newMode);

    boolean hasActiveModule(ResourceLocation regName);

    void cycleMode(Player player, int dMode);

    int nextMode();

    int prevMode();

    boolean isModuleActiveAndOnline(ResourceLocation moduleName);



    default boolean onBlockStartBreak(ItemStack itemstack, BlockPos pos, Player player) {
        return getActiveModule()
                .getCapability(NuminaCapabilities.POWER_MODULE)
                .filter(IMiningEnhancementModule.class::isInstance)
                .map(IMiningEnhancementModule.class::cast)
                .filter(pm ->pm.isModuleOnline())
                .map(pm ->pm.onBlockStartBreak(itemstack, pos, player))
                .orElse(false);
    }

    default int getUseDuration() {
        return getActiveModule().getCapability(NuminaCapabilities.POWER_MODULE)
                                .filter(IRightClickModule.class::isInstance)
                                .map(IRightClickModule.class::cast)
                                .map(m-> m.getModuleStack().getUseDuration())
                                .orElse(72000);
    }

    default boolean mineBlock(ItemStack powerFist, Level worldIn, BlockState state, BlockPos pos, LivingEntity entityLiving) {
        double playerEnergy = ElectricItemUtils.getPlayerEnergy(entityLiving);
        return getInstalledModulesOfType(IBlockBreakingModule.class).stream().anyMatch(module ->
                module.getCapability(NuminaCapabilities.POWER_MODULE)
                        .filter(IBlockBreakingModule.class::isInstance)
                        .map(IBlockBreakingModule.class::cast)
                        .map(pm-> pm.mineBlock(powerFist, worldIn, state, pos, entityLiving, playerEnergy))
                        .orElse(false));
    }

    default InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand, InteractionResultHolder<ItemStack> fallback) {
        ItemStack fist = player.getItemInHand(hand);
        return getActiveModule().
                getCapability(NuminaCapabilities.POWER_MODULE)
                .filter(IRightClickModule.class::isInstance)
                .map(IRightClickModule.class::cast)
                .map(rc-> rc.use(fist, level, player, hand)).orElse(fallback);
    }

   default InteractionResult onItemUseFirst(ItemStack itemStack, UseOnContext context, InteractionResult fallback) {
        return getActiveModule().getCapability(NuminaCapabilities.POWER_MODULE)
                            .filter(IRightClickModule.class::isInstance)
                            .map(IRightClickModule.class::cast)
                            .map(m-> m.onItemUseFirst(itemStack, context))
                            .orElse(fallback);
    }

    default float getDestroySpeed(ItemStack pStack, BlockState pState) {
        return getInstalledModules().stream()
                .filter(IBlockBreakingModule.class::isInstance)
                .map(IBlockBreakingModule.class::cast)
                .filter(pm->pm.getEmulatedTool().getDestroySpeed(pState) > 1.0F)
                .max(Comparator.comparing(pm->pm.getEmulatedTool().getDestroySpeed(pState)))
                .map(pm-> pm.getEmulatedTool().getDestroySpeed(pState)).orElse(1.0F);
    }

    default ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entity) {
        return getActiveModule().getCapability(NuminaCapabilities.POWER_MODULE)
                                .filter(IRightClickModule.class::isInstance)
                                .map(IRightClickModule.class::cast)
                                .map(m-> m.finishUsingItem(stack, worldIn, entity))
                                .orElse(stack);
    }

    default void releaseUsing(ItemStack stack, Level worldIn, LivingEntity entityLiving, int timeLeft) {
                getActiveModule().getCapability(NuminaCapabilities.POWER_MODULE)
                            .filter(IRightClickModule.class::isInstance)
                            .map(IRightClickModule.class::cast)
                            .ifPresent(m-> m.releaseUsing(stack, worldIn, entityLiving, timeLeft));
    }

    default InteractionResult useOn(UseOnContext context, InteractionResult fallback) {
            return getActiveModule().getCapability(NuminaCapabilities.POWER_MODULE)
                            .filter(IRightClickModule.class::isInstance)
                            .map(IRightClickModule.class::cast)
                            .map(m-> m.useOn(context)).orElse(fallback);
    }

    default boolean isCorrectToolForDrops(ItemStack itemStack, BlockState state) {
        return getInstalledModulesOfType(IBlockBreakingModule.class)
                .stream().anyMatch(module ->
                        module.getCapability(NuminaCapabilities.POWER_MODULE)
                                .filter(IBlockBreakingModule.class::isInstance)
                                .map(IBlockBreakingModule.class::cast)
                                .map(pm ->pm.getEmulatedTool().isCorrectToolForDrops(state)).orElse(false));
    }
}