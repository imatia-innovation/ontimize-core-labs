package com.ontimize.jee.webclient.export.style.util;

import java.awt.Color;

import com.lowagie.text.Element;
import com.ontimize.jee.webclient.export.ExportColumnStyle;
import com.ontimize.jee.webclient.export.style.PdfCellStyle;
import com.ontimize.jee.webclient.export.style.support.DefaultPdfPCellStyle;

public class PdfCellStyleUtils {

    public static PdfCellStyle createPdfCellStyle(final ExportColumnStyle columnStyle) {
        if (columnStyle == null) {
            return null;
        }
		final DefaultPdfPCellStyle cellStyle = new DefaultPdfPCellStyle();

		if (columnStyle.getHorizontalAlignment() != null) {
            cellStyle.setHorizontalAlignment(PdfCellStyleUtils.getHorizontalAlignment(columnStyle.getHorizontalAlignment()));
        }
        if (columnStyle.getVerticalAlignment() != null) {
            cellStyle.setVerticalAlignment(PdfCellStyleUtils.getVerticalAlignment(columnStyle.getVerticalAlignment()));
        }
        if (columnStyle.getFillBackgroundColor() != null) {
            cellStyle.setBackgroundColor(PdfCellStyleUtils.getBackgroundColor(columnStyle.getFillBackgroundColor()));
        }
        return cellStyle;
    }

	public static Integer getHorizontalAlignment(final ExportColumnStyle.HorizontalAlignment alignment) {
		if (alignment == null) {
			return Element.ALIGN_RIGHT;
		}
        switch (alignment) {
            case LEFT:
				return Element.ALIGN_LEFT;
            case CENTER:
				return Element.ALIGN_CENTER;
            case RIGHT:
				return Element.ALIGN_RIGHT;
            case JUSTIFY:
				return Element.ALIGN_JUSTIFIED;
            case FILL:
				// OpenPDF has ALIGN_JUSTIFIED and ALIGN_JUSTIFIED_ALL in some versions;
				// fallback to ALIGN_JUSTIFIED if ALIGN_JUSTIFIED_ALL not desired.
				try {
					// Prefer ALIGN_JUSTIFIED_ALL if available (compatibility defensive)
					return (Integer) Element.class.getField("ALIGN_JUSTIFIED_ALL").get(null);
				} catch (final Exception e) {
					return Element.ALIGN_JUSTIFIED;
				}
            default:
				return Element.ALIGN_RIGHT;
        }
    }

	public static Integer getVerticalAlignment(final ExportColumnStyle.VerticalAlignment alignment) {
		if (alignment == null) {
			return Element.ALIGN_MIDDLE;
		}
        switch (alignment) {
            case TOP:
				return Element.ALIGN_TOP;
            case CENTER:
				return Element.ALIGN_MIDDLE;
            case BOTTOM:
				return Element.ALIGN_BOTTOM;
            default:
				return Element.ALIGN_MIDDLE;
        }
    }

    public static Color getBackgroundColor(final ExportColumnStyle.CellColor color) {
		if (color == null) {
			return null;
		}
        switch (color) {
            case AQUA:
				return new Color(51, 204, 205);
            case AUTOMATIC:
            case BLACK:
            case BLACK1:
				return new Color(0, 0, 0);
            case BLUE:
            case BLUE1:
				return new Color(0, 0, 255);
            case BLUE_GREY:
				return new Color(102, 102, 153);
            case BRIGHT_GREEN:
            case BRIGHT_GREEN1:
				return new Color(0, 255, 0);
            case BROWN:
				return new Color(150, 50, 0);
            case CORAL:
				return new Color(255, 128, 128);
            case CORNFLOWER_BLUE:
				return new Color(150, 150, 255);
            case DARK_BLUE:
				return new Color(0, 0, 128);
            case DARK_GREEN:
				return new Color(0, 50, 0);
            case DARK_RED:
				return new Color(128, 0, 0);
            case DARK_TEAL:
				return new Color(0, 50, 100);
            case DARK_YELLOW:
				return new Color(128, 128, 0);
            case GOLD:
				return new Color(255, 200, 0);
            case GREEN:
				return new Color(0, 128, 0);
            case GREY_25_PERCENT:
				return new Color(192, 192, 192);
            case GREY_40_PERCENT:
				return new Color(150, 150, 150);
            case GREY_50_PERCENT:
				return new Color(128, 128, 128);
            case GREY_80_PERCENT:
				return new Color(51, 51, 51);
            case INDIGO:
				return new Color(51, 51, 154);
            case LAVENDER:
				return new Color(204, 153, 254);
            case LEMON_CHIFFON:
				return new Color(255, 255, 205);
            case LIGHT_BLUE:
				return new Color(51, 103, 255);
            case LIGHT_CORNFLOWER_BLUE:
				return new Color(204, 204, 255);
            case LIGHT_GREEN:
				return new Color(204, 255, 204);
            case LIGHT_ORANGE:
				return new Color(255, 154, 0);
            case LIGHT_TURQUOISE:
            case LIGHT_TURQUOISE1:
				return new Color(204, 255, 255);
            case LIGHT_YELLOW:
				return new Color(255, 255, 151);
            case LIME:
				return new Color(151, 203, 0);
            case MAROON:
				return new Color(152, 51, 102);
            case OLIVE_GREEN:
				return new Color(51, 51, 0);
            case ORANGE:
				return new Color(204, 153, 254);
            case ORCHID:
				return new Color(102, 0, 102);
            case PALE_BLUE:
				return new Color(152, 204, 255);
            case PINK:
            case PINK1:
				return new Color(255, 0, 255);
            case PLUM:
				return new Color(153, 51, 102);
            case RED:
            case RED1:
				return new Color(255, 0, 0);
            case ROSE:
				return new Color(255, 153, 205);
            case ROYAL_BLUE:
				return new Color(0, 102, 204);
            case SEA_GREEN:
				return new Color(51, 151, 102);
            case SKY_BLUE:
				return new Color(0, 204, 255);
            case TAN:
				return new Color(255, 204, 153);
            case TEAL:
				return new Color(0, 128, 128);
            case TURQUOISE:
            case TURQUOISE1:
				return new Color(0, 255, 255);
            case VIOLET:
				return new Color(128, 0, 128);
            case WHITE:
            case WHITE1:
				return new Color(255, 255, 255);
            case YELLOW:
            case YELLOW1:
				return new Color(255, 255, 0);
			default:
				return null;
        }
    }
}
