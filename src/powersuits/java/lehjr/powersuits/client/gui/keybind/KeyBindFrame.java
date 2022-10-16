package lehjr.powersuits.client.gui.keybind;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import lehjr.numina.client.render.MuseRenderer;
import lehjr.numina.common.math.Colour;
import lehjr.numina.client.gui.clickable.CheckBox;
import lehjr.numina.client.gui.clickable.ClickableButton2;
import lehjr.numina.client.gui.frame.ScrollableFrame;
import lehjr.numina.client.gui.gemoetry.MusePoint2D;
import lehjr.numina.client.gui.gemoetry.RelativeRect;
import lehjr.powersuits.client.control.KeybindManager;
import lehjr.powersuits.client.control.MPSKeyBinding;
import lehjr.powersuits.client.event.RenderEventHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.lwjgl.glfw.GLFW;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lehjr
 */
public class KeyBindFrame extends ScrollableFrame {
    @Nullable
    MPSKeyBinding keybindingToRemap = null;
    Map<MPSKeyBinding, Pair<CheckBox, ClickableButton2>> checkBoxList = new HashMap<>();

    public KeyBindFrame(MusePoint2D topleft, MusePoint2D bottomright) {
        super(topleft, bottomright, Colour.DARK_GREY, new Colour(0.216F, 0.216F, 0.216F, 1F), Colour.WHITE.withAlpha(0.8F));
        this.enableAndShow();
        keybindingToRemap = null;
    }

    @Override
    public RelativeRect init(double left, double top, double right, double bottom) {
        super.init(left, top, right, bottom);
        loadConditions();
        return this;
    }

    @Override
    public void setVisible(boolean visible) {
        super.setVisible(visible);
        loadConditions();
    }

    public void loadConditions() {
        Arrays.stream(Minecraft.getInstance().options.keyMappings)
                .filter(MPSKeyBinding.class::isInstance)
                .map(MPSKeyBinding.class::cast)
                .forEach(keyBinding -> {
                    CheckBox checkbox = new CheckBox(MusePoint2D.ZERO, new TranslationTextComponent(keyBinding.getName()), false);
                    checkbox.setChecked(keyBinding.showOnHud);
                    checkbox.setOnPressed(onPressed -> {
                        keyBinding.showOnHud = checkbox.isChecked();
                        KeybindManager.INSTANCE.writeOutKeybindSetings();
                        RenderEventHandler.INSTANCE.makeKBDisplayList();
                    });
                    checkbox.enableAndShow();
                    ClickableButton2 button = new ClickableButton2(keyBinding.getKey().getDisplayName(), MusePoint2D.ZERO, true);
                    button.setOnPressed(onPressed->
                    {
                        keybindingToRemap = keyBinding;
                    });
                    button.setWidth(95);
                    button.setHeight(16);

                    checkBoxList.put(keyBinding, Pair.of(checkbox, button));
                });
        this.setTotalSize(checkBoxList.size() * 20);
    }

    static boolean isKeyPressed(int key) {
        return GLFW.glfwGetKey(Minecraft.getInstance().getWindow().getWindow(), key) == GLFW.GLFW_PRESS;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        if (this.isEnabled() && this.isVisible()) {
            super.preRender(matrixStack, mouseX, mouseY, partialTicks);
            RenderSystem.pushMatrix();
            RenderSystem.translated(0.0, -this.currentScrollPixels, 0.0);

            double y = top() + 8;
            for (Map.Entry<MPSKeyBinding, Pair<CheckBox, ClickableButton2>> entry : checkBoxList.entrySet()) {
                MuseRenderer.drawModuleAt(matrixStack, finalLeft() + 2, y -8, new ItemStack(ForgeRegistries.ITEMS.getValue(entry.getKey().registryName)), true);
                MPSKeyBinding kb = entry.getKey();
                CheckBox checkbox = entry.getValue().getFirst();
                checkbox.setPosition(new MusePoint2D(finalLeft() + 24, y));
                checkbox.enableAndShow();
                checkbox.render(matrixStack, mouseX, mouseY, partialTicks);

                ClickableButton2 button = entry.getValue().getSecond();

                if (keybindingToRemap != null && keybindingToRemap == kb) {
                    button.setLable((new StringTextComponent("> ")).append(kb.getKey().getDisplayName().copy().withStyle(TextFormatting.YELLOW)).append(" <").withStyle(TextFormatting.YELLOW));
                } else  {
                    button.setLable(kb.getKey().getDisplayName().copy().withStyle( /* keyCodeModifierConflict ? */ TextFormatting.WHITE /*: TextFormatting.RED*/));
                }

//                button.setLable(entry.getKey().getKey().getDisplayName());// in case the mapping is changed


                button.setRight(finalRight() -2);
                button.setTop(y-8);
                button.render(matrixStack, mouseX, mouseY, partialTicks);



//                MuseRenderer.drawRightAlignedShadowedString(matrixStack, entry.getKey().getKey().getDisplayName(), finalRight()- 4, y);

                y += 20;
            }

            RenderSystem.popMatrix();
            super.postRender(mouseX, mouseY, partialTicks);
        } else {
            super.preRender(matrixStack, mouseX, mouseY, partialTicks);
            super.postRender(mouseX, mouseY, partialTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (this.isEnabled() && this.isVisible()) {
            super.mouseClicked(mouseX, mouseY, button);
            for (Pair<CheckBox, ClickableButton2> pair : checkBoxList.values()) {
                if (pair.getFirst().mouseClicked(mouseX, mouseY + getCurrentScrollPixels(), button) ||
                        pair.getSecond().mouseClicked(mouseX, mouseY + getCurrentScrollPixels(), button)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        if (this.isEnabled() && this.isVisible()) {
            for (Pair<CheckBox, ClickableButton2> pair : checkBoxList.values()) {
                if (pair.getFirst().hitBox(x, y)) {
                    return Arrays.asList(new TranslationTextComponent("gui.powersuits.showOnHUD"));
                }
            }
        }
        return super.getToolTip(x, y);
    }
}