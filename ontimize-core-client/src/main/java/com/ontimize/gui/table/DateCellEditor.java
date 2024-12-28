package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import com.ontimize.gui.OpenDialog;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.DateDataField;
import com.ontimize.gui.field.TextDataField;
import com.ontimize.gui.field.document.HourDateDocument;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.util.ParseUtils;

/**
 * Class to manage edition of dates in table components. It wraps a <code>DateDataField</code> whose
 * parameters are passed in <code>Hashtable</code> parameter in constructor. It allows to manage
 * some date patterns: including date and hour.
 *
 * @author Imatia Innovation SL
 * @since 5.2000
 * @since 5.2059EN. It has been added parameters: <i>withhour, onlyhour, hourfirst</i>
 */
public class DateCellEditor extends CellEditor implements OpenDialog {

	/**
	 * Variable that indicates the order to show date and hour. By default hour is showed at first.
	 */
	public static boolean hourDefaultValueFirst = true;

	public static final String WITH_HOUR = "withhour";

	public static final String ONLY_HOUR = "onlyhour";

	public static final String HOUR_FIRST = "hourfirst";

	/**
	 * Static variable to show a calendar for selecting date on the right of the cell editor.
	 */
	public static boolean SHOW_CALENDAR = false;

	public static final String SHOWCALENDAR = "showcalendar";

	public boolean showCalendar = false;

	protected class EditorComp extends JPanel {

		private JComponent dataComponent = null;

		private DateDataField field = null;

		private JButton calendarButton = null;

		public EditorComp(final DateDataField dataField) {
			this.field = dataField;
			this.setLayout(new BorderLayout(0, 0));
			this.dataComponent = dataField.getDataField();
			this.setOpaque(false);

			this.calendarButton = new DataField.FieldButton(ImageManager.getIcon(ImageManager.CALENDAR));
			this.add(this.calendarButton, BorderLayout.EAST);
			this.calendarButton.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(final ActionEvent e) {
					EditorComp.this.field.showCalendar((Component) e.getSource());
				}
			});
			this.add(dataField.getDataField());

		}

	};

	protected EditorComp editorAux = null;

	/**
	 * Constructor of class.
	 * @param parameters the <code>Hashtable</code> with parameters. They are allowed parameters of
	 *        <code>DateDataField</code>: {@link DateDataField#init(Map)} and additionally:
	 *        <p>
	 *        <Table BORDER=1 CELLPADDING=3 CELLSPACING=1 RULES=ROWS * FRAME=BOX>
	 *        <tr>
	 *        <td><b>attribute</td>
	 *        <td><b>values</td>
	 *        <td><b>default</td>
	 *        <td><b>required</td>
	 *        <td><b>meaning</td>
	 *        </tr>
	 *        <tr>
	 *        <td>column</td>
	 *        <td><i></td>
	 *        <td></td>
	 *        <td>yes</td>
	 *        <td>The column to apply the editor</td>
	 *        </tr>
	 *        <tr>
	 *        <td>showcalendar</td>
	 *        <td><i></td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Shows a graphical calendar to choose the date from a calendar. It is also possible fix
	 *        this parameter for all DateCellEditor objects with static variable SHOW_CALENDAR.</td>
	 *        </tr>
	 *        <tr>
	 *        <td>withhour</td>
	 *        <td><i>yes/no</td>
	 *        <td>no</td>
	 *        <td>no</td>
	 *        <td>Indicates wheter cell editor will contain the hour. <b> Since 5.2059EN </b></td>
	 *        </tr>
	 *        <tr>
	 *        <td>hourfirst</td>
	 *        <td><i>yes/no</td>
	 *        <td>Value of variable hourDefaultValueFirst. By default, yes</td>
	 *        <td>no</td>
	 *        <td>Indicates whether hour must be showed at first. <b> Since 5.2059EN </b></td>
	 *        </tr>
	 *        </table>
	 */
	public DateCellEditor(final Map parameters) {
		super(parameters.get(CellEditor.COLUMN_PARAMETER), DateCellEditor.createDataField(parameters));
		this.showCalendar = ParseUtils.getBoolean((String) parameters.get(DateCellEditor.SHOWCALENDAR), false);
		if (this.showCalendar || DateCellEditor.SHOW_CALENDAR) {
			this.editorAux = new EditorComp((DateDataField) this.field);
		}
	}

	@Override
	public void setParentFrame(final Frame f) {
		if (this.field != null) {
			((OpenDialog) this.field).setParentFrame(f);
		}
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {

		if (this.editorAux == null) {
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		} else {
			// Configure the data field
			super.getTableCellEditorComponent(table, value, isSelected, row, column);
			// Return the auxiliary editor
			return this.editorAux;
		}
	}

	@Override
	public boolean stopCellEditing() {
		final boolean res = super.stopCellEditing();
		if (res) {
			((DateDataField) this.field).hiddenCalendar();
		}
		return res;
	}

	/**
	 * This method creates the data field for building the cell editor.
	 * @param parameters <code>Hashtable</code> with parameters
	 * @return the data field
	 */
	public static DateDataField createDataField(final Map parameters) {
		final DateDataField dataField = new DateDataField(parameters);
		if (ParseUtils.getBoolean((String) parameters.get(DateCellEditor.WITH_HOUR), false)) {
			dataField.setDocument(new HourDateDocument(
					ParseUtils.getBoolean((String) parameters.get(DateCellEditor.ONLY_HOUR), false),
					ParseUtils.getBoolean((String) parameters.get(DateCellEditor.HOUR_FIRST),
							DateCellEditor.hourDefaultValueFirst)));
		}

		if (dataField.getDataField() instanceof TextDataField.EJTextField) {
			((TextDataField.EJTextField) dataField.getDataField()).setCaretPositionOnFocusLost(false);
		}

		return dataField;
	}

}
