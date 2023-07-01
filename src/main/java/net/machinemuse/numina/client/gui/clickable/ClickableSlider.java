package net.machinemuse.numina.client.gui.clickable;

import net.machinemuse.numina.client.gui.geometry.DrawableMuseRect;
import net.machinemuse.numina.client.gui.geometry.MusePoint2D;
import net.machinemuse.numina.client.render.MuseRenderer;
import net.machinemuse.numina.common.math.Colour;
import net.machinemuse.numina.common.math.MuseMathUtils;
import net.minecraft.client.resources.I18n;

/**
 * Author: MachineMuse (Claire Semple)
 * Created: 7:08 AM, 06/05/13
 * <p>
 * Ported to Java by lehjr on 10/19/16.
 */
public class ClickableSlider extends Clickable {
    final int cornersize = 3;
    private double valueInternal = 0;
    MusePoint2D pos;
    double width;
    private String id;
    private String label;
    DrawableMuseRect insideRect;
    DrawableMuseRect outsideRect;

    public ClickableSlider(MusePoint2D pos, double width, String id, String label) {
        this.pos = pos;
        this.width = width;
        this.id = id;
        this.position = pos;
        this.insideRect = new DrawableMuseRect(position.getX() - width / 2.0 - cornersize, position.getY() + 8, 0, position.getY() + 16, Colour.LIGHTBLUE, Colour.ORANGE);
        this.outsideRect = new DrawableMuseRect(position.getX() - width / 2.0 - cornersize, position.getY() + 8, position.getX() + width / 2.0 + cornersize, position.getY() + 16, Colour.LIGHTBLUE, Colour.DARKBLUE);
        this.label = label;
    }

    public String id() {
        return this.id;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public void render(double mouseX, double mouseY, float partialTicks) {
        MuseRenderer.drawCenteredString(I18n.format(label), position.getX(), position.getY());
        this.insideRect.setRight(position.getX() + width * (getValue() - 0.5) + cornersize);
        this.outsideRect.render(mouseX, mouseY, partialTicks);
        this.insideRect.render(mouseX, mouseY, partialTicks);
    }

    @Override
    public boolean containsPoint(double mouseX, double mouseY) {
        return Math.abs(position.getX() - mouseX) < width / 2 &&
                Math.abs(position.getY() + 12 - mouseY) < 4;
    }

    public double getValue() {
        return valueInternal;
    }

    public void setValue(double v) {
        valueInternal = v;
    }

    public void setValueByX(double x) {
        valueInternal = MuseMathUtils.clampDouble((x - pos.getX()) / width + 0.5, 0, 1);
    }
}
