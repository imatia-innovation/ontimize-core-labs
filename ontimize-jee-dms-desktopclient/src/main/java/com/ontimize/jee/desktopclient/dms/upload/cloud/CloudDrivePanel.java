package com.ontimize.jee.desktopclient.dms.upload.cloud;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.container.Row;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.exceptions.DmsException;
import com.ontimize.jee.common.exceptions.DmsRuntimeException;
import com.ontimize.jee.common.tools.ParseUtilsExtended;
import com.ontimize.jee.common.tools.ReflectionTools;

public class CloudDrivePanel<T extends Serializable> extends Row implements Internationalization, ICloudFileSelectionListener<T> {

	private static final long				serialVersionUID	= 1L;
	private CloudDriveTable<T>				table;
	private ICloudFileSelectionListener<T>	selectionListener;

	/**
	 * Instantiates a new ExpressionDataField.
	 *
	 * @param parameters
	 *            the parameters
	 * @throws Exception
	 * @throws IOException
	 */
	public CloudDrivePanel(final Map parameters) {
		super(parameters);
	}

	@Override
	public void init(final Map parameters) {
		super.init(parameters);
		this.setLayout(new BorderLayout());
		try {
			this.table = new CloudDriveTable<>(this,
					(ICloudManager<T>) ReflectionTools.invoke(ParseUtilsExtended.getClazz((String) parameters.get("cloudmanagerclass"), null), "getInstance", null));
		} catch (final ClassNotFoundException e1) {
			throw new DmsRuntimeException(e1);
		}
		this.add(new JScrollPane(this.table), BorderLayout.CENTER);
		final JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
		this.add(buttonsPanel, BorderLayout.SOUTH);
		final JButton openButton = new JButton(ApplicationManager.getTranslation("dms.open_file"));
		buttonsPanel.add(openButton);
		openButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CloudDrivePanel.this.table.onFileSelected();
			}
		});

		final JButton cancelButton = new JButton(ApplicationManager.getTranslation("dms.cancel"));
		buttonsPanel.add(cancelButton);
		cancelButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {
				CloudDrivePanel.this.onFileSelected(null);
			}
		});
	}

	@Override
	public Object getConstraints(final LayoutManager parentLayout) {
		return new GridBagConstraints(-1, -1, 1, 1, 1, 1, GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0);
	}

	public T getSelectedFile() {
		return this.table.getSelectedFile();
	}

	public void setSelectionListener(final ICloudFileSelectionListener<T> selectionListener) {
		this.selectionListener = selectionListener;
	}

	@Override
	public void onFileSelected(final T file) {
		if (this.selectionListener != null) {
			this.selectionListener.onFileSelected(file);
		}
	}

	public void startNavigation() throws DmsException {
		this.table.startNavigation();
	}
}
