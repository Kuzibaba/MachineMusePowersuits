package lehjr.numina.client.gui.frame;

import com.mojang.blaze3d.vertex.PoseStack;
import lehjr.numina.client.gui.clickable.IClickable;
import lehjr.numina.client.gui.clickable.ModularItemTabToggleWidget;
import lehjr.numina.client.gui.clickable.button.VanillaButton;
import lehjr.numina.client.gui.geometry.DrawableTile;
import lehjr.numina.client.gui.geometry.IRect;
import lehjr.numina.client.gui.geometry.MusePoint2D;
import lehjr.numina.client.gui.geometry.Rect;
import lehjr.numina.common.capabilities.inventory.modularitem.IModularItem;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.ForgeCapabilities;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ModularItemSelectionFrame extends AbstractGuiFrame {
    public ModularItemTabToggleWidget selectedTab = null;
    public VanillaButton creativeInstallButton;

    IChanged changed;

    List<IRect> boxes;

    // just because I prefer this order :P
    final List<EquipmentSlot> equipmentSlotTypes = Arrays.asList(
            EquipmentSlot.HEAD,
            EquipmentSlot.CHEST,
            EquipmentSlot.LEGS,
            EquipmentSlot.FEET,
            EquipmentSlot.MAINHAND,
            EquipmentSlot.OFFHAND);
    public ModularItemSelectionFrame(MusePoint2D ul) {
        this(ul, EquipmentSlot.HEAD);
    }

    public ModularItemSelectionFrame(MusePoint2D ul, EquipmentSlot type) {
        super(new DrawableTile(ul, ul.plus(35, 200)));
        boxes = new ArrayList<>();
        // each tab is 27 tall and 35 wide
        /** 6 widgets * 27 high each = 162 + 5 spacers at 3 each = 177 gui height is 200 so 23 to split */

        // top spacer
        boxes.add(new Rect(ul.copy(), ul.plus(35, 11)));

        int i = 0;
        // look for modular items
        for (EquipmentSlot slotType : equipmentSlotTypes) {
            ModularItemTabToggleWidget widget = new ModularItemTabToggleWidget(slotType);
            widget.setUL(ul.copy());
            widget.setBelow(boxes.get(boxes.size() - 1));

            widget.setOnPressed(pressed -> {
                if (widget != selectedTab) {
                    this.selectedTab.setStateActive(false);
                    this.selectedTab = widget;
                    this.selectedTab.setStateActive(true);
                    this.onChanged();
                    disableAbstractContainerMenuSlots();
                }
            });

            if (slotType == type) {
                this.selectedTab = widget;
                this.selectedTab.setStateActive(true);
            }

            boxes.add(widget);
            boxes.add(new Rect(ul, ul.plus(35, 3)));
        }
        creativeInstallButton = new VanillaButton(MusePoint2D.ZERO, Component.translatable("gui.powersuits.creative.install"), false);
        creativeInstallButton.setHeight(18);
        creativeInstallButton.setWidth(38);
        creativeInstallButton.disableAndHide();
//        creativeInstallButton.setEnabledBackground(Color.LIGHT_GRAY);
//        creativeInstallButton.setDisabledBackground(Color.RED);
        creativeInstallButton.setUL(getUL().copy());


//        List<Component> toolTip =  new ArrayList<Component>() {{
//            add(Component.translatable("gui.powersuits.creative.install.desc"));
//        }};
        boxes.add(creativeInstallButton);
        refreshRects();
    }

    public void refreshRects() {
        double finalHeight = 0;

        for (int index = 0; index < boxes.size(); index++) {
            IRect rect = boxes.get(index);
            if (index == 0) {
                rect.setUL(this.getUL());
            } else {
                rect.setLeft(left()).setBelow(boxes.get(index -1));
            }
            finalHeight += rect.height();;
        }
        setHeight(finalHeight);
        creativeInstallButton.setLeft(left() -7);
    }

    public VanillaButton getCreativeInstallButton() {
        return creativeInstallButton;
    }

    void disableAbstractContainerMenuSlots() {
    }

    public ItemStack getModularItemOrEmpty() {
        return getSelectedTab().map(tab -> {
            ItemStack stack = getStack(tab.getSlotType());
            return getModularItemCapability(tab.getSlotType()).isPresent() ? stack : ItemStack.EMPTY;
        }).orElse(ItemStack.EMPTY);
    }

    public Optional<IModularItem> getModularItemCapability () {
        return getModularItemOrEmpty().getCapability(ForgeCapabilities.ITEM_HANDLER)
                .filter(IModularItem.class::isInstance)
                .map(IModularItem.class::cast);
    }

    public boolean playerHasModularItems() {
        return equipmentSlotTypes.stream()
                .filter(type->getModularItemCapability(type)
                        .isPresent()).findFirst().isPresent();
    }

    Optional<IModularItem> getModularItemCapability (EquipmentSlot type) {
        return getStack(type).getCapability(ForgeCapabilities.ITEM_HANDLER)
                .filter(IModularItem.class::isInstance)
                .map(IModularItem.class::cast);
    }

    @Nonnull
    ItemStack getStack(EquipmentSlot type) {
        return getMinecraft().player.getItemBySlot(type);
    }

    public Optional<EquipmentSlot> selectedType() {
        return getSelectedTab().map(tab ->tab.getSlotType());
    }

//    public boolean selectedIsSlotHovered() {
//        return getSelectedTab().map(tab->tab.isHovered()).orElse(false);
//    }

    public Optional<ModularItemTabToggleWidget> getSelectedTab() {
        if (this.selectedTab == null) {
            this.selectedTab = boxes.stream().filter(ModularItemTabToggleWidget.class::isInstance).map(ModularItemTabToggleWidget.class::cast).findFirst().orElse(null);
            if (selectedTab != null) {
                this.selectedTab.setStateActive(true);
            }
        }

        boxes.stream().filter(ModularItemTabToggleWidget.class::isInstance)
                .map(ModularItemTabToggleWidget.class::cast).forEach(widget ->
                        widget.setStateActive(widget==selectedTab));

        return Optional.ofNullable(selectedTab);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (boxes.stream().filter(IClickable.class::isInstance)
                .map(IClickable.class::cast).filter(box->box.mouseClicked(mouseX, mouseY, button)).findFirst().isPresent()) {
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (boxes.stream().filter(IClickable.class::isInstance)
                .map(IClickable.class::cast).filter(box->box.mouseReleased(mouseX, mouseY, button)).findFirst().isPresent()) {
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTick) {
//        renderBackgroundRect(matrixStack, mouseX, mouseY, partialTick);
        boxes.stream().filter(IClickable.class::isInstance)
                .map(IClickable.class::cast)
                .forEach(box-> {
                    box.render(matrixStack, mouseX, mouseY, partialTick);
                });
    }

    @Nullable
    @Override
    public List<Component> getToolTip(int x, int y) {
        return boxes.stream().filter(IClickable.class::isInstance)
                .map(IClickable.class::cast).filter(box -> box.containsPoint(x, y))
                .findFirst().map(box -> box.getToolTip(x, y)).orElse(super.getToolTip(x, y));
    }

    @Override
    public void update(double mouseX, double mouseY) {
        super.update(mouseX, mouseY);
        getSelectedTab();
    }



    public void setOnChanged(IChanged changed) {
        this.changed = changed;
    }

    public void onChanged() {
        if(this.isEnabled() && this.isVisible()) {
            refreshRects();
            if (changed != null) {
                this.changed.onChanged();
            }
        }
    }

    public interface IChanged {
        void onChanged();
    }
}
