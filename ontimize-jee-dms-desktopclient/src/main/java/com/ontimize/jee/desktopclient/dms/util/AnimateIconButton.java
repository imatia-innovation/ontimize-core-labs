package com.ontimize.jee.desktopclient.dms.util;

import java.util.Timer;
import java.util.TimerTask;

import javax.swing.AbstractButton;
import javax.swing.ImageIcon;
import javax.swing.SwingUtilities;

import com.ontimize.gui.images.ImageManager;

/**
 *
 * Class to animate icons in buttons. It has two static methods. <br> {@link #animate(AbstractButton, String, String, int, long) animate} <br>
 * {@link #animateGIF(AbstractButton, String) animateGIF}
 *
 */
public class AnimateIconButton {

	public AnimateIconButton() {}

	/**
	 * Animate a button with alternated defaultIcon and alertIcon a finite number of cycles. The transition of the animations is defined in the period argument.
	 *
	 * @param button
	 *            button to apply the animation
	 * @param defaultIcon
	 *            default icon to show when animation stops
	 * @param alertIcon
	 *            icon to show to alternate with defaultIcon while animation is running
	 * @param cycles
	 *            number of cycles to repeat the animation
	 * @param period
	 *            time in milliseconds between successive task executions.
	 */
	public static void animate(final AbstractButton button, final String defaultIcon, final String alertIcon, final int cycles, final long period) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (alertIcon != null) {
					Timer t1 = new Timer();
					t1.schedule(new TaskAnimateIcon(button, t1, defaultIcon, alertIcon, cycles), 0, period);
				} else {
					button.setIcon(ImageManager.getIcon(defaultIcon));
					button.setSelectedIcon(ImageManager.getIcon(defaultIcon));
					button.setRolloverIcon(ImageManager.getIcon(defaultIcon));
					button.setRolloverSelectedIcon(ImageManager.getIcon(defaultIcon));
					button.setPressedIcon(ImageManager.getIcon(defaultIcon));
				}
			}
		});
	}

	/**
	 * Animate a button with a GIF image
	 *
	 * @param button
	 *            button to apply the animation
	 * @param icon
	 *            complete path's name where is located the gif
	 */
	public static void animateGIF(final AbstractButton button, final String icon) {
		AnimateIconButton.animateGIF(button, ImageManager.getIcon(icon));
	}

	/**
	 * Animate a button with a GIF image
	 *
	 * @param button
	 *            button to apply the animation
	 * @param icon
	 *            complete path's name where is located the gif
	 */
	public static void animateGIF(final AbstractButton button, final ImageIcon imageIcon) {
		SwingUtilities.invokeLater(new Runnable() {

			@Override
			public void run() {
				if (imageIcon != null) {
					button.setIcon(imageIcon);
					button.setSelectedIcon(imageIcon);
					button.setRolloverIcon(imageIcon);
					button.setRolloverSelectedIcon(imageIcon);
					button.setPressedIcon(imageIcon);
				}
			}
		});
	}
}

/**
 *
 * Task that animate the button
 *
 */
class TaskAnimateIcon extends TimerTask {

	private int						animationTimes	= 10;
	private int						counter			= 0;
	private final ImageIcon			defaultIco;
	private final ImageIcon			alertIco;
	private final Timer				t1;
	private final AbstractButton	button;

	public TaskAnimateIcon(AbstractButton button, Timer t1, String defaultIco, String alertIco, int cycles) {
		this.defaultIco = ImageManager.getIcon(defaultIco);
		this.alertIco = ImageManager.getIcon(alertIco);
		this.t1 = t1;
		this.button = button;
		this.animationTimes = cycles;
	}

	@Override
	public void run() {
		if (this.button.getSelectedIcon() != null) {
			this.button.setSelectedIcon(null);
			this.button.setRolloverIcon(null);
			this.button.setRolloverSelectedIcon(null);
			this.button.setPressedIcon(null);
		}
		if (this.counter < this.animationTimes) {
			if ((this.counter % 2) == 0) {
				this.button.setIcon(this.defaultIco);
			} else {
				this.button.setIcon(this.alertIco);
			}
			this.counter++;
		} else {
			this.t1.cancel();
		}
	}
}
