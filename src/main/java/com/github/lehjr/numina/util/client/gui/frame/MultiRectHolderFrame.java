package com.github.lehjr.numina.util.client.gui.frame;

import com.github.lehjr.numina.util.client.gui.clickable.IClickable;
import com.github.lehjr.numina.util.client.gui.gemoetry.*;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A bunch of rectangles laid out either vertically or horizontally
 */

public class MultiRectHolderFrame<T extends Map<Integer, IRect>> extends GUISpacer {
    float zLevel;
    boolean isEnabled = true;
    boolean isVisible = true;
    @Nullable IDrawableRect background = null;
    Map<Integer, IRect> rects = new HashMap<>();
    boolean horizontalLayout;
    boolean startTopLeft;
    /**
     *  Note, all rectangles should have the same width for vertical layout, or same height for horizontal layout.
     *
     * @param horizontalLayout if true, layout will be like books on a shelf, or vertical (layered from top to bottom)
     * @param startTopLeft determines where the first link in the chain is, since that is the one that will determine which points to set
     *                     when the outer rectangle is moved. For instance, false will start on the bottom for vertical layout, or right for
     *                     horizontal layout. The rest in the chain should be using setMe-LOCATION-Of.
     * @param widthIn
     * @param heightIn
     */
    public MultiRectHolderFrame(boolean horizontalLayout, boolean startTopLeft, double widthIn, double heightIn) {
        super(widthIn, heightIn);
        this.horizontalLayout = horizontalLayout;
        this.startTopLeft = startTopLeft;
        setDoThisOnChange(change->refreshRects());
    }

    /**
     * Automatically sets orientation of next IRect based on Layout and direction settings
     * @param rect
     * @return
     */
    public MultiRectHolderFrame addRect(IRect rect) {
        if (rects.size() >0) {
            if (horizontalLayout) {
                if (startTopLeft) {
                    rect.setMeRightOf(rects.get(rects.size() -1));
                } else {
                    rect.setMeLeftOf(rects.get(rects.size() -1));
                }
            } else {
                if (startTopLeft) {
                    rect.setMeBelow(rects.get(rects.size() -1));
                } else {
                    rect.setMeAbove(rects.get(rects.size() -1));
                }
            }
        }
        rects.put(rects.size(), rect);
        return this;
    }

    public Optional<IRect> getLast() {
        return Optional.ofNullable(rects.size() > 0 ? (rects.get(rects.size() -1)) : null);
    }

    public void setRects(T rects){
        this.rects = rects;
    }

    /**
     * Automatically set width and height
     */
    public void doneAdding() {
        double finalVal = 0;
        double highestVal = 0;
        for (IRect rect : rects.values()) {
            // single max height, multiple widths
            if (horizontalLayout) {
                finalVal += rect.finalWidth();
                if (rect.finalHeight() > highestVal) {
                    highestVal = rect.finalHeight();
                }
                // single max width, multiple heights
            } else {
                finalVal += rect.finalHeight();
                if (rect.finalWidth() > highestVal) {
                    highestVal = rect.finalWidth();
                }
            }
        }
        if (horizontalLayout) {
            super.setWidth(finalVal);
            super.setHeight(highestVal);
        } else {
            super.setWidth(highestVal);
            super.setHeight(finalVal);
        }
    }

    public MultiRectHolderFrame setBackground(IDrawableRect background) {
        this.background = background;
        return this;
    }

    Optional<IDrawableRect> getBackground() {
        return Optional.ofNullable(this.background);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        for (IRect rect : rects.values()) {
            if (rect instanceof IGuiFrame && ((IGuiFrame) rect).mouseClicked(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        for (IRect rect : rects.values()) {
            if (rect instanceof IGuiFrame && ((IGuiFrame) rect).mouseReleased(mouseX, mouseY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double dWheel) {
        for (IRect rect : rects.values()) {
            if (rect instanceof IGuiFrame && ((IGuiFrame) rect).mouseScrolled(mouseX, mouseY, dWheel)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float frameTime) {
        getBackground().ifPresent(background-> background.render(matrixStack, mouseX, mouseY, frameTime));
        for (IRect rect : rects.values()) {
            if (rect instanceof IDrawable) {
                ((IDrawable) rect).render(matrixStack, mouseX, mouseY, frameTime);
            }
        }
    }

    @Override
    public List<ITextComponent> getToolTip(int x, int y) {
        List<ITextComponent> toolTip = null;
        for (IRect rect : rects.values()) {
            if (rect instanceof IDrawableRect) {
                toolTip = ((IDrawableRect) rect).getToolTip(x, y);
            }
            if (toolTip != null) {
                return toolTip;
            }
        }
        return null;
    }

    @Override
    public void setEnabled(boolean enabled) {
        this.isEnabled = enabled;
        for (IRect rect : rects.values()) {
            if(rect instanceof IClickable) {
                ((IClickable) rect).setEnabled(enabled);
            }
            if (rect instanceof IGuiFrame) {
                ((IGuiFrame) rect).setEnabled(enabled);
            }
        }
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public void setVisible(boolean visible) {
        this.isVisible = visible;
        for (IRect rect : rects.values()) {
            if(rect instanceof IClickable) {
                ((IClickable) rect).setVisible(visible);
            }
            if (rect instanceof IGuiFrame) {
                ((IGuiFrame) rect).setVisible(visible);
            }
        }
    }

    @Override
    public boolean isVisible() {
        return isVisible;
    }

    @Override
    public float getZLevel() {
        return zLevel;
    }

    @Override
    public IDrawable setZLevel(float zLevel) {
        this.zLevel = zLevel;
        for (IRect rect : rects.values()) {
            if (rect instanceof IDrawable) {
                ((IDrawable) rect).setZLevel(zLevel);
            }
        }

        return this;
    }

    @Override
    public void initGrowth() {
        super.initGrowth();
        for(IRect rect : rects.values()) {
            rect.initGrowth();
        }
        refreshRects();
    }

    // FIXME: this is just a workaround... maybe figure out why this is needed in the first place and fix
    public void refreshRects() {
        if (rects.size() > 0) {
            if (horizontalLayout) {
                // find leftmost box and set the left value
                if (startTopLeft) {
                    rects.get(0).setLeft(finalLeft());
                } else {
                    rects.get(rects.size() - 1).setRight(finalRight());
                }

                for (int i = 0; i< rects.size(); i++) {
                    /** sets the rects centered vertically */
                    rects.get(i).setTop(this.centery() - rects.get(i).finalHeight() * 0.5);
                    rects.get(i).setLeft(rects.get(i).finalLeft());
                }

            } else {
                // all boxes linked from top to bottom
                if (startTopLeft) {
                    rects.get(0).setTop(finalTop());
                    // all boxes lined from bottom to top
                } else {
                    rects.get(rects.size() - 1).setBottom(this.finalBottom());
                }

                for (int i = 0; i< rects.size(); i++) {
                    // centered height
                    rects.get(i).setTop(rects.get(i).finalTop());
                    rects.get(i).setLeft(this.centerx() - rects.get(i).finalWidth() * 0.5);
                }
            }
        }

        getBackground().ifPresent(rect-> {
            rect.setPosition(this.getPosition());
            rect.setWidth(this.finalWidth());
            rect.setHeight(this.finalHeight());
        });
    }

    @Override
    public void update(double mouseX, double mouseY) {
        for (IRect rect : rects.values()) {
            if (rect instanceof IGuiFrame) {
                ((IGuiFrame) rect).update(mouseX, mouseY);
            }
        }
    }

    @Override
    public IRect setWH(MusePoint2D wh) {
        return this;
    }
}
