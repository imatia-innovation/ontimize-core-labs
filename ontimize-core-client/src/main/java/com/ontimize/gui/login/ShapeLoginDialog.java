package com.ontimize.gui.login;

import java.awt.Color;
import java.awt.geom.RoundRectangle2D;
import java.util.Map;

import javax.swing.border.EmptyBorder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.Application;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.AWTUtilities;

public class ShapeLoginDialog extends LoginDialog {

	private static final Logger logger = LoggerFactory.getLogger(ShapeLoginDialog.class);

	public static boolean opaque = true;

	public static boolean shape = true;

	public ShapeLoginDialog(final Application mainApplication, final Map parameters, final EntityReferenceLocator locator) {
		super(mainApplication, parameters, locator);
		try {
			if (ShapeLoginDialog.shape) {
				AWTUtilities.setWindowShape(this,
						new RoundRectangle2D.Float(0, 0, this.getWidth(), this.getHeight(), 20, 20));
			}
			if (!ShapeLoginDialog.opaque) {
				AWTUtilities.setWindowOpaque(this, ShapeLoginDialog.opaque);
				this.getRootPane().setBackground(new Color(0, 0, 0, 1));
				this.getRootPane().setBorder(new EmptyBorder(0, 0, 0, 0));
			}
		} catch (final Exception e) {
			ShapeLoginDialog.logger.error(null, e);
		}
	}

}
