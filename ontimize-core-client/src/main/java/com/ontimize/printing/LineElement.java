package com.ontimize.printing;

import java.awt.Color;
import java.awt.Graphics;
import java.util.Map;

public class LineElement extends AbstractPrintingElement {

	protected int xi = -1;

	protected int yi = -1;

	protected int xf = -1;

	protected int yf = -1;

	protected int pixelWidth = 1;

	public LineElement(final Map parameters) {
		this.setBasicParameters(parameters);
		final Object xini = parameters.get("xi");
		if (xini != null) {
			this.xi = this.getInteger(xini);
		}
		final Object yini = parameters.get("yi");
		if (yini != null) {
			this.yi = this.getInteger(yini);
		}
		final Object xfin = parameters.get("xf");
		if (xfin != null) {
			this.xf = this.getInteger(xfin);
		}
		final Object yfin = parameters.get("yf");
		if (yfin != null) {
			this.yf = this.getInteger(yfin);
		}

		final Object weight = parameters.get("weight");
		if (weight != null) {
			this.pixelWidth = this.getInteger(weight);
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
		return this.xf - this.xi;
	}

	@Override
	public int getHeight() {
		return this.yf - this.yi;
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
		this.xf = this.xi + w;
	}

	@Override
	public void setHeight(final int h) {
		this.yf = this.yi + h;
	}

	@Override
	public void paint(final Graphics g, final double scale) {
		if (this.color == null) {
			return;
		}
		final Color c = g.getColor();
		g.setColor(this.color);
		for (int i = 0; i < this.pixelWidth; i++) {
			g.drawLine((int) (AbstractPrintingElement.millimeterToPagePixels(this.xi) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.yi) * scale) + i,
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.xf) * scale),
					(int) (AbstractPrintingElement.millimeterToPagePixels(this.yf) * scale) + i);
		}
		g.setColor(c);
	}

}
