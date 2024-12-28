package com.ontimize.util.swing.list;

import java.text.Collator;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Vector;

import javax.swing.DefaultListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.jee.common.db.NullValue;

public class SortedListModel extends DefaultListModel implements ListDataListener {

	public static Collator comparator = Collator.getInstance();

	private static final Logger logger = LoggerFactory.getLogger(SortedListModel.class);

	public enum SortOrder {

		UNORDERED, // Leaves the model in its original order
		ASCENDING, // Produces a sort in ascending order
		DESCENDING; // Produces a sort in descending order

	}

	protected DefaultListModel model;

	protected int indexes[] = new int[0];

	protected SortOrder sortOrder = SortOrder.UNORDERED;

	public SortedListModel() {
		this.model = new DefaultListModel();
		this.model.addListDataListener(this);
	}

	public SortOrder getSortOrder() {
		return this.sortOrder;
	}

	public void setSortOrder(final SortOrder sortOrder) {
		this.sortOrder = sortOrder;
		this.sort();
		this.fireContentsChanged(this, 0, this.getSize());
	}

	/**
	 * Returns the length of the list.
	 * @return the length of the list
	 */
	@Override
	public int getSize() {
		return this.model.getSize();
	}

	/**
	 * Returns the value at the specified index.
	 * @param index the requested index
	 * @return the value at <code>index</code>
	 */
	@Override
	public Object getElementAt(final int index) {
		return this.model.getElementAt(this.indexes[index]);
	}

	@Override
	public void copyInto(final Object anArray[]) {
		this.model.copyInto(anArray);
	}

	/**
	 * Trims the capacity of this list to be the list's current size.
	 *
	 * @see Vector#trimToSize()
	 */
	@Override
	public void trimToSize() {
		this.model.trimToSize();
	}

	/**
	 * Increases the capacity of this list, if necessary, to ensure that it can hold at least the number
	 * of components specified by the minimum capacity argument.
	 * @param minCapacity the desired minimum capacity
	 * @see Vector#ensureCapacity(int)
	 */
	@Override
	public void ensureCapacity(final int minCapacity) {
		this.model.ensureCapacity(minCapacity);
	}

	/**
	 * Sets the size of this list.
	 * @param newSize the new size of this list
	 * @see Vector#setSize(int)
	 */
	@Override
	public void setSize(final int newSize) {
		this.model.setSize(newSize);
	}

	@Override
	public int capacity() {
		return this.model.capacity();
	}

	/**
	 * Returns the number of components in this list.
	 * @return the number of components in this list
	 * @see Vector#size()
	 */
	@Override
	public int size() {
		return this.model.size();
	}

	/**
	 * Tests whether this list has any components.
	 * @return <code>true</code> if and only if this list has no components, that is, its size is zero;
	 *         <code>false</code> otherwise
	 * @see Vector#isEmpty()
	 */
	@Override
	public boolean isEmpty() {
		return this.model.isEmpty();
	}

	/**
	 * Returns an enumeration of the components of this list.
	 * @return an enumeration of the components of this list
	 * @see Vector#elements()
	 */
	@Override
	public Enumeration<?> elements() {
		return this.model.elements();
	}

	/**
	 * Tests whether the specified object is a component in this list.
	 * @param elem an object
	 * @return <code>true</code> if the specified object is the same as a component in this list
	 * @see Vector#contains(Object)
	 */
	@Override
	public boolean contains(final Object elem) {
		return this.model.contains(elem);
	}

	/**
	 * Searches for the first occurrence of <code>elem</code>.
	 * @param elem an object
	 * @return the index of the first occurrence of the argument in this list; returns <code>-1</code>
	 *         if the object is not found
	 * @see Vector#indexOf(Object)
	 */
	@Override
	public int indexOf(final Object elem) {
		return this.model.indexOf(elem);
	}

	/**
	 * Searches for the first occurrence of <code>elem</code>, beginning the search at
	 * <code>index</code>.
	 * @param elem an desired component
	 * @param index the index from which to begin searching
	 * @return the index where the first occurrence of <code>elem</code> is found after
	 *         <code>index</code>; returns <code>-1</code> if the <code>elem</code> is not found in the
	 *         list
	 * @see Vector#indexOf(Object,int)
	 */
	@Override
	public int indexOf(final Object elem, final int index) {
		return this.model.indexOf(elem, index);
	}

