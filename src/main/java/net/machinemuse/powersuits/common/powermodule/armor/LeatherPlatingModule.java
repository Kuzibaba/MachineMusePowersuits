package net.machinemuse.powersuits.common.powermodule.armor;

import net.machinemuse.numina.common.constants.NuminaNBTConstants;
import net.machinemuse.numina.common.item.MuseItemUtils;
import net.machinemuse.numina.common.module.EnumModuleCategory;
import net.machinemuse.numina.common.module.EnumModuleTarget;
import net.machinemuse.powersuits.common.base.ModuleManager;
import net.machinemuse.powersuits.common.constants.MPSModuleConstants;
import net.machinemuse.powersuits.common.powermodule.PowerModuleBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class LeatherPlatingModule extends PowerModuleBase {
    protected final ItemStack leather = new ItemStack(Items.LEATHER);

    public LeatherPlatingModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(this.getDataName(), MuseItemUtils.copyAndResize(new ItemStack(Items.LEATHER), 2));

        addBasePropertyDouble(MPSModuleConstants.ARMOR_VALUE_PHYSICAL, 3,
                MPSModuleConstants.MODULE_TRADEOFF_PREFIX + MPSModuleConstants.ARMOR_POINTS);
        addBasePropertyDouble(NuminaNBTConstants.MAXIMUM_HEAT, 75);
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_LEATHER_PLATING__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return Minecraft.getMinecraft().getRenderItem().getItemModelMesher().getItemModel(leather).getParticleTexture();
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.CATEGORY_ARMOR;
    }
}