package com.ontimize.report.item;

import java.util.List;
import java.util.ResourceBundle;

public class SelectableMultipleItem {

	List itemList = null;

	public SelectableMultipleItem(final List list, final ResourceBundle res) {
		this.itemList = list;
	}

	public List getItemList() {
		return this.itemList;
	}

	@Override
	public String toString() {
		String sValue = "";
		for (int i = 0; i < this.itemList.size(); i++) {
			final Object ite = this.itemList.get(i);
			sValue += sValue.length() == 0 ? ite.toString() : "," + ite.toString();
		}
		return sValue;
	}

	public String getText() {
		String sValue = "";
		for (int i = 0; i < this.itemList.size(); i++) {
			final com.ontimize.report.item.SelectableItem ite = (com.ontimize.report.item.SelectableItem) this.itemList
					.get(i);
			sValue += sValue.length() == 0 ? ite.getText() : "," + ite.getText();
		}
		return sValue;

	}

}
