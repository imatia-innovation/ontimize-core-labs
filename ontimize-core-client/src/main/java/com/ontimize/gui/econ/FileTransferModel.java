package com.ontimize.gui.econ;

import java.io.UnsupportedEncodingException;
import java.text.DateFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTransferModel {

	private static final Logger logger = LoggerFactory.getLogger(FileTransferModel.class);

	private final boolean DEBUG = false;

	protected SimpleDateFormat df = (SimpleDateFormat) SimpleDateFormat.getDateInstance();

	protected NumberFormat numberFormat = NumberFormat.getInstance();

	protected abstract class FileTransferRegister {

		protected char[] zoneA = new char[2];

		protected char[] zoneB = new char[2];

		protected char[] zoneC = new char[10];

		protected char[] zoneD = new char[12];

		public FileTransferRegister(final String applicantCode) {
			if (applicantCode.length() > 10) {
				FileTransferModel.logger.debug("Applicant code is invalid (maximum 10 characters): " + applicantCode);
				FileTransferModel.logger.debug("Transferor code invalid (maximum 10 characters): " + applicantCode);
				throw new IllegalArgumentException(
						"Transferor code  invalid (maximum 10 characters): " + applicantCode);
			}

			for (int i = 0; i < this.zoneA.length; i++) {
				this.zoneA[i] = ' ';
			}
			for (int i = 0; i < this.zoneB.length; i++) {
				this.zoneB[i] = ' ';
			}
			for (int i = 0; i < this.zoneC.length; i++) {
				this.zoneC[i] = ' ';
			}
			for (int i = 0; i < this.zoneD.length; i++) {
				this.zoneD[i] = ' ';
			}
			// Justified to the right

			final String sApplicantCode = applicantCode.toUpperCase();
			for (int i = 0; (i < this.zoneC.length) && (i < sApplicantCode.length()); i++) {
				this.zoneC[i] = sApplicantCode.charAt(i);
			}
		}

		public abstract String getContents() throws UnsupportedEncodingException;

	};

	protected abstract class HeadRegister extends FileTransferRegister {

		protected final char[] zoneAValue = { '0', '3' };

		protected final char[] zoneBValue = { '5', '6' };

		protected final char[] zoneDValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		public HeadRegister(final String applicantCode) throws IllegalArgumentException {
			super(applicantCode);
			// Fills values
			for (int i = 0; i < this.zoneAValue.length; i++) {
				this.zoneA[i] = this.zoneAValue[i];
			}
			for (int i = 0; i < this.zoneBValue.length; i++) {
				this.zoneB[i] = this.zoneBValue[i];
			}

			for (int i = 0; i < this.zoneDValue.length; i++) {
				this.zoneD[i] = this.zoneDValue[i];
			}

		}

	};

	protected abstract class BeneficiaryRegister extends FileTransferRegister {

		protected final char[] zoneAValue = { '0', '6' };

		protected final char[] zoneBValue = { '5', '6' };

		public BeneficiaryRegister(final String applicantCode, final String beneficiaryCode) throws IllegalArgumentException {
			super(applicantCode);
			if (beneficiaryCode.length() > 12) {
				FileTransferModel.logger
				.debug("Beneficiary code is invalid (maximum 12 character): " + beneficiaryCode);
				throw new IllegalArgumentException(
						"Beneficiary code is invalid (maximum 12 character): " + beneficiaryCode);
			}
			// Fills values
			for (int i = 0; i < this.zoneAValue.length; i++) {
				this.zoneA[i] = this.zoneAValue[i];
			}
			for (int i = 0; i < this.zoneBValue.length; i++) {
				this.zoneB[i] = this.zoneBValue[i];
			}
			final String sBeneficiaryCodeUpper = beneficiaryCode.toUpperCase();
			// Justified to right
			for (int i = sBeneficiaryCodeUpper.length() - 1, k = this.zoneD.length - 1; i >= 0; i--, k--) {
				this.zoneD[k] = sBeneficiaryCodeUpper.charAt(i);
			}

		}

	};

	protected class TotalRegisters extends FileTransferRegister {

		protected final char[] zoneAValue = { '0', '8' };

		protected final char[] zoneBValue = { '5', '6' };

		protected final char[] zoneDValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneEValue = { ' ', ' ', ' ' };

		protected final char[] zoneF4Value = { ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneGValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected char[] zoneE = new char[3];

		protected char[] zoneF1 = new char[12];

		protected char[] zoneF2 = new char[8];

		protected char[] zoneF3 = new char[10];

		protected char[] zoneF4 = new char[6];

		protected char[] zoneG = new char[7];

		public TotalRegisters(final String applicantCode, final double amountSum, final int numberOfRegistry010,
				final int totalRegistryBenificiaryHeaderNumber) throws IllegalArgumentException {
			super(applicantCode);

			// Now fills values
			for (int i = 0; i < this.zoneAValue.length; i++) {
				this.zoneA[i] = this.zoneAValue[i];
			}
			for (int i = 0; i < this.zoneBValue.length; i++) {
				this.zoneB[i] = this.zoneBValue[i];
			}
			for (int i = 0; i < this.zoneDValue.length; i++) {
				this.zoneD[i] = this.zoneDValue[i];
			}
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
			for (int i = 0; i < this.zoneF4Value.length; i++) {
				this.zoneF4[i] = this.zoneF4Value[i];
			}
			for (int i = 0; i < this.zoneGValue.length; i++) {
				this.zoneG[i] = this.zoneGValue[i];
			}
			// Now variable fields

			String sSTRImport = FileTransferModel.this.numberFormat.format(amountSum);
			// Removes points and commas.
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < sSTRImport.length(); i++) {
				if ((sSTRImport.charAt(i) == ',') || (sSTRImport.charAt(i) == '.')) {
					continue;
				}
				sb.append(sSTRImport.charAt(i));
			}
			sSTRImport = sb.toString();
			if (sSTRImport.length() != 12) {
				FileTransferModel.logger.debug("Wrong amount format: " + amountSum + " ---->  " + sSTRImport);
				throw new IllegalArgumentException("Wrong amount format: " + amountSum + " ---->  " + sSTRImport);
			}
			for (int i = 0; i < this.zoneF1.length; i++) {
				this.zoneF1[i] = sSTRImport.charAt(i);
			} //

			final String sNumRegistryData010 = Integer.toString(numberOfRegistry010);
			// Justified to right with '0' in left
			for (int i = 0; i < this.zoneF2.length; i++) {
				this.zoneF2[i] = '0';
			}
			// Inserts the number
			// Justified to the right
			for (int i = sNumRegistryData010.length() - 1, k = this.zoneF2.length - 1; i >= 0; i--, k--) {
				this.zoneF2[k] = sNumRegistryData010.charAt(i);
			}

			// +1 is used because total register is included
			final String sTotalRegistry = Integer.toString(totalRegistryBenificiaryHeaderNumber + 1);
			// Right align with 0's in the left
			for (int i = 0; i < this.zoneF3.length; i++) {
				this.zoneF3[i] = '0';
			}
			// Inserts number
			// Justified to the right
			for (int i = sTotalRegistry.length() - 1, k = this.zoneF3.length - 1; i >= 0; i--, k--) {
				this.zoneF3[k] = sTotalRegistry.charAt(i);
			}
		}

		@Override
		public String getContents() throws UnsupportedEncodingException {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.zoneA);
			sb.append(this.zoneB);
			sb.append(this.zoneC);
			sb.append(this.zoneD);
			sb.append(this.zoneE);
			sb.append(this.zoneF1);
			sb.append(this.zoneF2);
			sb.append(this.zoneF3);
			sb.append(this.zoneF4);
			sb.append(this.zoneG);

			// Returns in default codification of system
			return sb.toString();
		}

	};

	protected class HeadRegister1 extends HeadRegister {

		public static final char F6_WITHOUT_RELATION = '0';

		public static final char F6_WITH_RELATION = '1';

		protected final char[] zoneEValue = { '0', '0', '1' };

		protected final char[] zoneF7Value = { ' ', ' ', ' ' };

		protected final char[] zoneGValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneE = new char[3];

		protected final char[] zoneF1 = new char[6];

		protected final char[] zoneF2 = new char[6];

		protected final char[] zoneF3 = new char[4];

		protected final char[] zoneF4 = new char[4];

		protected final char[] zoneF5 = new char[10];

		protected final char[] zoneF6 = new char[1];

		protected final char[] zoneF7 = new char[3];

		protected final char[] zoneF8 = new char[2];

		protected final char[] zoneG = new char[7];

		public HeadRegister1(final String applicantCode, final Date sendDate, final Date creationDate, final String entityNumber,
				final String officeNumber, final String accountNumber, final char chargeDetail,
				final String controlDigit) throws IllegalArgumentException {

			super(applicantCode);
			if (entityNumber.length() != 4) {
				FileTransferModel.logger.debug("Invalid entity number: " + entityNumber);
				throw new IllegalArgumentException("Invalid entity number: " + entityNumber);
			}
			if (officeNumber.length() != 4) {
				FileTransferModel.logger.debug("Invalid branch number: " + entityNumber);
				throw new IllegalArgumentException("Invalid branch number: " + entityNumber);
			}
			if (accountNumber.length() != 10) {
				FileTransferModel.logger.debug("Invalid account number: " + entityNumber);
				throw new IllegalArgumentException("Invalid account number: " + entityNumber);
			}
			if (controlDigit.length() != 2) {
				FileTransferModel.logger.debug("Invalid Control Digit: " + controlDigit);
				throw new IllegalArgumentException("Invalid Control Digit: " + controlDigit);
			}
			if ((chargeDetail != HeadRegister1.F6_WITH_RELATION)
					&& (chargeDetail != HeadRegister1.F6_WITHOUT_RELATION)) {
				FileTransferModel.logger.debug("Invoice detail character must be " + HeadRegister1.F6_WITH_RELATION
						+ " ó " + HeadRegister1.F6_WITHOUT_RELATION);
				throw new IllegalArgumentException("Invoice detail character must be " + HeadRegister1.F6_WITH_RELATION
						+ " ó " + HeadRegister1.F6_WITHOUT_RELATION);
			}
			// Now formats
			final String fEnvio = FileTransferModel.this.df.format(sendDate);
			if (fEnvio.length() != 6) {
				FileTransferModel.logger.debug("Expedition date cannot be formatted correctly: '" + fEnvio + "'");
				throw new IllegalArgumentException("Expedition date cannot be formatted correctly: '" + fEnvio + "'");
			}
			final String fEmision = FileTransferModel.this.df.format(creationDate);
			if (fEmision.length() != 6) {
				FileTransferModel.logger.debug("Date of issue cannot be formatted correctly: '" + fEmision + "'");
				throw new IllegalArgumentException("Date of issue cannot be formatted correctly: '" + fEmision + "'");
			}

			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
			for (int i = 0; i < this.zoneF7Value.length; i++) {
				this.zoneF7[i] = this.zoneF7Value[i];
			}
			for (int i = 0; i < this.zoneGValue.length; i++) {
				this.zoneG[i] = this.zoneGValue[i];
			}
			// Not fixed values

			for (int i = 0; i < this.zoneF1.length; i++) {
				this.zoneF1[i] = fEnvio.charAt(i);
			}

			for (int i = 0; i < this.zoneF2.length; i++) {
				this.zoneF2[i] = fEmision.charAt(i);
			}

			for (int i = 0; i < this.zoneF3.length; i++) {
				this.zoneF3[i] = entityNumber.charAt(i);
			}

			for (int i = 0; i < this.zoneF4.length; i++) {
				this.zoneF4[i] = officeNumber.charAt(i);
			}

			for (int i = 0; i < this.zoneF5.length; i++) {
				this.zoneF5[i] = accountNumber.charAt(i);
			}

			this.zoneF6[0] = chargeDetail;

			for (int i = 0; i < this.zoneF8.length; i++) {
				this.zoneF8[i] = controlDigit.charAt(i);
			}

			// Now we have the registry values.
		}

		@Override
		public String getContents() throws UnsupportedEncodingException {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.zoneA);
			sb.append(this.zoneB);
			sb.append(this.zoneC);
			sb.append(this.zoneD);
			sb.append(this.zoneE);
			sb.append(this.zoneF1);
			sb.append(this.zoneF2);
			sb.append(this.zoneF3);
			sb.append(this.zoneF4);
			sb.append(this.zoneF5);
			sb.append(this.zoneF6);
			sb.append(this.zoneF7);
			sb.append(this.zoneF8);
			sb.append(this.zoneG);

			// Returns default codification of system
			return sb.toString();
		}

	};

	protected abstract class HeadRegister234 extends HeadRegister {

		protected final char[] zoneGValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneE = new char[3];

		protected final char[] zoneF = new char[36];

		protected final char[] zoneG = new char[7];

		public HeadRegister234(final String applicantCode, final String content) throws IllegalArgumentException {
			super(applicantCode);
			if (content.length() == 0) {
				FileTransferModel.logger.debug("Invalid Content: " + content);
				throw new IllegalArgumentException("Invalid Content: " + content);
			} else if (content.length() > this.zoneF.length) {
				FileTransferModel.logger.debug("INFORMATION: Content too large. It has been truncated" + content);
			}
			// Initializes with with spaces
			for (int i = 0; i < this.zoneF.length; i++) {
				this.zoneF[i] = ' ';
			}

			for (int i = 0; i < this.zoneGValue.length; i++) {
				this.zoneG[i] = this.zoneGValue[i];
			}
			// Not fixed values: applicant number
			final String conten = content.toUpperCase();
			for (int i = 0; i < Math.min(conten.length(), this.zoneF.length); i++) {
				this.zoneF[i] = conten.charAt(i);
			}
			// Now we have registry values
		}

		@Override
		public String getContents() throws UnsupportedEncodingException {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.zoneA);
			sb.append(this.zoneB);
			sb.append(this.zoneC);
			sb.append(this.zoneD);
			sb.append(this.zoneE);
			sb.append(this.zoneF);
			sb.append(this.zoneG);

			// Returns the default system codification
			return sb.toString();
		}

	};

	protected class HeadRegister2 extends HeadRegister234 {

		protected final char[] zoneEValue = { '0', '0', '2' };

		public HeadRegister2(final String applicantCode, final String applicantName) throws IllegalArgumentException {
			super(applicantCode, applicantName);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class HeadRegister3 extends HeadRegister234 {

		protected final char[] zoneEValue = { '0', '0', '3' };

		public HeadRegister3(final String applicantCode, final String applicantAddress) throws IllegalArgumentException {
			super(applicantCode, applicantAddress);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class HeadRegister4 extends HeadRegister234 {

		protected final char[] zoneEValue = { '0', '0', '4' };

		public HeadRegister4(final String applicantCode, final String applicantPosition) throws IllegalArgumentException {
			super(applicantCode, applicantPosition);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class HeadRegister5 extends HeadRegister234 {

		protected final char[] zoneEValue = { '0', '0', '7' };

		public HeadRegister5(final String applicantCode, final String accountName) throws IllegalArgumentException {
			super(applicantCode, accountName);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class HeadRegister6 extends HeadRegister234 {

		protected final char[] zoneEValue = { '0', '0', '8' };

		public HeadRegister6(final String applicantCode, final String accountByAddress) throws IllegalArgumentException {
			super(applicantCode, accountByAddress);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class BeneficiaryRegister1 extends BeneficiaryRegister {

		public static final char F5_BY_ORDERER_ACCOUNT = '1';

		public static final char F5_BY_BENEFICIARY_ACCOUNT = '2';

		public static final char F6_SALARY_CONCEPT = '1';

		public static final char F6_PENSION_CONCEPT = '8';

		public static final char F6_OTHER_CONCEPTS = '9';

		protected final char[] zoneEValue = { '0', '1', '0' };

		protected final char[] zoneF7Value = { ' ', ' ' };

		protected final char[] zoneGValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneE = new char[3];

		protected final char[] zoneF1 = new char[12];

		protected final char[] zoneF2 = new char[4];

		protected final char[] zoneF3 = new char[4];

		protected final char[] zoneF4 = new char[10];

		protected final char[] zoneF5 = new char[1];

		protected final char[] zoneF6 = new char[1];

		protected final char[] zoneF7 = new char[2];

		protected final char[] zoneF8 = new char[2];

		protected final char[] zoneG = new char[7];

		public BeneficiaryRegister1(final String applicantCode, final String beneficiaryCode, final double amount, final String entityNumber,
				final String branchNumber, final String accountNumber, final char expenditure,
				final char concept, final String controlDigit) throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode);
			if (entityNumber.length() != 4) {
				FileTransferModel.logger.debug("Invalid entity number: " + entityNumber);
				throw new IllegalArgumentException("Invalid entity number: " + entityNumber);
			}
			if (branchNumber.length() != 4) {
				FileTransferModel.logger.debug("Invalid branch number: " + branchNumber);
				throw new IllegalArgumentException("Invalid branch number: " + branchNumber);
			}
			if (accountNumber.length() != 10) {
				FileTransferModel.logger.debug("Invalid account number: " + accountNumber);
				throw new IllegalArgumentException("Invalid account number: " + accountNumber);
			}
			if (controlDigit.length() != 2) {
				FileTransferModel.logger.debug("Invalid control digit: " + controlDigit);
				throw new IllegalArgumentException("Invalid control digit: " + controlDigit);
			}
			if ((expenditure != BeneficiaryRegister1.F5_BY_ORDERER_ACCOUNT)
					&& (expenditure != BeneficiaryRegister1.F5_BY_BENEFICIARY_ACCOUNT)) {
				FileTransferModel.logger.debug("Expenditure character is invalid");
				throw new IllegalArgumentException("Expenditure character is invalid");
			}
			if ((concept != BeneficiaryRegister1.F6_SALARY_CONCEPT)
					&& (concept != BeneficiaryRegister1.F6_PENSION_CONCEPT)
					&& (concept != BeneficiaryRegister1.F6_OTHER_CONCEPTS)) {
				FileTransferModel.logger.debug("Concept character is invalid");
				throw new IllegalArgumentException("Concept character is invalid");
			}

			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
			for (int i = 0; i < this.zoneF7Value.length; i++) {
				this.zoneF7[i] = this.zoneF7Value[i];
			}
			for (int i = 0; i < this.zoneGValue.length; i++) {
				this.zoneG[i] = this.zoneGValue[i];
			}

			// Set values
			// Not fixed values
			String sImport = FileTransferModel.this.numberFormat.format(amount);
			// Removes point and comma
			final StringBuilder sb = new StringBuilder();
			for (int i = 0; i < sImport.length(); i++) {
				if ((sImport.charAt(i) == ',') || (sImport.charAt(i) == '.')) {
					continue;
				}
				sb.append(sImport.charAt(i));
			}
			sImport = sb.toString();
			if (sImport.length() != 12) {
				FileTransferModel.logger.debug("Wrong amount format: " + amount + " ---->  " + sImport);
				throw new IllegalArgumentException("Wrong amount format: " + amount + " ---->  " + sImport);
			}
			for (int i = 0; i < this.zoneF1.length; i++) {
				this.zoneF1[i] = sImport.charAt(i);
			}

			for (int i = 0; i < this.zoneF2.length; i++) {
				this.zoneF2[i] = entityNumber.charAt(i);
			}

			for (int i = 0; i < this.zoneF3.length; i++) {
				this.zoneF3[i] = branchNumber.charAt(i);
			}

			for (int i = 0; i < this.zoneF4.length; i++) {
				this.zoneF4[i] = accountNumber.charAt(i);
			}

			this.zoneF5[0] = expenditure;

			this.zoneF6[0] = concept;

			for (int i = 0; i < this.zoneF8.length; i++) {
				this.zoneF8[i] = controlDigit.charAt(i);
			}
		}

		@Override
		public String getContents() throws UnsupportedEncodingException {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.zoneA);
			sb.append(this.zoneB);
			sb.append(this.zoneC);
			sb.append(this.zoneD);
			sb.append(this.zoneE);
			sb.append(this.zoneF1);
			sb.append(this.zoneF2);
			sb.append(this.zoneF3);
			sb.append(this.zoneF4);
			sb.append(this.zoneF5);
			sb.append(this.zoneF6);
			sb.append(this.zoneF7);
			sb.append(this.zoneF8);
			sb.append(this.zoneG);

			// Returns default codification
			return sb.toString();
		}

	};

	protected abstract class BeneficiaryRegister2to8 extends BeneficiaryRegister {

		protected final char[] zoneGValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneE = new char[3];

		protected final char[] zoneF = new char[36];

		protected final char[] zoneG = new char[7];

		public BeneficiaryRegister2to8(final String applicantCode, final String beneficiaryCode, final String content)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode);
			if ((content == null) || (content.length() == 0)) {
				FileTransferModel.logger.debug("Content is invalid: " + content);
				throw new IllegalArgumentException(this.getClass().toString() + "Content is invalid: " + content);
			} else if (content.length() > this.zoneF.length) {
				FileTransferModel.logger.debug("WARNING. Content too large. It has been truncated" + content);
			}

			for (int i = 0; i < this.zoneGValue.length; i++) {
				this.zoneG[i] = this.zoneGValue[i];
			}

			for (int i = 0; i < this.zoneF.length; i++) {
				this.zoneF[i] = ' ';
			}
			// Not fixed values: beneficiary name
			final String conten = content.toUpperCase();
			for (int i = 0; i < Math.min(conten.length(), this.zoneF.length); i++) {
				this.zoneF[i] = conten.charAt(i);
			}
		}

		@Override
		public String getContents() throws UnsupportedEncodingException {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.zoneA);
			sb.append(this.zoneB);
			sb.append(this.zoneC);
			sb.append(this.zoneD);
			sb.append(this.zoneE);
			sb.append(this.zoneF);
			sb.append(this.zoneG);

			// Default system codification
			return sb.toString();
		}

	};

	protected class BeneficiaryRegister2 extends BeneficiaryRegister2to8 {

		protected final char[] zoneEValue = { '0', '1', '1' };

		public BeneficiaryRegister2(final String applicantCode, final String beneficiaryCode, final String beneficiaryName)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode, beneficiaryName);

			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}

		}

	};

	protected class BeneficiaryRegister3 extends BeneficiaryRegister2to8 {

		protected final char[] zoneEValue = { '0', '1', '2' };

		public BeneficiaryRegister3(final String applicantCode, final String beneficiaryCode, final String beneficiaryAddress)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode, beneficiaryAddress);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class BeneficiaryRegister4 extends BeneficiaryRegister2to8 {

		protected final char[] zoneEValue = { '0', '1', '3' };

		public BeneficiaryRegister4(final String applicantCode, final String beneficiaryCode, final String beneficiaryAddress)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode, beneficiaryAddress);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class BeneficiaryRegister5 extends BeneficiaryRegister2to8 {

		protected final char[] zoneEValue = { '0', '1', '4' };

		public BeneficiaryRegister5(final String applicantCode, final String beneficiaryCode, final String postalCode)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode, postalCode);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class BeneficiaryRegister6 extends BeneficiaryRegister2to8 {

		protected final char[] zoneEValue = { '0', '1', '5' };

		public BeneficiaryRegister6(final String applicantCode, final String beneficiaryCode, final String beneficiaryProvince)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode, beneficiaryProvince);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class BeneficiaryRegister7 extends BeneficiaryRegister2to8 {

		protected final char[] zoneEValue = { '0', '1', '6' };

		public BeneficiaryRegister7(final String applicantCode, final String beneficiaryCode, final String concept)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode, concept);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}
		}

	};

	protected class BeneficiaryRegister9 extends BeneficiaryRegister {

		protected final char[] zoneEValue = { '0', '1', '8' };

		protected final char[] zoneGValue = { ' ', ' ', ' ', ' ', ' ', ' ', ' ' };

		protected final char[] zoneE = new char[3];

		protected final char[] zoneF1 = new char[18];

		protected final char[] zoneF2 = new char[18];

		protected final char[] zoneG = new char[7];

		public BeneficiaryRegister9(final String applicantCode, final String beneficiaryCode, final String dni)
				throws IllegalArgumentException {
			super(applicantCode, beneficiaryCode);
			for (int i = 0; i < this.zoneEValue.length; i++) {
				this.zoneE[i] = this.zoneEValue[i];
			}

			for (int i = 0; i < this.zoneF2.length; i++) {
				this.zoneF2[i] = ' ';
			}
			// Number 1 filled with '0'
			for (int i = 0; i < this.zoneF1.length; i++) {
				this.zoneF1[i] = '0';
			}
			// Justified to right
			for (int i = dni.length() - 1, k = this.zoneF1.length - 1; i >= 0; i--, k--) {
				this.zoneF1[k] = dni.charAt(i);
			}

			// Zone g with blank spaces
			// Number 1 filled with '0'
			for (int i = 0; i < this.zoneG.length; i++) {
				this.zoneG[i] = ' ';
			}

		}

		@Override
		public String getContents() throws UnsupportedEncodingException {
			final StringBuilder sb = new StringBuilder();
			sb.append(this.zoneA);
			sb.append(this.zoneB);
			sb.append(this.zoneC);
			sb.append(this.zoneD);
			sb.append(this.zoneE);
			sb.append(this.zoneF1);
			sb.append(this.zoneF2);
			sb.append(this.zoneG);

			// Returns default codification of system
			return sb.toString();
		}

	};

	protected String ordererCode = null;

	protected Date sendDate = null;

	protected Date emissionDate = null;

	protected String ordererBank = null;

	protected String ordererBranch = null;

	protected String ordererAccount = null;

	protected char debitDetail = ' ';

	protected String ordererName = null;

	protected String ordererAddress = null;

	protected String applicantPost = null;

	protected String controlDigit = null;

	protected String accountOfName = null;

	protected String accountAddress = null;

	protected BeneficiaryDataTransfers beneficiaryData = null;

	public FileTransferModel(final String applicantCode, final Date sendDate, final Date emissionDate, final String applicantEntity,
			final String applicantOffice, final String applicantAccount, final char debitDetail,
			final String applicantName, final String applicantAddress, final String applicantPost, final String controlDigit,
			final String accountOfName, final String accountAddress,
			final BeneficiaryDataTransfers beneficiaryData) {

		this.ordererCode = applicantCode;
		this.sendDate = sendDate;
		this.emissionDate = emissionDate;
		this.ordererBank = applicantEntity;
		this.ordererBranch = applicantOffice;
		this.ordererAccount = applicantAccount;
		this.debitDetail = debitDetail;

		this.ordererName = applicantName;
		this.ordererAddress = applicantAddress;
		this.applicantPost = applicantPost;
		this.controlDigit = controlDigit;

		this.accountOfName = accountOfName;
		this.accountAddress = accountAddress;

		this.beneficiaryData = beneficiaryData;

		final DateFormatSymbols simbolos = new DateFormatSymbols();
		simbolos.setLocalPatternChars("GyMdkHmsSEDFwWahKz");
		this.df.setDateFormatSymbols(simbolos);
		this.df.applyPattern("ddMMyy");

		this.numberFormat.setMaximumFractionDigits(2);
		this.numberFormat.setMinimumFractionDigits(2);
		this.numberFormat.setMaximumIntegerDigits(10);
		this.numberFormat.setMinimumIntegerDigits(10);
		this.numberFormat.setGroupingUsed(false);
	}

	public List generate() {
		// Generating ordered registers
		final List v = new Vector();
		int iTotalRegistry = 0;
		final HeadRegister1 headerRegistry1 = new HeadRegister1(this.ordererCode, this.sendDate, this.emissionDate,
				this.ordererBank, this.ordererBranch, this.ordererAccount,
				this.debitDetail, this.controlDigit);
		final HeadRegister2 headerRegistry2 = new HeadRegister2(this.ordererCode, this.ordererName);
		final HeadRegister3 headerRegistry3 = new HeadRegister3(this.ordererCode, this.ordererAddress);
		final HeadRegister4 headerRegistry4 = new HeadRegister4(this.ordererCode, this.applicantPost);

		v.add(v.size(), headerRegistry1);
		v.add(v.size(), headerRegistry2);
		v.add(v.size(), headerRegistry3);
		v.add(v.size(), headerRegistry4);

		if ((this.accountOfName != null) && !this.accountOfName.equals("")) {
			final HeadRegister5 headerRegistry5 = new HeadRegister5(this.ordererCode, this.accountOfName);
			final HeadRegister6 headerRegistry6 = new HeadRegister6(this.ordererCode, this.accountAddress);
			v.add(v.size(), headerRegistry5);
			v.add(v.size(), headerRegistry6);
		}

		iTotalRegistry = v.size();

		// Now beneficiary registers
		double dImportSum = 0.0;
		int i010Registers = 0;
		for (int i = 0; i < this.beneficiaryData.getSize(); i++) {
			int iBeneficiaryRegisters = 0;
			final BeneficiaryDataTransfer bdtDataB = this.beneficiaryData.getTransferData(i);
			if (this.DEBUG) {
				FileTransferModel.logger.debug(this.getClass().toString() + ": Records data: " + bdtDataB.toString());
			}
			final BeneficiaryRegister1 registroB1 = new BeneficiaryRegister1(this.ordererCode, bdtDataB.getBeneficiaryCode(),
					bdtDataB.getAmount(), bdtDataB.getBankNumber(),
					bdtDataB.getBranch(), bdtDataB.getCurrentAccount(), bdtDataB.getCosts(), bdtDataB.getConcept(),
					bdtDataB.getControlDigit());
			final BeneficiaryRegister2 registroB2 = new BeneficiaryRegister2(this.ordererCode, bdtDataB.getBeneficiaryCode(),
					bdtDataB.getBeneficiaryName());

			final BeneficiaryRegister3 registroB3 = new BeneficiaryRegister3(this.ordererCode, bdtDataB.getBeneficiaryCode(),
					bdtDataB.getBeneficiaryAddress());

			final BeneficiaryRegister5 registroB5 = new BeneficiaryRegister5(this.ordererCode, bdtDataB.getBeneficiaryCode(),
					bdtDataB.getBeneficiaryZipCode());

			final BeneficiaryRegister6 registroB6 = new BeneficiaryRegister6(this.ordererCode, bdtDataB.getBeneficiaryCode(),
					bdtDataB.getBeneficiaryProvince());

			final BeneficiaryRegister7 registroB7 = new BeneficiaryRegister7(this.ordererCode, bdtDataB.getBeneficiaryCode(),
					bdtDataB.getTransferConcept());

			v.add(v.size(), registroB1);
			iBeneficiaryRegisters++;
			v.add(v.size(), registroB2);
			iBeneficiaryRegisters++;
			v.add(v.size(), registroB3);
			iBeneficiaryRegisters++;
			v.add(v.size(), registroB5);
			iBeneficiaryRegisters++;
			v.add(v.size(), registroB6);
			iBeneficiaryRegisters++;
			v.add(v.size(), registroB7);
			iBeneficiaryRegisters++;

			final String dni = bdtDataB.getDNI();
			if ((dni != null) && (!dni.equals(""))) {
				final BeneficiaryRegister9 registroB9 = new BeneficiaryRegister9(this.ordererCode,
						bdtDataB.getBeneficiaryCode(), bdtDataB.getDNI());
				v.add(v.size(), registroB9);
				iBeneficiaryRegisters++;
			}

			dImportSum += bdtDataB.getAmount();
			i010Registers++;
			iTotalRegistry = iTotalRegistry + iBeneficiaryRegisters;
		}

		// Now totals
		final TotalRegisters totalRegisters = new TotalRegisters(this.ordererCode, dImportSum, i010Registers, iTotalRegistry);
		v.add(totalRegisters);

		return v;
	}

}
