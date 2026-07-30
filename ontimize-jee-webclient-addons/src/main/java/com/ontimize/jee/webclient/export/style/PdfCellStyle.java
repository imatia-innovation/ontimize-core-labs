package com.ontimize.jee.webclient.export.style;

import java.awt.Color;
import java.text.Format;

/**
 * PdfCellStyle: interfaz agnóstica a la implementación de PDF. Migrada de iText7
 * (TextAlignment/VerticalAlignment/Color) a tipos compatibles con OpenPDF: java.awt.Color y constantes de alineación
 * (int) de com.lowagie.text.Element. Nota: usa Element.ALIGN_LEFT / ALIGN_CENTER / ALIGN_RIGHT para horizontal,
 * Element.ALIGN_TOP / ALIGN_MIDDLE / ALIGN_BOTTOM para vertical.
 */
public interface PdfCellStyle {

	/**
	 * Establece alineación horizontal. Use constantes de com.lowagie.text.Element (int). Ejemplo: Element.ALIGN_LEFT
	 */
	void setHorizontalAlignment(Integer horizontalAlignment);

	/**
	 * Devuelve la alineación horizontal (Integer con constantes Element.ALIGN_*).
	 */
	Integer getHorizontalAlignment();

	/**
	 * Establece alineación vertical. Use constantes de com.lowagie.text.Element (int). Ejemplo: Element.ALIGN_MIDDLE
	 */
	void setVerticalAlignment(Integer verticalAlignment);

	/**
	 * Devuelve la alineación vertical (Integer con constantes Element.ALIGN_*).
	 */
	Integer getVerticalAlignment();

	/**
	 * Color de fondo de la celda (java.awt.Color).
	 */
    void setBackgroundColor(Color backgroundColor);

	/**
	 * Color de fondo de la celda.
	 */
    Color getBackgroundColor();

	/**
	 * Formateador de datos para la celda.
	 */
    Format getDataFormatter();

    void setDataFormatter(final Format formatter);

	/**
	 * Copia/merge del estilo desde otra instancia.
	 */
    void cloneStyleFrom(PdfCellStyle source);
}