	/**
	 * Returns the index of the last occurrence of <code>elem</code>.
	 * @param elem the desired component
	 * @return the index of the last occurrence of <code>elem</code> in the list; returns
	 *         <code>-1</code> if the object is not found
	 * @see Vector#lastIndexOf(Object)
	 */
	@Override
	public int lastIndexOf(final Object elem) {
		return this.model.lastIndexOf(elem);
	}

	/**
	 * Searches backwards for <code>elem</code>, starting from the specified index, and returns an index
	 * to it.
	 * @param elem the desired component
	 * @param index the index to start searching from
	 * @return the index of the last occurrence of the <code>elem</code> in this list at position less
	 *         than <code>index</code>; returns <code>-1</code> if the object is not found
	 * @see Vector#lastIndexOf(Object,int)
	 */
	@Override
	public int lastIndexOf(final Object elem, final int index) {
		return this.model.lastIndexOf(elem, index);
	}

	/**
	 * Returns the component at the specified index. Throws an
	 * <code>ArrayIndexOutOfBoundsException</code> if the index is negative or not less than the size of
	 * the list. <blockquote> <b>Note:</b> Although this method is not deprecated, the preferred method
	 * to use is <code>get(int)</code>, which implements the <code>List</code> interface defined in the
	 * 1.2 Collections framework. </blockquote>
	 * @param index an index into this list
	 * @return the component at the specified index
	 * @see #get(int)
	 * @see Vector#elementAt(int)
	 */
	@Override
	public Object elementAt(final int index) {
		return this.model.elementAt(this.indexes[index]);
	}

	/**
	 * Returns the first component of this list. Throws a <code>NoSuchElementException</code> if this
	 * List has no components.
	 * @return the first component of this list
	 * @see Vector#firstElement()
	 */
	@Override
	public Object firstElement() {
		return this.model.getElementAt(this.indexes[0]);
	}

	/**
	 * Returns the last component of the list. Throws a <code>NoSuchElementException</code> if this
	 * List has no components.
	 * @return the last component of the list
	 * @see Vector#lastElement()
	 */
	@Override
	public Object lastElement() {
		return this.model.getElementAt(this.indexes[this.indexes.length - 1]);
	}

	/**
	 * Sets the component at the specified <code>index</code> of this list to be the specified object.
	 * The previous component at that position is discarded.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is invalid. <blockquote>
	 * <b>Note:</b> Although this method is not deprecated, the preferred method to use is
	 * <code>set(int,Object)</code>, which implements the <code>List</code> interface defined in the 1.2
	 * Collections framework. </blockquote>
	 * @param obj what the component is to be set to
	 * @param index the specified index
	 * @see #set(int,Object)
	 * @see Vector#setElementAt(Object,int)
	 */
	@Override
	public void setElementAt(final Object obj, final int index) {
		this.model.setElementAt(obj, index);
	}

	/**
	 * Deletes the component at the specified index.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is invalid. <blockquote>
	 * <b>Note:</b> Although this method is not deprecated, the preferred method to use is
	 * <code>remove(int)</code>, which implements the <code>List</code> interface defined in the 1.2
	 * Collections framework. </blockquote>
	 * @param index the index of the object to remove
	 * @see #remove(int)
	 * @see Vector#removeElementAt(int)
	 */
	@Override
	public void removeElementAt(final int index) {
		this.model.removeElementAt(this.indexes[index]);
	}

	/**
	 * Inserts the specified object as a component in this list at the specified <code>index</code>.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is invalid. <blockquote>
	 * <b>Note:</b> Although this method is not deprecated, the preferred method to use is
	 * <code>add(int,Object)</code>, which implements the <code>List</code> interface defined in the 1.2
	 * Collections framework. </blockquote>
	 * @param obj the component to insert
	 * @param index where to insert the new component
	 * @exception ArrayIndexOutOfBoundsException if the index was invalid
	 * @see #add(int,Object)
	 * @see Vector#insertElementAt(Object,int)
	 */
	@Override
	public void insertElementAt(final Object obj, final int index) {
		this.model.insertElementAt(obj, index);
	}

