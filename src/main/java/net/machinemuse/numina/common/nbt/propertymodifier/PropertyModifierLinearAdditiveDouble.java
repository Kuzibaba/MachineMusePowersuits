package net.machinemuse.numina.common.nbt.propertymodifier;

import net.machinemuse.numina.common.item.MuseItemUtils;
import net.minecraft.nbt.NBTTagCompound;

public class PropertyModifierLinearAdditiveDouble implements IPropertyModifierDouble {
    public final String tradeoffName;
    public double multiplier;

    public PropertyModifierLinearAdditiveDouble(String tradeoffName, double multiplier) {
        this.multiplier = multiplier;
        this.tradeoffName = tradeoffName;
    }

    @Override
    public Double applyModifier(NBTTagCompound moduleTag, double value) {
        return value + multiplier * MuseItemUtils.getDoubleOrZero(moduleTag, tradeoffName);
    }

    public String getTradeoffName() {
        return tradeoffName;
    }
}