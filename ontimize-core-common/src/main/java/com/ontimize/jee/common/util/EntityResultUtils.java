package com.ontimize.jee.common.util;

import java.security.InvalidParameterException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.tools.FastQSortAlgorithm;

public class EntityResultUtils {

	private static final Logger logger = LoggerFactory.getLogger(EntityResultUtils.class);

	/**
	 * @param entityResult
	 * @param recordValue
	 * @param index
	 * @use EntityResultTools.updateRecordValues(entityResult, recordValue, index);
	 */
	@Deprecated
	public static void updateRecordValues(final EntityResult entityResult, final Map recordValue, final int index) {
		com.ontimize.jee.common.dto.EntityResultTools.updateRecordValues(entityResult, recordValue, index);
	}

	/**
	 * @param entityResult
	 * @param kv
	 * @return
	 * @use EntityResultTools.getValuesKeysIndex(entityResult, kv);
	 */
	@Deprecated
	public static int getValuesKeysIndex(final Map entityResult, final Map kv) {
		EntityResult er;
		if (entityResult instanceof EntityResult) {
			er = (EntityResult) entityResult;
		} else {
			er = new EntityResultMapImpl(new HashMap(entityResult));
		}
		return com.ontimize.jee.common.dto.EntityResultTools.getValuesKeysIndex(er, kv);
	}

	/**
	 * Joins the data in two EntityResult objects. These objects must have the same structure, is they
	 * have not it the method uses the structure of res1
	 * @param r1
	 * @param r2
	 * @return
	 */
	public static EntityResult merge(final EntityResult r1, final EntityResult r2) {
		if (r1.isEmpty()) {
			return r2.clone();
		}
		if (r2.isEmpty()) {
			return r1.clone();
		}
		// None of them are empty
		final EntityResult res1 = r1.clone();
		final Enumeration enumKeys = res1.keys();
		final int necordNumber2 = r2.calculateRecordNumber();
		while (enumKeys.hasMoreElements()) {
			final Object oKey = enumKeys.nextElement();
			final List vValues1 = (List) res1.get(oKey);
			final List vValues2 = (List) r2.get(oKey);
			if (vValues2 == null) {
				for (int i = 0; i < necordNumber2; i++) {
					vValues1.add(vValues1.size(), null);
				}
			} else {
				for (int i = 0; (i < vValues2.size()) && (i < necordNumber2); i++) {
					vValues1.add(vValues1.size(), vValues2.get(i));
				}
				for (int i = vValues1.size(); i < necordNumber2; i++) {
					vValues1.add(vValues1.size(), null);
				}
			}
		}
		return res1;
	}

	/**
	 * Combines all data in the Map h and the EntityResult r2. The combination is done in the next
	 * way:<BR>
	 * - All pairs (key-value) in the Map h are added in each record of r2.<BR>
	 * - NullValue objects in the Map h are null objects in the result.<BR>
	 * @param h
	 * @param r2
	 * @return
	 */
	public static EntityResult combine(final Map<?, ?> h, final EntityResult r2) {
		final EntityResult res = r2.clone();
		if (h.isEmpty()) {
			return res;
		}
		int r = res.calculateRecordNumber();
		// If r == 0, combine using 1
		if (r == 0) {
			r = 1;
		}
		for (final Entry<?, ?> entry : h.entrySet()) {
			final Object oKey = entry.getKey();
			Object oValue = entry.getValue();
			if (oValue instanceof NullValue) {
				oValue = null;
			}
			final List v = new Vector();
			for (int i = 0; i < r; i++) {
				v.add(i, oValue);
			}
			res.put(oKey, v);
		}
		return res;
	}

	/**
	 * Combines the data from two hashtables. The combination is the next:<BR>
	 * - All pairs (key-value) in the Map h are added in h2.<BR>
	 * - NullValue objects are null objects in the result.<BR>
	 * @param h
	 * @param h2
	 * @return
	 */
	public static EntityResult combine(final Map h, final Map h2) {
		final EntityResult rAux = new EntityResultMapImpl();
		rAux.putAll(h2);
		return EntityResultUtils.combine(h, rAux);
	}

	public static void addColumn(final EntityResult entityResult, final String columnName) {
		if (!entityResult.containsKey(columnName)) {
			final int recordNumber = entityResult.calculateRecordNumber();
			final Vector columnRecords = new Vector(recordNumber);
			columnRecords.setSize(recordNumber);
			entityResult.put(columnName, columnRecords);
		} else {
			EntityResultUtils.logger.warn("{} column already exists in this EntityResult", columnName);
		}
	}

	public static Map<Object, Object> toMap(final Object ob) {
		if (ob instanceof Map) {
			return (Map) ob;
		} else if (ob instanceof EntityResult) {
			final EntityResult res = (EntityResult) ob;
			final Map<Object, Object> map = new HashMap<>();
			for (final Object key : res.keySet()) {
				map.put(key, res.get(key));
			}
			return map;
		}
		throw new InvalidParameterException("must be Map or EntityResult");
	}

	/**
	 * Do sort.
	 *
	 * @param res
	 *            the res
	 * @param cols
	 *            the cols
	 * @return the entity result
	 */
	public static EntityResult doSort(final EntityResult res, final String... cols) {
		if (cols.length == 0) {
			return res;
		}
		EntityResult sortedResult = res;
		// Ordenar en orden inverso por cada columna
		for (int i = cols.length - 1; i >= 0; i--) {
			sortedResult = doFastSort(sortedResult, cols[i]);
		}
		return sortedResult;
	}

	/**
	 * Do fast sort.
	 *
	 * @param a
	 *            the a
	 * @param col
	 *            the col
	 * @return the entity result
	 */
	private static EntityResult doFastSort(final EntityResult a, final String col) {
		if (a.calculateRecordNumber() < 2) {
			return a;
		}
		final Object[] keysSorted = ((List) a.get(col)).toArray();
		final int[] indexes = FastQSortAlgorithm.sort(keysSorted);
		final List cols = new Vector(a.keySet());
		final EntityResult res = new EntityResultMapImpl();
		com.ontimize.jee.common.tools.EntityResultTools.initEntityResult(res, cols, indexes.length);
		for (final Object key : cols) {
			final List vOrig = (List) a.get(key);
			final List vDest = (List) res.get(key);
			for (int i = 0; i < indexes.length; i++) {
				vDest.add(i, vOrig.get(indexes[i]));
			}
		}
		return res;
	}

}
