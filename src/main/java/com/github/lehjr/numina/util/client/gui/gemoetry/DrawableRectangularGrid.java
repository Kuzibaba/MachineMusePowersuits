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

package com.github.lehjr.numina.util.client.gui.gemoetry;

import com.github.lehjr.numina.util.math.Colour;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import org.lwjgl.opengl.GL11;

import java.nio.FloatBuffer;

public class DrawableRectangularGrid extends DrawableRelativeRect {
    Colour gridColour;
    int gridHeight;
    int gridWidth;
    Double horizontalSegmentSize;
    Double verticleSegmentSize;
    final MuseRelativeRect[] boxes;

    public DrawableRectangularGrid(double left, double top, double right, double bottom, boolean growFromMiddle,
                                   Colour insideColour,
                                   Colour outsideColour,
                                   Colour gridColour,
                                   int gridHeight,
                                   int gridWidth) {
        super(left, top, right, bottom, growFromMiddle, insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    public DrawableRectangularGrid(double left, double top, double right, double bottom,
                                   Colour insideColour,
                                   Colour outsideColour,
                                   Colour gridColour,
                                   int gridHeight,
                                   int gridWidth) {
        super(left, top, right, bottom, false, insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    public DrawableRectangularGrid(MusePoint2D ul, MusePoint2D br,
                                   Colour insideColour,
                                   Colour outsideColour,
                                   Colour gridColour,
                                   int gridHeight,
                                   int gridWidth) {
        super(ul, br, insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    public DrawableRectangularGrid(MuseRelativeRect ref,
                                   Colour insideColour,
                                   Colour outsideColour,
                                   Colour gridColour,
                                   int gridHeight,
                                   int gridWidth) {
        super(ref.left(), ref.top(), ref.right(), ref.bottom(), ref.growFromMiddle(), insideColour, outsideColour);
        this.gridColour = gridColour;
        this.gridHeight = gridHeight;
        this.gridWidth = gridWidth;
        this.boxes = new MuseRelativeRect[gridHeight*gridWidth];
        setBoxes();
    }

    void setBoxes() {
        for (int i = 0; i < boxes.length; i++) {
            boxes[i] = new MuseRelativeRect(0, 0, 0, 0);
        }
    }


    public MuseRelativeRect[] getBoxes() {
        return boxes;
    }

    void setupGrid() {
        horizontalSegmentSize = (double) (width() / gridWidth);
        verticleSegmentSize = (double) (height() / gridHeight);

        // uper left coner
        MusePoint2D box_ul;
        // bottom right
        MusePoint2D box_br;
        // width and height of each box

        MusePoint2D box_offset = new MusePoint2D(horizontalSegmentSize, verticleSegmentSize);
        int i = 0;

        // These boxes provide centers for the slots
        for (int y = 0; y < gridHeight; y++) {
            for (int x = 0; x < gridWidth; x++) {
                box_ul = new MusePoint2D(horizontalSegmentSize * x, verticleSegmentSize * y);
                boxes[i].setTargetDimensions(box_ul, box_offset);

                if (i >0) {
                    if (x > 0)
                        boxes[i].setMeRightOf(boxes[i-1]);
                    if (y > 0){
                        boxes[i].setMeBelow(boxes[i-gridWidth]);
                    }
                }
                i++;
            }
        }
    }

    void drawGrid(MatrixStack matrixStack) {

        // reinitialize values on "growFromCenter" or resize
        boolean needInt = false;
        for (int i = 0; i < boxes.length; i++) {
            if (boxes[i] == null) {
                needInt = true;
                break;
            }
        }

        if (needInt) {
            setBoxes();
        }

        if (horizontalSegmentSize == null || verticleSegmentSize == null || (this.ul != this.ulFinal || this.wh != this.whFinal)) {
            setupGrid();
        }

        RenderSystem.disableTexture();
        RenderSystem.enableBlend();
        RenderSystem.disableAlphaTest();
        RenderSystem.defaultBlendFunc();
        RenderSystem.shadeModel(GL11.GL_SMOOTH);

        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();
        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);

        // Horizontal lines
        if (gridHeight >1) {
            for (double y = (double) (verticleSegmentSize + top()); y < bottom(); y += verticleSegmentSize) {
                buffer.pos(matrixStack.getLast().getMatrix(), (float)left(), (float) y, zLevel).color(gridColour.r, gridColour.g, gridColour.b, gridColour.a).endVertex();
                buffer.pos(matrixStack.getLast().getMatrix(), (float)right(), (float) y, zLevel).color(gridColour.r, gridColour.g, gridColour.b, gridColour.a).endVertex();
            }
        }

        // Vertical lines
        if(gridWidth > 1) {
            for (double x = (double) (horizontalSegmentSize + left()); x < right(); x += horizontalSegmentSize) {
                buffer.pos(matrixStack.getLast().getMatrix(), (float) x, (float) top(), zLevel).color(gridColour.r, gridColour.g, gridColour.b, gridColour.a).endVertex();
                buffer.pos(matrixStack.getLast().getMatrix(), (float) x, (float) bottom(), zLevel).color(gridColour.r, gridColour.g, gridColour.b, gridColour.a).endVertex();
            }
        }

        tessellator.draw();

        RenderSystem.shadeModel(GL11.GL_FLAT);
        RenderSystem.disableBlend();
        RenderSystem.enableAlphaTest();
        RenderSystem.enableTexture();
    }

    @Override
    public DrawableRelativeRect setLeft(double value) {
        double diff = value - left();
        super.setLeft(value);
        for (MuseRelativeRect box : boxes) {
            if (box != null) {
                box.setLeft(box.left() + diff);
            }
        }
        return this;
    }

    @Override
    public void draw(MatrixStack matrixStack, float zLevel) {
        FloatBuffer vertices = preDraw(0);
        drawBackground(matrixStack, vertices);
        drawGrid(matrixStack);
        drawBorder(matrixStack, vertices);
    }
}