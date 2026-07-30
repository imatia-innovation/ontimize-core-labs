package com.ontimize.jee.desktopclient.dms.viewer;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.table.RefreshTableEvent;
import com.ontimize.gui.table.Table.RefreshThread;
import com.ontimize.jee.desktopclient.components.messaging.MessageManager;

public class DocumentationTableRefreshThread extends RefreshThread {

	private static final Logger logger = LoggerFactory.getLogger(DocumentationTableRefreshThread.class);

	/**
	 * Creates the thread for the corresponding table.
	 *
	 * @param table
	 *            the table
	 */
	public DocumentationTableRefreshThread(DocumentationTable table) {
		super(table);
	}

	private DocumentationTable getTable() {
		return (DocumentationTable) this.table;
	}

	/**
	 * Executes the table query.
	 */
	@Override
	public void run() {
		boolean ok = true;
		try {
			//this.getTable().fireRefreshBackgroundStart();
			if (this.delay > 0) {
				Thread.sleep(this.delay);
			}
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					DocumentationTableRefreshThread.this.getTable().showWaitPanel();
					DocumentationTableRefreshThread.this.getTable().repaint();
				}
			});

			this.getTable().requeryDocuments();

			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					DocumentationTableRefreshThread.this.getTable().hideWaitPanel();
				}
			});
		} catch (final Exception e) {
			ok = false;
			DocumentationTableRefreshThread.logger.error(null, e);
			SwingUtilities.invokeLater(new Runnable() {

				@Override
				public void run() {
					Throwable causeEx = MessageManager.getMessageManager().getCauseException(e);
					DocumentationTableRefreshThread.this.getTable().showInformationPanel((causeEx == null ? e : causeEx).getMessage());
				}
			});
		} finally {
			if (!this.isStop()) {
				this.setStop(true);
				final boolean okFinal = ok;
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						synchronized (DocumentationTableRefreshThread.this.getTable().getJTable()) {
							//DocumentationTableRefreshThread.this.getTable().fireRefreshBackgroundFinished();
							/*
							 * sin comentarios... ni copiar, ademas encima de usar enumerados meten estaticas...
							 */
							DocumentationTableRefreshThread.this.getTable().fireRefreshTableEvent(
							        new RefreshTableEvent(DocumentationTableRefreshThread.this.table, okFinal ? RefreshTableEvent.OK : RefreshTableEvent.ERROR));
						}

					}
				});
			}
		}
	}

}
