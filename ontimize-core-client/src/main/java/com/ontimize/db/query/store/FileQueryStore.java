package com.ontimize.db.query.store;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.db.query.QueryExpression;
import com.ontimize.jee.common.db.query.store.QueryStore;

public class FileQueryStore implements QueryStore {

	private static final Logger logger = LoggerFactory.getLogger(FileQueryStore.class);

	private static final String DEFAULT_DIRECTORY = System.getProperty("user.home");

	private static String SUBDIR_NAME = ".query";

	private static final String EXTENSION = ".qry";

	public static final String NULL_ENTITY = "NULL_ENTITY";

	private String storePath = null;

	public FileQueryStore() {
		this.storePath = FileQueryStore.DEFAULT_DIRECTORY + File.separator + FileQueryStore.SUBDIR_NAME
				+ (com.ontimize.gui.ApplicationManager.getApplication()
						.getName() != null ? File.separator + com.ontimize.gui.ApplicationManager.getApplication().getName()
								: "");

	}

	/**
	 * addQuery
	 * @param description String
	 * @param query QueryExpression
	 */
	@Override
	public void addQuery(final String description, final QueryExpression query) {
		try {
			this.save(description, query);
		} catch (final Exception ex) {
			FileQueryStore.logger.error(null, ex);
		}
	}

	protected String generateFileName(final String description, final String entity) {
		if (description == null) {
			throw new IllegalArgumentException("Id can´t be null");
		}

		String e = FileQueryStore.NULL_ENTITY;
		if (entity != null) {
			e = "" + entity.hashCode();
		}
		return description + "-" + e + FileQueryStore.EXTENSION;
	}

	private void save(final String description, final QueryExpression query) throws IOException {
		final String name = this.generateFileName(description, query.getEntity());
		final File fAux = new File(this.storePath);
		fAux.mkdirs();
		final File f = new File(this.storePath, name);
		final FileOutputStream fOut = new FileOutputStream(f);
		try {
			final ObjectOutputStream out = new ObjectOutputStream(fOut);
			out.writeObject(query);
			out.flush();
			out.close();
		} catch (final IOException e) {
			FileQueryStore.logger.error(null, e);
			throw e;
		} finally {
			if (fOut != null) {
				fOut.close();
			}
		}
	}

	private QueryExpression load(final String description, final String entity) throws IOException {
		final String name = this.generateFileName(description, entity);
		final File f = new File(this.storePath, name);
		if (!f.exists()) {
			return null;
		}
		final FileInputStream fIn = new FileInputStream(f);

		try {
			final ObjectInputStream in = new ObjectInputStream(fIn);
			final Object o = in.readObject();
			if (o instanceof QueryExpression) {
				return (QueryExpression) o;
			} else {
				return null;
			}
		} catch (final IOException e) {
			FileQueryStore.logger.error(null, e);
			throw e;
		} catch (final ClassNotFoundException ex) {
			FileQueryStore.logger.error(null, ex);
			return null;
		} finally {
			if (fIn != null) {
				fIn.close();
			}
		}
	}

	/**
	 * removeQuery
	 * @param description String
	 */
	@Override
	public void removeQuery(final String description, final String entity) {
		try {
			this.delete(description, entity);
		} catch (final Exception ex) {
			FileQueryStore.logger.error(null, ex);
		}
	}

	private void delete(final String description, final String entity) throws IOException {
		final String name = this.generateFileName(description, entity);
		final File f = new File(this.storePath, name);
		if (!f.exists()) {
			return;
		}
		f.delete();
	}

	/**
	 * list
	 * @param entity String
	 * @return QueryExpression[]
	 */
	@Override
	public String[] list(final String entity) {
		String filter = new String();
		if (entity == null) {
			filter = "-" + FileQueryStore.NULL_ENTITY + FileQueryStore.EXTENSION;
		} else {
			filter = "-" + String.valueOf(entity.hashCode()) + FileQueryStore.EXTENSION;
		}

		final File f = new File(this.storePath);
		if (!f.exists()) {
			return new String[0];
		}
		final String[] list = f.list();
		String[] filteredList = new String[0];

		for (int i = 0; i < list.length; i++) {
			String sCurrent = list[i];
			if (sCurrent.indexOf(filter) != -1) {
				sCurrent = sCurrent.replaceFirst(filter, "");
				final String[] aux = new String[filteredList.length + 1];
				System.arraycopy(filteredList, 0, aux, 0, filteredList.length);
				aux[filteredList.length] = sCurrent;
				filteredList = aux;
			}
		}
		return filteredList;
	}

	/**
	 * get
	 * @param description String
	 * @param entity String
	 * @return QueryExpression
	 */
	@Override
	public QueryExpression get(final String description, final String entity) {
		try {
			return this.load(description, entity);
		} catch (final Exception ex) {
			FileQueryStore.logger.error(null, ex);
			return null;
		}
	}

}
