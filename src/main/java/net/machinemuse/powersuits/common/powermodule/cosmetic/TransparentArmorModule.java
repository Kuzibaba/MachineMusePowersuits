package net.machinemuse.powersuits.common.powermodule.cosmetic;

import net.machinemuse.numina.common.item.MuseItemUtils;
import net.machinemuse.numina.common.module.EnumModuleCategory;
import net.machinemuse.numina.common.module.EnumModuleTarget;
import net.machinemuse.numina.common.module.IToggleableModule;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.common.base.ModuleManager;
import net.machinemuse.powersuits.common.constants.MPSModuleConstants;
import net.machinemuse.powersuits.common.item.ItemComponent;
import net.machinemuse.powersuits.common.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

public class TransparentArmorModule extends PowerModuleBase implements IToggleableModule {
    public TransparentArmorModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), MuseItemUtils.copyAndResize(ItemComponent.laserHologram, 1));
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.CATEGORY_COSMETIC;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_TRANSPARENT_ARMOR__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.transparentArmor;
    }
}