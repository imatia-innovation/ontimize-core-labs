package com.ontimize.jee.webclient.export.style.support;

import java.awt.Color;
import java.text.Format;

import com.ontimize.jee.webclient.export.style.PdfCellStyle;

/**
 * Implementación por defecto de PdfCellStyle usando tipos compatibles con OpenPDF. - Alineaciones: Integer (constantes
 * com.lowagie.text.Element.ALIGN_*) - Color: java.awt.Color
 */
public class DefaultPdfPCellStyle implements PdfCellStyle {

	private Integer	horizontalAlignment;

	private Integer	verticalAlignment;

    private Color backgroundColor;

    private Format formatter;

    @Override
	public void setHorizontalAlignment(final Integer horizontalAlignment) {
        this.horizontalAlignment = horizontalAlignment;
    }

    @Override
	public Integer getHorizontalAlignment() {
        return this.horizontalAlignment;
    }

    @Override
	public void setVerticalAlignment(final Integer verticalAlignment) {
        this.verticalAlignment = verticalAlignment;
    }

    @Override
	public Integer getVerticalAlignment() {
        return this.verticalAlignment;
    }

    @Override
    public void setBackgroundColor(final Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    @Override
    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    @Override
    public Format getDataFormatter() {
        return this.formatter;
    }

    @Override
    public void setDataFormatter(final Format formatter) {
        this.formatter = formatter;
    }

    @Override
    public void cloneStyleFrom(final PdfCellStyle source) {
        if (source != null) {
            this.setBackgroundColor(source.getBackgroundColor());
            this.setHorizontalAlignment(source.getHorizontalAlignment());
            this.setVerticalAlignment(source.getVerticalAlignment());
            this.setDataFormatter(source.getDataFormatter());
        }
    }
}
