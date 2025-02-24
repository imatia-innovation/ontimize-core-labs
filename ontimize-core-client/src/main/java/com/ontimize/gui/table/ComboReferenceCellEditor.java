package com.ontimize.gui.table;

import java.awt.BorderLayout;
import java.awt.Component;
import java.sql.Types;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import javax.swing.ComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.cache.CacheManager;
import com.ontimize.cache.CachedComponent;
import com.ontimize.gui.ApplicationManager;
import com.ontimize.gui.Form;
import com.ontimize.gui.ReferenceComponent;
import com.ontimize.gui.field.AccessForm;
import com.ontimize.gui.field.ComboDataField;
import com.ontimize.gui.field.DataField;
import com.ontimize.gui.field.ReferenceComboDataField;
import com.ontimize.gui.images.ImageManager;
import com.ontimize.jee.common.db.NullValue;
import com.ontimize.jee.common.locator.EntityReferenceLocator;
import com.ontimize.util.ParseUtils;

public class ComboReferenceCellEditor extends CellEditor
implements ReferenceComponent, AccessForm, CachedComponent, ISetReferenceValues {

	private static final Logger logger = LoggerFactory.getLogger(ComboReferenceCellEditor.class);

	private EditorComp editorAux = null;

	protected Map colsSetTypes;

	public ComboReferenceCellEditor(final Map parameters) {
		super(parameters.get("column"), new ExtCampoComboRef(parameters));

		this.field.remove(this.field.getDataField());
		if (((ReferenceComboDataField) this.field).getDetailButtonListener() != null) {
			this.editorAux = new EditorComp((ReferenceComboDataField) this.field);
		}
		((ExtCampoComboRef) this.field).setComboReferenceCellEditor(this);

		final Object setTypes = parameters.get("onsetsqltypes");
		if (setTypes != null) {
			this.colsSetTypes = ApplicationManager.getTokensAt(setTypes.toString(), ";", ":");
		}
	}

	@Override
	public String getEntity() {
		return ((ExtCampoComboRef) this.field).getEntity();
	}

	@Override
	public List getAttributes() {
		return ((ExtCampoComboRef) this.field).getAttributes();
	}

	@Override
	public void setCacheManager(final CacheManager m) {
		((ExtCampoComboRef) this.field).setCacheManager(m);
	}

	protected Object getParentKeyValue(final String p) {
		return null;
	}

	@Override
	public Component getTableCellEditorComponent(final JTable table, final Object value, final boolean isSelected, final int row, final int column) {

		if (this.editorAux == null) {
			return super.getTableCellEditorComponent(table, value, isSelected, row, column);
		}

		if (value != null) {
			if (ApplicationManager.DEBUG) {
				System.out.println("getTableCellEditorComponent: " + value.toString());
			}
		} else {
			if (ApplicationManager.DEBUG) {
				System.out.println("getTableCellEditorComponent: NULL");
			}
		}
		if (table != null) {
			this.currentEditor = this.field;
			this.field.deleteData();
			this.field.setValue(value);
			this.editor = this.editorAux;
			this.editor.setBorder(this.getDefaultFocusBorder());
			this.editor.setFont(this.getEditorFont(table));
			this.editor.setForeground(CellEditor.fontColor);
			this.editor.setBackground(CellEditor.backgroundColor);
			return this.editor;
		} else {
			this.currentEditor = null;
			return null;
		}
	}

	@Override
	public void setReferenceLocator(final EntityReferenceLocator locator) {
		if (this.field != null) {
			((ReferenceComponent) this.field).setReferenceLocator(locator);
		}
	}

	@Override
	public void setParentForm(final Form f) {
		if (this.field != null) {
			this.field.setParentForm(f);
		}
	}

	@Override
	public List getSetColumns() {
		if ((this.field != null) && (this.field instanceof ReferenceComboDataField)) {
			return ((ReferenceComboDataField) this.field).getOnSetValueSetAttributes();
		}
		return null;
	}

	@Override
	public Map getSetData(final boolean useNullValues) {
		if ((this.field != null) && (this.field instanceof ReferenceComboDataField)) {
			final List columnsToSet = ((ReferenceComboDataField) this.field).getOnSetValueSetAttributes();
			if ((columnsToSet != null) && (columnsToSet.size() > 0)) {

				final Object currentCode = this.field.getValue();

				Map hCurrentComboCodeValues = new Hashtable();
				if (currentCode != null) {
					hCurrentComboCodeValues = ((ReferenceComboDataField) this.field).getValuesToCode(currentCode);
				}

				final Map onSetValueSetEquivalences = ((ReferenceComboDataField) this.field)
						.getOnSetValueSetEquivalences();

				final Map result = new Hashtable();
				for (int i = 0; i < columnsToSet.size(); i++) {
					final String colName = (String) columnsToSet.get(i);
					String originalName = colName;
					if ((onSetValueSetEquivalences != null) && onSetValueSetEquivalences.containsKey(colName)) {
						originalName = (String) onSetValueSetEquivalences.get(colName);
					}
					if (!hCurrentComboCodeValues.containsKey(originalName)) {
						if ((this.colsSetTypes != null) && this.colsSetTypes.containsKey(colName)) {
							final String colType = (String) this.colsSetTypes.get(colName);
							result.put(columnsToSet.get(i), new NullValue(ParseUtils.getSQLType(colType)));
						} else {
							result.put(columnsToSet.get(i), new NullValue(Types.VARCHAR));
						}
					} else {
						result.put(colName, hCurrentComboCodeValues.get(originalName));
					}
				}
				return result;
			}
		}
		return null;
	}

	private class EditorComp extends JPanel {

		private JComponent dataComponent = null;

		private JButton detailButton = null;

		public EditorComp(final ReferenceComboDataField dataField) {
			this.setLayout(new BorderLayout(0, 0));
			this.dataComponent = dataField.getDataField();
			this.setOpaque(false);

			if (dataField.getDetailButtonListener() != null) {
				this.detailButton = new DataField.FieldButton(ImageManager.getIcon(ImageManager.MAGNIFYING_GLASS)) {
					@Override
					public boolean isFocusTraversable() {
						return false;
					}
				};
				this.add(this.detailButton, BorderLayout.EAST);
				this.detailButton.setRequestFocusEnabled(false);
				this.detailButton.addActionListener(dataField.getDetailButtonListener());
			}

			this.add(dataField.getDataField());
			dataField.getDataField().setRequestFocusEnabled(false);
			dataField.getDataField().setBorder(null);
		}

	};

	protected static class ExtCampoComboRef extends ReferenceComboDataField {

		protected ComboReferenceCellEditor comboReferenceCellEditor = null;

		ExtCampoComboRef(final Map p) {
			super(p);

			((CustomComboBox) this.dataField).setKeySelectionManager(new ComboDataField.ExtKeySelectionManager() {

				@Override
				public int getComboIndex(final String str, final ComboBoxModel m) {
					final long t = System.currentTimeMillis();

					int selectedIndex = ((CustomComboBox) ExtCampoComboRef.this.dataField).getSelectedIndex();
					if ((selectedIndex < 0) || (str.length() == 1)) {
						selectedIndex = 0;
					}
					int nCoincidences = 0;
					int maxIndex = -1;
					for (int i = selectedIndex; i < m.getSize(); i++) {
						int nEastCoincidences = 0;
						String sText = ExtCampoComboRef.this.getCodeDescription(m.getElementAt(i));
						sText = sText.replace('á', 'a');
						sText = sText.replace('é', 'e');
						sText = sText.replace('í', 'i');
						sText = sText.replace('ó', 'o');
						sText = sText.replace('ú', 'u');
						sText = sText.replace('Á', 'a');
						sText = sText.replace('É', 'e');
						sText = sText.replace('Í', 'i');
						sText = sText.replace('Ó', 'o');
						sText = sText.replace('Ú', 'u');
						for (int j = 0; (j < sText.length()) && (j < str.length()); j++) {
							if (Character.toLowerCase(sText.charAt(j)) != Character.toLowerCase(str.charAt(j))) {
								break;
							} else {
								nEastCoincidences++;
							}
						}
						if (nEastCoincidences > nCoincidences) {
							nCoincidences = nEastCoincidences;
							maxIndex = i;
						}
					}

					if (ApplicationManager.DEBUG) {
						System.out.println("getIndexCombo() time: " + (System.currentTimeMillis() - t));
					}

					return maxIndex;
				}

				@Override
				public int selectionForKey(final char keyChar, final ComboBoxModel m) {
					return -1;
				}
			});
		}

		public void setComboReferenceCellEditor(final ComboReferenceCellEditor e) {
			this.comboReferenceCellEditor = e;
		}

		@Override
		protected Object getParentKeyValue(final String p) {
			Object parentkey = null;
			if (this.comboReferenceCellEditor != null) {
				parentkey = this.comboReferenceCellEditor.getParentKeyValue(p);
			}
			if (parentkey != null) {
				return parentkey;
			} else {
				return super.getParentKeyValue(p);
			}
		}

	}

}
