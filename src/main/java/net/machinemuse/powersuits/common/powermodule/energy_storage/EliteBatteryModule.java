package net.machinemuse.powersuits.common.powermodule.energy_storage;

import net.machinemuse.numina.common.constants.NuminaNBTConstants;
import net.machinemuse.numina.common.item.MuseItemUtils;
import net.machinemuse.numina.common.module.EnumModuleCategory;
import net.machinemuse.numina.common.module.EnumModuleTarget;
import net.machinemuse.powersuits.client.event.MuseIcon;
import net.machinemuse.powersuits.common.base.ModuleManager;
import net.machinemuse.powersuits.common.constants.MPSModuleConstants;
import net.machinemuse.powersuits.common.item.ItemComponent;
import net.machinemuse.powersuits.common.powermodule.PowerModuleBase;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

public class EliteBatteryModule extends PowerModuleBase {
    public EliteBatteryModule(EnumModuleTarget moduleTarget) {
        super(moduleTarget);
        ModuleManager.INSTANCE.addInstallCost(getDataName(), MuseItemUtils.copyAndResize(ItemComponent.hvcapacitor, 1));
        addBasePropertyDouble(NuminaNBTConstants.MAXIMUM_ENERGY, 50000000, "RF");
    }

    @Override
    public EnumModuleCategory getCategory() {
        return EnumModuleCategory.CATEGORY_ENERGY_STORAGE;
    }

    @Override
    public String getDataName() {
        return MPSModuleConstants.MODULE_BATTERY_ELITE__DATANAME;
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.eliteBattery;
    }
}