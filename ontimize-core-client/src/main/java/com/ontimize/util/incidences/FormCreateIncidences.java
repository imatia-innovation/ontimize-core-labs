package com.ontimize.util.incidences;

import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Robot;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.WindowConstants;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.impl.StaticLoggerBinder;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.MainApplication;
import com.ontimize.gui.MessageDialog;
import com.ontimize.gui.button.Button;
import com.ontimize.gui.button.FormHeaderButton;
import com.ontimize.gui.container.EJDialog;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.MemoDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.jee.common.locator.UtilReferenceLocator;
import com.ontimize.jee.common.util.incidences.IIncidenceService;

public class FormCreateIncidences extends EJDialog implements Internationalization {

	protected ResourceBundle resourceBundle;

	protected EntityReferenceLocator locator;

	protected IIncidenceService remoteReference;

	protected BufferedImage bufferedImage;

	protected MemoDataField descriptionDataField;

	protected TextDataField subjectDataField;

	private static final Logger logger = LoggerFactory.getLogger(FormCreateIncidences.class);

	public FormCreateIncidences(final Object eventSourceObject) {
		super(SwingUtilities.getWindowAncestor((Component) eventSourceObject),
				ApplicationManager.getTranslation("M_CREATE_INCIDENCE_TITLE"), true);
		try {
			this.locator = ApplicationManager.getApplication().getReferenceLocator();
			this.remoteReference = (IIncidenceService) ((UtilReferenceLocator) this.locator)
					.getRemoteReference(IIncidenceService.REMOTE_NAME, this.locator.getSessionId());
			if (eventSourceObject instanceof JPanel) {
				final Window w = SwingUtilities.getWindowAncestor((Component) eventSourceObject);
				this.bufferedImage = new Robot().createScreenCapture(w.getBounds());
			} else if (eventSourceObject instanceof FormHeaderButton) {
				final Window w =  ApplicationManager.getApplication().getFrame();
				this.bufferedImage = new Robot().createScreenCapture(w.getBounds());
			} else if (eventSourceObject instanceof JButton) {
				Window w = SwingUtilities.getWindowAncestor((Component) eventSourceObject).getOwner();

				if (!(w instanceof JFrame) && !(w instanceof JDialog) && !(w instanceof MainApplication)) {
					w = ApplicationManager.getApplication().getFrame();
				}

				// if (w instanceof){
				//
				// }
				this.bufferedImage = new Robot().createScreenCapture(w.getBounds());
			}

			// TODO TEST IMAGE
			final File outputFile = new File(System.getProperty("java.io.tmpdir") + "incidencetest.png");
			ImageIO.write(this.bufferedImage, "png", outputFile);
			//

			// this.bufferedImage = img;
			// FormCreateIncidences.setActionForKey(KeyEvent.VK_I,
			// InputEvent.CTRL_MASK | InputEvent.SHIFT_MASK, null,
			// "Create incidence");

			// this.bufferedImage = new
			// Robot().createScreenCapture(this.getBounds());

			this.createFormIncidences();
			this.pack();
		} catch (final Exception e) {
			FormCreateIncidences.logger.error("Remote referene error. ERROR: {}", e.getMessage(), e);
		}

	}

	protected void createFormIncidences() {
		this.subjectDataField = this.createAndConfigureSubjectDataField();
		this.descriptionDataField = this.createAndConfigureMemoDataField();
		final Button acceptButton = this.createAndConfigureButtonAccept();
		final Button cancelButton = this.createAndConfigureButtonCancel();

		this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		final GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 1.0, 0.0, Double.MIN_VALUE };
		this.getContentPane().setLayout(gridBagLayout);

		final GridBagConstraints gbc_subjectDataField = new GridBagConstraints();
		gbc_subjectDataField.insets = new Insets(0, 0, 5, 0);
		gbc_subjectDataField.anchor = GridBagConstraints.EAST;
		gbc_subjectDataField.fill = GridBagConstraints.BOTH;
		gbc_subjectDataField.gridwidth = 2;
		gbc_subjectDataField.gridx = 0;
		gbc_subjectDataField.gridy = 0;
		this.getContentPane().add(this.subjectDataField, gbc_subjectDataField);

		final GridBagConstraints gbc_memoDataField = new GridBagConstraints();
		gbc_memoDataField.gridwidth = 2;
		gbc_memoDataField.insets = new Insets(0, 0, 5, 0);
		gbc_memoDataField.fill = GridBagConstraints.BOTH;
		gbc_memoDataField.gridx = 0;
		gbc_memoDataField.gridy = 1;
		this.getContentPane().add(this.descriptionDataField, gbc_memoDataField);

