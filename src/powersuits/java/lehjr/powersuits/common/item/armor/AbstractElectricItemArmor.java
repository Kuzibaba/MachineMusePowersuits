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

package lehjr.powersuits.common.item.armor;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.google.common.util.concurrent.AtomicDouble;
import lehjr.numina.client.model.item.armor.ArmorModelInstance;
import lehjr.numina.client.model.item.armor.HighPolyArmor;
import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import lehjr.numina.common.capabilities.module.powermodule.IPowerModule;
import lehjr.numina.common.capabilities.module.powermodule.ModuleCategory;
import lehjr.numina.common.capabilities.module.powermodule.PowerModuleCapability;
import lehjr.numina.common.capabilities.module.toggleable.IToggleableModule;
import lehjr.numina.common.capabilities.render.IArmorModelSpecNBT;
import lehjr.numina.common.capabilities.render.ModelSpecNBTCapability;
import lehjr.numina.common.capabilities.render.modelspec.EnumSpecType;
import lehjr.numina.common.constants.NuminaConstants;
import lehjr.numina.common.energy.ElectricItemUtils;
import lehjr.numina.common.network.NuminaPackets;
import lehjr.numina.common.network.packets.CosmeticInfoPacket;
import lehjr.numina.common.string.AdditionalInfo;
import lehjr.powersuits.common.base.MPSObjects;
import lehjr.powersuits.common.capability.PowerArmorCap;
import lehjr.powersuits.common.constants.MPSConstants;
import lehjr.powersuits.common.constants.MPSRegistryNames;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.texture.AtlasTexture;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.ai.attributes.Attribute;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public abstract class AbstractElectricItemArmor extends ArmorItem {
    public static final UUID[] ARMOR_MODIFIERS = new UUID[]{
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID(),
            UUID.randomUUID()};

    public AbstractElectricItemArmor(EquipmentSlotType slots, Properties builder) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, builder);
    }

    public AbstractElectricItemArmor(EquipmentSlotType slots) {
        super(MPAArmorMaterial.EMPTY_ARMOR, slots, new Item.Properties()
                .stacksTo(1)
                .tab(MPSObjects.creativeTab)
                .durability(0)
                .setNoRepair());
    }

    @Override
    public boolean makesPiglinsNeutral(ItemStack stack, LivingEntity wearer) {
        return stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(IModularItem.class::isInstance)
                .map(IModularItem.class::cast)
                .map(iModularItem -> iModularItem.isModuleOnline(MPSRegistryNames.PIGLIN_PACIFICATION_MODULE)).orElse(false);
    }

    //    /*
