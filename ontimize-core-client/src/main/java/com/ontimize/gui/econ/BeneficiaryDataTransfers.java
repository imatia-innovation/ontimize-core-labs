package com.ontimize.gui.econ;

import java.util.List;
import java.util.Vector;

public class BeneficiaryDataTransfers {

	protected List data = new Vector();

	public BeneficiaryDataTransfers() {
	}

	public void add(final String code, final double amount, final String bankNumber, final String branchNumber, final String accountNumber, final char cost,
			final char concept, final String controlDigit, final String name,
			final String address, final String zipCode, final String province, final String transferConcep, final String dni) {

		this.data.add(new BeneficiaryDataTransfer(code, amount, bankNumber, branchNumber, accountNumber, cost, concept,
				controlDigit, name, address, zipCode, province,
				transferConcep, dni));
	}

	public int getSize() {
		return this.data.size();
	}

	public BeneficiaryDataTransfer getTransferData(final int index) {
		if ((index < 0) || (index >= this.getSize())) {
			return null;
		}
		return (BeneficiaryDataTransfer) this.data.get(index);
	}

}