		final GridBagConstraints gbc_acceptButton = new GridBagConstraints();
		gbc_acceptButton.insets = new Insets(0, 0, 0, 5);
		gbc_acceptButton.anchor = GridBagConstraints.CENTER;
		gbc_acceptButton.weightx = 1.0;
		gbc_acceptButton.gridx = 0;
		gbc_acceptButton.gridy = 2;
		this.getContentPane().add(acceptButton, gbc_acceptButton);

		final GridBagConstraints gbc_cancelButton = new GridBagConstraints();
		gbc_cancelButton.anchor = GridBagConstraints.CENTER;
		gbc_cancelButton.weightx = 1.0;
		gbc_cancelButton.gridx = 1;
		gbc_cancelButton.gridy = 2;
		this.getContentPane().add(cancelButton, gbc_cancelButton);

	}

	private TextDataField createAndConfigureSubjectDataField() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, IIncidenceService.INCIDENCES_SUBJECT);
		h.put(DataField.REQUIRED, "yes");
		h.put(DataField.DIM, "text");
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation(IIncidenceService.INCIDENCES_SUBJECT));

		final TextDataField subjectDataField = new TextDataField(h);
		return subjectDataField;
	}

	protected MemoDataField createAndConfigureMemoDataField() {
		final Map h = new Hashtable();
		h.put(DataField.ATTR, IIncidenceService.INCIDENCES_DESCRIPTION);
		h.put(DataField.REQUIRED, "no");
		h.put(DataField.EXPAND, "yes");
		h.put(DataField.DIM, "text");
		h.put(DataField.LABELPOSITION, "top");
		h.put(DataField.TEXT_STR, ApplicationManager.getTranslation("FORM_INCIDENCES_LABEL_TEXT"));
		h.put("rows", "8");

		final MemoDataField messageMemo = new MemoDataField(h);
		return messageMemo;
	}

	protected Button createAndConfigureButtonCancel() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "cancelButton");
		h.put(Button.TEXT, ApplicationManager.getTranslation("application.cancel"));
		final Button cancelButton = new Button(h);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				FormCreateIncidences.this.dispose();
			}
		});

		return cancelButton;

	}

	protected Button createAndConfigureButtonAccept() {
		final Map h = new Hashtable();
		h.put(Button.KEY, "acceptButton");
		h.put(Button.TEXT, ApplicationManager.getTranslation("application.accept"));
		final Button acceptButton = new Button(h);
		acceptButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				if (!FormCreateIncidences.this.subjectDataField.isEmpty()) {
					final StaticLoggerBinder binder = StaticLoggerBinder.getSingleton();
					final IIncidenceLogger logger = IncidenceLoggerFactory
							.incidenceLoggerInstance(binder.getLoggerFactoryClassStr());
					final ByteArrayOutputStream output = logger.getCompressClientLogger();
					if ((output != null) && (FormCreateIncidences.this.bufferedImage != null)
							&& (FormCreateIncidences.this.descriptionDataField != null)) {
						ByteArrayOutputStream baos = null;
						try {
							baos = new ByteArrayOutputStream();
							ImageIO.write(FormCreateIncidences.this.bufferedImage, "png", baos);
							baos.flush();
							final byte[] imageInByte = baos.toByteArray();
							baos.close();
							FormCreateIncidences.this.remoteReference.createIncidende(
									FormCreateIncidences.this.descriptionDataField.getText(),
									FormCreateIncidences.this.subjectDataField.getText(), imageInByte,
									output.toByteArray(), FormCreateIncidences.this.locator.getSessionId());
							JOptionPane.showMessageDialog((Component) e.getSource(),
									ApplicationManager.getTranslation("M_CREATED_INCIDENCE"));
							FormCreateIncidences.this.dispose();
						} catch (final Exception e1) {
							MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor((Component) e.getSource()),
									ApplicationManager.getTranslation("M_NOT_CREATED_INCIDENCE"));
							FormCreateIncidences.logger.error("Error sending incidence. ERROR: {}", e1.getMessage(),
									e1);
						} finally {
							try {
								if (baos != null) {
									baos.close();
								}
							} catch (final IOException e1) {
								FormCreateIncidences.logger.error("Error closing output stream {}", e1.getMessage(),
										e1);
							}
						}
					}

				} else {
					MessageDialog.showErrorMessage(SwingUtilities.getWindowAncestor((Component) e.getSource()),
							ApplicationManager.getTranslation("M_SUBJECT_FIELD_EMPTY"));
				}
			}
		});
		return acceptButton;
	}

	@Override
	public void setComponentLocale(final Locale l) {
		// TODO Auto-generated method stub
	}

	@Override
	public void setResourceBundle(final ResourceBundle resourceBundle) {
		this.resourceBundle = resourceBundle;

	}

	@Override
	public List getTextsToTranslate() {
		// TODO Auto-generated method stub
		return null;
	}

}