//        returning a value higher than 0 is applied damage to the armor, even if the armor is not setup to take damage.
//        TODO: does this even work???? Is this even needed?
//
//      */
    @Override
    public <T extends LivingEntity> int damageItem(ItemStack stack, int amount, T entity, Consumer<T> onBroken) {
        // FIXME: this should probably factor in the damage value

        int enerConsum = (int) Math.round(stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(IModularItem.class::isInstance)
                .map(IModularItem.class::cast)
                .map(iItemHandler -> {
                    Pair<Integer, Integer> range = iItemHandler.getRangeForCategory(ModuleCategory.ARMOR);
                    double energyUsed = 0;
                    for (int x = range.getKey(); x < range.getRight(); x ++) {
                        energyUsed += iItemHandler.getStackInSlot(x).getCapability(PowerModuleCapability.POWER_MODULE)
                                .map(pm->pm.applyPropertyModifiers(MPSConstants.ARMOR_ENERGY_CONSUMPTION)).orElse(0D);
                    }
                    return energyUsed;
                }).orElse(0D));

        // protects as long as there is energy to drain I guess
        if (enerConsum > 0 && entity instanceof LivingEntity) {
            ElectricItemUtils.drainPlayerEnergy(entity, enerConsum);
        }
        return 0;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlotType slot, ItemStack stack) {
        // PropertyModifier tags applied directly to armor will disable the ItemStack sensitive version
        stack.removeTagKey("AttributeModifiers");

        Multimap<Attribute, AttributeModifier> multimap = super.getAttributeModifiers(slot, stack);
        EquipmentSlotType slotType = MobEntity.getEquipmentSlotForItem(stack);
        if (slot != slotType) {
            return multimap;
        }

        AtomicDouble armorVal = new AtomicDouble(0);
        AtomicDouble toughnessVal = new AtomicDouble(0);
        AtomicDouble knockbackResistance = new AtomicDouble(0);
        AtomicDouble speed = new AtomicDouble(0);
        AtomicDouble movementResistance = new AtomicDouble(0);
        AtomicDouble swimBoost = new AtomicDouble(0);

        stack.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .filter(IModularItem.class::isInstance)
                .map(IModularItem.class::cast)
                .ifPresent(iItemHandler -> {

                    // Armor **should** only occupy one slot
                    Pair<Integer, Integer> range = iItemHandler.getRangeForCategory(ModuleCategory.ARMOR);
                    if (range != null) {
                        for (int i = range.getLeft(); i < range.getRight(); i++) {
                            iItemHandler.getStackInSlot(i).getCapability(PowerModuleCapability.POWER_MODULE).ifPresent(pm -> {
                                if (pm.isAllowed()) {
                                    // physical armor and hybrid energy/physical armor
                                    double armorDouble = pm.applyPropertyModifiers(MPSConstants.ARMOR_VALUE_PHYSICAL);
                                    double knockBack = 0;

                                    if (pm instanceof IToggleableModule && pm.isModuleOnline()) {
                                        armorDouble += pm.applyPropertyModifiers(MPSConstants.ARMOR_VALUE_ENERGY);
                                    }

                                    if (armorDouble > 0) {
                                        armorVal.getAndAdd(armorDouble);
                                        knockbackResistance.getAndAdd(pm.applyPropertyModifiers(MPSConstants.KNOCKBACK_RESISTANCE));
                                        toughnessVal.addAndGet(pm.applyPropertyModifiers(MPSConstants.ARMOR_TOUGHNESS));

                                        /*
                                        toughness for diamond = 2 per
                                        */



                                    }
                                }
                            });
                        }
                    }

                    if (slotType == EquipmentSlotType.LEGS) {
                        for(int i= 0; i < iItemHandler.getSlots(); i++) {
                            /** Note: attribute should already be removed when module is offline */
                            iItemHandler.getStackInSlot(i).getCapability(PowerModuleCapability.POWER_MODULE)
                                    .filter(IPowerModule.class::isInstance)
                                    .map(IPowerModule.class::cast)
                                    .filter(IPowerModule::isModuleOnline)
                                    .ifPresent(iPowerModule -> {
                                        movementResistance.getAndAdd( iPowerModule.applyPropertyModifiers(MPSConstants.MOVEMENT_RESISTANCE));
                                        iPowerModule.getModuleStack().getAttributeModifiers(slotType).get(Attributes.MOVEMENT_SPEED).forEach(attributeModifier -> speed.getAndAdd(attributeModifier.getAmount()));
                                        iPowerModule.getModuleStack().getAttributeModifiers(slotType).get(ForgeMod.SWIM_SPEED.get()).forEach(attributeModifier -> {
                                            swimBoost.getAndAdd(attributeModifier.getAmount());
                                        });
                                    });
                        }
                    }
                });

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();

        for (Map.Entry entry : multimap.entries()) {
            builder.put((Attribute)entry.getKey(), (AttributeModifier)entry.getValue());
        }

        if (armorVal.get() > 0) {
            builder.put(Attributes.ARMOR, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor modifier", armorVal.get(), AttributeModifier.Operation.ADDITION));
        }

        if (knockbackResistance.get() > 0) {
            builder.put(Attributes.KNOCKBACK_RESISTANCE, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Knockback resistance", knockbackResistance.get(), AttributeModifier.Operation.ADDITION));
        }

        if (toughnessVal.get() > 0) {
            builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()], "Armor toughness", toughnessVal.get(), AttributeModifier.Operation.ADDITION));
        }

        if (speed.get() != 0 || movementResistance.get() != 0) {
            /*
                --------------------------
                kinetic gen max speed hit: 0.025 (total walking speed: 0.075) ( total running speed: 0.08775)
                --------------------------

                -----------------------
                sprint max speed boost: 0.1625 ( total: 0.34125 )
                ----------------------

                walking max speed boost: 0.1 ( total: 0.2 )
                -----------------------

                vanilla walk speed:
                -------------------
                0.13

                vanilla sprint speed:
                ---------------------
                0.1

                resistance should be up to about 80% of walking speed or 0.08
             */

            builder.put(Attributes.MOVEMENT_SPEED,
                    new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()],
                            Attributes.MOVEMENT_SPEED.getDescriptionId(),
                            (speed.get() - movementResistance.get() * 0.16), // up to 80% walking speed restriction
                            AttributeModifier.Operation.ADDITION));
        }

        if (swimBoost.get() > 0) {
            builder.put(ForgeMod.SWIM_SPEED.get(),
                    new AttributeModifier(ARMOR_MODIFIERS[slot.getIndex()],
                            ForgeMod.SWIM_SPEED.get().getDescriptionId(),
                            swimBoost.get(),
                            AttributeModifier.Operation.ADDITION));
        }

        return builder.build();
    }

    /**
     * This will work for the vanilla type models. This will not work for high polly models due to how the rendering works
     * @param armor
     * @param entity
     * @param equipmentSlotType
     * @param type
     * @return
     */
    @Nullable
    @Override
    public String getArmorTexture(ItemStack armor, Entity entity, EquipmentSlotType equipmentSlotType, String type) {
        if (type == "overlay") { // this is to allow a tint to be applied tot the armor
            return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
        }

        if (!equipmentSlotType.equals(armor.getEquipmentSlot())) {
            return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
        }

        if (entity instanceof LivingEntity) {
            if (armor.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .filter(IModularItem.class::isInstance)
                    .map(IModularItem.class::cast)
                    .map(iItemHandler -> iItemHandler.isModuleOnline(MPSRegistryNames.TRANSPARENT_ARMOR_MODULE)).orElse(false)) {
                return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
            }
        }

        return armor.getCapability(ModelSpecNBTCapability.RENDER)
                .filter(IArmorModelSpecNBT.class::isInstance)
                .map(IArmorModelSpecNBT.class::cast)
                .map(spec-> {
                    if (spec.getSpecType() == EnumSpecType.NONE) {
                        return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
                    }

                    if (spec.getRenderTag() != null && spec.getRenderTag().isEmpty()) {
                        return NuminaConstants.BLANK_ARMOR_MODEL_PATH;
                    }
                    return spec.getArmorTexture();
                })
                .orElse(AtlasTexture.LOCATION_BLOCKS.toString());
    }

    /**
     * Null return gets replaced with the _default elsewhere
     * @param entityLiving
     * @param itemStack
     * @param armorSlot
     * @param _default
     * @return
     */
    @Nullable
    @Override
    @OnlyIn(Dist.CLIENT)
    public BipedModel getArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlotType armorSlot, BipedModel _default) {
//        if (!(entityLiving instanceof PlayerEntity)) {
//            return _default;
//        }

        return itemStack.getCapability(ModelSpecNBTCapability.RENDER).map(spec-> {
            CompoundNBT renderTag = spec.getRenderTag();

            EquipmentSlotType slot = MobEntity.getEquipmentSlotForItem(itemStack);
            /** sets up default spec tags. A tag with all parts disabled should still have a color tag rather than being empty or null */
            if ((renderTag == null ||  renderTag.isEmpty()) && entityLiving == Minecraft.getInstance().player && armorSlot == slot) {
                renderTag = spec.getDefaultRenderTag();
                if (renderTag != null && !renderTag.isEmpty()) {
                    spec.setRenderTag(renderTag, NuminaConstants.TAG_RENDER);
                    NuminaPackets.CHANNEL_INSTANCE.sendToServer(new CosmeticInfoPacket(armorSlot, NuminaConstants.TAG_RENDER, renderTag));
                }
            }

            /** Armor skin uses vanilla model, but returning _default for EnumSpecType.NONE renders a garbage model */
            if (spec.getRenderTag() != null && (spec.getSpecType() == EnumSpecType.ARMOR_SKIN)) {
                return _default;
            }

            BipedModel model = ArmorModelInstance.getInstance();
            ItemStack chestplate = entityLiving.getItemBySlot(EquipmentSlotType.CHEST);
            if (chestplate.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                    .filter(IModularItem.class::isInstance)
                    .map(IModularItem.class::cast)
                    .map(iItemHandler -> iItemHandler.isModuleOnline(MPSRegistryNames.ACTIVE_CAMOUFLAGE_MODULE)).orElse(false)) {
                ((HighPolyArmor) model).setVisibleSection(null);
            } else
            {
                if (renderTag != null) {
                    ((HighPolyArmor) model).setVisibleSection(slot);
                    ((HighPolyArmor) model).setRenderSpec(renderTag);
                }
            }
            return model;
        }).orElse(_default);
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn != null) {
            AdditionalInfo.appendHoverText(stack, worldIn, tooltip, flagIn);
        }
    }

    @Nullable
    @Override
    public ICapabilityProvider initCapabilities(@Nonnull ItemStack stack, @Nullable CompoundNBT nbt) {
        return new PowerArmorCap(stack, this.slot);
    }

    @Override
    public boolean showDurabilityBar(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> energyCap.getMaxEnergyStored() > 0).orElse(false);
    }

    @Override
    public double getDurabilityForDisplay(final ItemStack stack) {
        return stack.getCapability(CapabilityEnergy.ENERGY)
                .map( energyCap-> 1 - energyCap.getEnergyStored() / (double) energyCap.getMaxEnergyStored()).orElse(1D);
    }

    @Override
    public boolean canBeDepleted() {
        return false;
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {

    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return false;
    }
}