	/**
	 * Adds the specified component to the end of this list.
	 * @param obj the component to be added
	 * @see Vector#addElement(Object)
	 */
	@Override
	public void addElement(final Object obj) {
		this.model.addElement(obj);
	}

	/**
	 * Removes the first (lowest-indexed) occurrence of the argument from this list.
	 * @param obj the component to be removed
	 * @return <code>true</code> if the argument was a component of this list; <code>false</code>
	 *         otherwise
	 * @see Vector#removeElement(Object)
	 */
	@Override
	public boolean removeElement(final Object obj) {
		final boolean rv = this.model.removeElement(obj);
		return rv;
	}

	/**
	 * Removes all components from this list and sets its size to zero. <blockquote> <b>Note:</b>
	 * Although this method is not deprecated, the preferred method to use is <code>clear</code>, which
	 * implements the <code>List</code> interface defined in the 1.2 Collections framework.
	 * </blockquote>
	 *
	 * @see #clear()
	 * @see Vector#removeAllElements()
	 */
	@Override
	public void removeAllElements() {
		this.model.removeAllElements();
	}

	/**
	 * Returns a string that displays and identifies this object's properties.
	 * @return a String representation of this object
	 */
	@Override
	public String toString() {
		return this.model.toString();
	}

	/*
	 * The remaining methods are included for compatibility with the Java 2 platform List class.
	 */

	/**
	 * Returns an array containing all of the elements in this list in the correct order.
	 * @return an array containing the elements of the list
	 * @see Vector#toArray()
	 */
	@Override
	public Object[] toArray() {
		return this.model.toArray();
	}

	/**
	 * Returns the element at the specified position in this list.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is out of range
	 * (<code>index &lt; 0 ||
	 * index &gt;= size()</code>).
	 * @param index index of element to return
	 */
	@Override
	public Object get(final int index) {
		return this.model.get(this.indexes[index]);
	}

	/**
	 * Replaces the element at the specified position in this list with the specified element.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is out of range
	 * (<code>index &lt; 0 || index &gt;= size()</code>).
	 * @param index index of element to replace
	 * @param element element to be stored at the specified position
	 * @return the element previously at the specified position
	 */
	@Override
	public Object set(final int index, final Object element) {
		return this.set(index, element);
	}

	/**
	 * Inserts the specified element at the specified position in this list.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is out of range (<code>index
	 * &lt; 0 || index &gt; size()</code>).
	 * @param index index at which the specified element is to be inserted
	 * @param element element to be inserted
	 */
	@Override
	public void add(final int index, final Object element) {
		this.model.add(index, element);
	}

	/**
	 * Removes the element at the specified position in this list. Returns the element that was removed
	 * from the list.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index is out of range
	 * (<code>index &lt; 0 || index &gt;= size()</code>).
	 * @param index the index of the element to removed
	 */
	@Override
	public Object remove(final int index) {
		return this.model.remove(this.indexes[index]);
	}

	/**
	 * Removes all of the elements from this list. The list will be empty after this call returns
	 * (unless it throws an exception).
	 */
	@Override
	public void clear() {
		this.model.clear();
	}

	/**
	 * Deletes the components at the specified range of indexes. The removal is inclusive, so specifying
	 * a range of (1,5) removes the component at index 1 and the component at index 5, as well as all
	 * components in between.
	 * <p>
	 * Throws an <code>ArrayIndexOutOfBoundsException</code> if the index was invalid. Throws an
	 * <code>IllegalArgumentException</code> if <code>fromIndex &gt; toIndex</code>.
	 * @param fromIndex the index of the lower end of the range
	 * @param toIndex the index of the upper end of the range
	 * @see #remove(int)
	 */
	@Override
	public void removeRange(final int fromIndex, final int toIndex) {
		this.removeRange(fromIndex, toIndex);
	}

	@Override
	public void intervalAdded(final ListDataEvent e) {
		this.sort();
		this.fireIntervalAdded(this, e.getIndex0(), e.getIndex1());
	}

	@Override
	public void intervalRemoved(final ListDataEvent e) {
		this.sort();
		this.fireIntervalRemoved(this, e.getIndex0(), e.getIndex1());
	}

	@Override
	public void contentsChanged(final ListDataEvent e) {
		this.sort();
		this.fireContentsChanged(this, e.getIndex0(), e.getIndex1());
	}

