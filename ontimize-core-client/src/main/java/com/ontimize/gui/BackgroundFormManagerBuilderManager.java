package com.ontimize.gui;

import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.gui.manager.IFormManager;

public class BackgroundFormManagerBuilderManager {

	private static final Logger logger = LoggerFactory.getLogger(BackgroundFormManagerBuilderManager.class);

	public static int runningThreadNumber = 4;

	protected static List<Thread> waitingList = new Vector<Thread>();

	protected static List<Thread> runningList = new Vector<Thread>();

	protected static class FormManagerBuilderThread extends Thread {

		protected IFormManager backgroundFormManager;

		FormManagerBuilderThread(final IFormManager backgroundFormManager) {
			super("FormManagerBuilder:" + backgroundFormManager.getId());
			this.backgroundFormManager = backgroundFormManager;
		}

		@Override
		public void run() {
			super.run();
			try {
				this.backgroundFormManager.loadInEDTh();
			} catch (final Exception e) {
				BackgroundFormManagerBuilderManager.logger.error(this.getClass().getName()
						+ ": Thread error building form manager -> " + this.backgroundFormManager.getId(), e);
			} finally {
				BackgroundFormManagerBuilderManager.finishThread(this);
				this.backgroundFormManager = null;
			}
		}

	}

	public static void finishThread(final Thread th) {
		synchronized (BackgroundFormManagerBuilderManager.runningList) {
			BackgroundFormManagerBuilderManager.runningList.remove(th);
			if (BackgroundFormManagerBuilderManager.waitingList.size() > 0) {
				final FormManagerBuilderThread thread = (FormManagerBuilderThread) BackgroundFormManagerBuilderManager.waitingList
						.remove(0);
				BackgroundFormManagerBuilderManager.runningList.add(thread);
				thread.start();
			}
		}
	}

	public static void buildFormManager(final IFormManager formManager) {
		synchronized (BackgroundFormManagerBuilderManager.runningList) {
			final FormManagerBuilderThread thread = new FormManagerBuilderThread(formManager);
			thread.setPriority(Thread.MIN_PRIORITY);
			if (BackgroundFormManagerBuilderManager.runningList
					.size() < BackgroundFormManagerBuilderManager.runningThreadNumber) {
				BackgroundFormManagerBuilderManager.runningList.add(thread);
				thread.start();
			} else {
				BackgroundFormManagerBuilderManager.waitingList.add(thread);
			}
		}
	}

}
