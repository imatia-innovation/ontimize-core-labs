package com.ontimize.printing;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

public class RectangleElement extends AbstractPrintingElement {

	protected int xi = 0;

	protected int yi = 0;

	protected int width = -1;

	protected int high = -1;

	public RectangleElement(final Map parameters) {
		this.setBasicParameters(parameters);
		final Object xini = parameters.get("xi");
		if (xini != null) {
			this.xi = this.getInteger(xini);
		}
		final Object yini = parameters.get("yi");
		if (yini != null) {
			this.yi = this.getInteger(yini);
		}
		final Object width = parameters.get("width");
		if (width != null) {
			this.width = this.getInteger(width);
		}
		final Object height = parameters.get("height");
		if (height != null) {
			this.high = this.getInteger(height);
		}
	}

	@Override
	public int getX() {
		return this.xi;
	}

	@Override
	public int getY() {
		return this.yi;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.high;
	}

	@Override
	public void setX(final int x) {
		this.xi = x;
	}

	@Override
	public void setY(final int y) {
		this.yi = y;
	}

	@Override
	public void setWidth(final int w) {
		this.width = w;
	}

	@Override
	public void setHeight(final int h) {
		this.high = h;
	}

	@Override
	public void paint(final Graphics g, final double scale) {
		final Color c = g.getColor();
		if (this.bgcolor != null) {
			g.setColor(this.bgcolor);
			g.fillRect((int) (AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.width) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.high) * scale));
		}
		if (this.color != null) {
			g.setColor(this.color);
			g.drawRect((int) (AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.width) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.high) * scale));
		}
		g.setColor(c);
	}

}