	protected void sort() {
		final long t = System.currentTimeMillis();
		SortedListModel.logger.debug("Sorting of an array of {} records", this.indexes.length);
		this.reallocateIndexes();
		final int total = this.indexes.length;
		if (this.getSortOrder() != SortOrder.UNORDERED) {
			this.shuttleSort(this.indexes.clone(), this.indexes, 0, total);
		}
		final long t2 = System.currentTimeMillis();
		SortedListModel.logger.trace(" Sorting time ShuttleSort : {} millisecs", t2 - t);

	}

	/**
	 * Sets up a new array of indexes with the right number of elements for the new data model.
	 */
	public void reallocateIndexes() {
		final int rowCount = this.model.getSize();

		this.indexes = new int[rowCount];

		for (int row = 0; row < rowCount; row++) {
			this.indexes[row] = row;
		}
	}

	/**
	 * Fast algorithm to sort an array.
	 * @param from the original array
	 * @param to the sorted array
	 * @param low the starting index (typically 0)
	 * @param high the ending index (typically from.length)
	 */
	public void shuttleSort(final int from[], final int to[], final int low, final int high) {
		if ((high - low) < 2) {
			return;
		}
		final int middle = (low + high) / 2;
		this.shuttleSort(to, from, low, middle);
		this.shuttleSort(to, from, middle, high);

		int p = low;
		int q = middle;

		/*
		 * This is an optional short-cut; at each recursive call, check to see if the elements in this
		 * subset are already ordered. If so, no further comparisons are needed; the sub-array can just be
		 * copied. The array must be copied rather than assigned otherwise sister calls in the recursion
		 * might get out of sinc. When the number of elements is three they are partitioned so that the
		 * first set, [low, mid), has one element and and the second, [mid, high), has two. We skip the
		 * optimisation when the number of elements is three or less as the first compare in the normal
		 * merge will produce the same sequence of steps. This optimisation seems to be worthwhile for
		 * partially ordered lists but some analysis is needed to find out how the performance drops to
		 * Nlog(N) as the initial order diminishes - it may drop very quickly.
		 */

		if (((high - low) >= 4) && (this.compare(from[middle - 1], from[middle]) <= 0)) {
			for (int i = low; i < high; i++) {
				to[i] = from[i];
			}
			return;
		}

		// A normal merge.

		for (int i = low; i < high; i++) {
			if ((q >= high) || ((p < middle) && (this.compare(from[p], from[q]) <= 0))) {
				to[i] = from[p++];
			} else {
				to[i] = from[q++];
			}
		}
	}

	/**
	 * Compares two row
	 *
	 * @see #compareRowsByColumn(int, int, int)
	 * @param rowIndex1
	 * @param rowIndex2
	 * @return 0 if the rows are equal<br>
	 *         1 if the first row has a null or a bigger value than the second <br>
	 *         -1 if the first row has a null or a lower value than the second <br>
	 */

	public int compare(final int rowIndex1, final int rowIndex2) {
		final int result = this.compareRow(rowIndex1, rowIndex2);
		if (result != 0) {
			return this.getSortOrder() == SortOrder.ASCENDING ? result : -result;
		}
		return result;
	}

	public int compareRow(final int rowIndex1, final int rowIndex2) {
		Object row1 = this.model.getElementAt(rowIndex1);
		if (row1 instanceof NullValue) {
			row1 = null;
		}

		Object row2 = this.model.getElementAt(rowIndex2);
		if (row2 instanceof NullValue) {
			row2 = null;
		}

		// If both values are null, return 0.
		if ((row1 == null) && (row2 == null)) {
			return 0;
		} else if (row1 == null) { // Define null less than everything.
			return -1;
		} else if (row2 == null) {
			return 1;
		}

		if ((row1 instanceof Comparable) && (row2 instanceof Comparable)) {
			return ((Comparable) row1).compareTo(row2);
		}

		if ((row1 instanceof String) && (row2 instanceof String)) {
			return SortedListModel.comparator.compare(row1, row2);
		}

		if (row1.equals(row2)) {
			return 0;
		}

		final Object[] data = new Object[] { row1, row2 };
		Arrays.sort(data);

		if (data[0].equals(row1)) {
			return 1;
		} else {
			return -1;
		}
	}

}
