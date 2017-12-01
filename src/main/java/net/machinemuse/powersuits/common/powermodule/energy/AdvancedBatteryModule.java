package net.machinemuse.powersuits.common.powermodule.energy;

import net.machinemuse.api.IModularItem;
import net.machinemuse.api.electricity.ElectricConversions;
import net.machinemuse.powersuits.client.events.MuseIcon;
import net.machinemuse.powersuits.common.items.ItemComponent;
import net.machinemuse.powersuits.common.powermodule.PowerModuleBase;
import net.machinemuse.utils.ElectricItemUtils;
import net.machinemuse.utils.MuseCommonStrings;
import net.machinemuse.utils.MuseItemUtils;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.item.ItemStack;

import java.util.List;

import static net.machinemuse.powersuits.common.MPSConstants.MODULE_BATTERY_ADVANCED;

public class AdvancedBatteryModule extends PowerModuleBase {
    public AdvancedBatteryModule(List<IModularItem> validItems) {
        super(validItems);
        addInstallCost(MuseItemUtils.copyAndResize(ItemComponent.mvcapacitor, 1));
        addBaseProperty(ElectricItemUtils.MAXIMUM_ENERGY, 100000, "J");
        addBaseProperty(MuseCommonStrings.WEIGHT, 2000, "g");
        addTradeoffProperty("Battery Size", ElectricItemUtils.MAXIMUM_ENERGY, 400000);
        addTradeoffProperty("Battery Size", MuseCommonStrings.WEIGHT, 8000);
        addBaseProperty(ElectricConversions.IC2_TIER, 1);
        addTradeoffProperty("IC2 Tier", ElectricConversions.IC2_TIER, 2);
    }

    @Override
    public String getCategory() {
        return MuseCommonStrings.CATEGORY_ENERGY;
    }

    @Override
    public String getDataName() {
        return MODULE_BATTERY_ADVANCED;
    }

    @Override
    public String getUnlocalizedName() {
        return "advancedBattery";
    }

    @Override
    public TextureAtlasSprite getIcon(ItemStack item) {
        return MuseIcon.advancedBattery;
    }
}