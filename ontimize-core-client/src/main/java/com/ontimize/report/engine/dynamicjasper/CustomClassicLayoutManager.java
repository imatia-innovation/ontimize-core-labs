package com.ontimize.report.engine.dynamicjasper;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ontimize.report.ReportEngine;

import ar.com.fdvs.dj.core.DJConstants;
import ar.com.fdvs.dj.core.FontHelper;
import ar.com.fdvs.dj.core.layout.AbstractLayoutManager;
import ar.com.fdvs.dj.core.layout.ClassicLayoutManager;
import ar.com.fdvs.dj.core.layout.LayoutException;
import ar.com.fdvs.dj.domain.DJCalculation;
import ar.com.fdvs.dj.domain.DJGroupLabel;
import ar.com.fdvs.dj.domain.DynamicJasperDesign;
import ar.com.fdvs.dj.domain.Style;
import ar.com.fdvs.dj.domain.constants.Font;
import ar.com.fdvs.dj.domain.constants.GroupLayout;
import ar.com.fdvs.dj.domain.constants.LabelPosition;
import ar.com.fdvs.dj.domain.constants.Transparency;
import ar.com.fdvs.dj.domain.entities.DJGroup;
import ar.com.fdvs.dj.domain.entities.DJGroupVariable;
import ar.com.fdvs.dj.domain.entities.columns.AbstractColumn;
import ar.com.fdvs.dj.domain.entities.columns.BarCodeColumn;
import ar.com.fdvs.dj.domain.entities.columns.GlobalGroupColumn;
import ar.com.fdvs.dj.domain.entities.columns.ImageColumn;
import ar.com.fdvs.dj.domain.entities.columns.PercentageColumn;
import ar.com.fdvs.dj.domain.entities.columns.PropertyColumn;
import ar.com.fdvs.dj.domain.entities.conditionalStyle.ConditionalStyle;
import ar.com.fdvs.dj.util.ExpressionUtils;
import ar.com.fdvs.dj.util.HyperLinkUtil;
import ar.com.fdvs.dj.util.LayoutUtils;
import ar.com.fdvs.dj.util.Utils;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRTextElement;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignConditionalStyle;
import net.sf.jasperreports.engine.design.JRDesignElement;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignGroup;
import net.sf.jasperreports.engine.design.JRDesignImage;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.type.EvaluationTimeEnum;
import net.sf.jasperreports.engine.type.ModeEnum;
import net.sf.jasperreports.engine.type.OnErrorTypeEnum;
import net.sf.jasperreports.engine.type.PositionTypeEnum;
import net.sf.jasperreports.engine.type.ScaleImageEnum;
import net.sf.jasperreports.engine.type.StretchTypeEnum;
import net.sf.jasperreports.engine.util.JRExpressionUtil;

/**
 * @author Imatia Innovation SL
 *
 */
public class CustomClassicLayoutManager extends ClassicLayoutManager {

	private static final Logger logger = LoggerFactory.getLogger(CustomClassicLayoutManager.class);

	protected Map hColsPositions = new Hashtable();

	protected ReportEngine reportEngine;

	protected boolean isShowedRowNumber;

	public CustomClassicLayoutManager(final Map hColsPositions, final ReportEngine reportEngine, final boolean isShowedRowNumber) {
		super();
		this.hColsPositions = hColsPositions;
		this.reportEngine = reportEngine;
		this.isShowedRowNumber = isShowedRowNumber;
	}

	protected void layoutGroupFooterLabels(final DJGroup djgroup, final JRDesignGroup jgroup) {
		final List footerVariables = djgroup.getFooterVariables();
		final DJGroupLabel label = djgroup.getFooterLabel();

		if ((label == null) || footerVariables.isEmpty()) {
			return;
		}

		final PropertyColumn col = djgroup.getColumnToGroupBy();
		JRDesignBand band = (JRDesignBand) jgroup.getGroupFooterSection();

		// log.debug("Adding footer group label for group " + djgroup);

		final DJGroupVariable lmvar = this.findLeftMostColumn(footerVariables);
		final AbstractColumn lmColumn = lmvar.getColumnToApplyOperation();
		// int width = lmColumn.getPosX().intValue() - col.getPosX().intValue();
		int width = this.getReport().getOptions().getPrintableWidth() - 35 - col.getPosX().intValue();
		if (this.isShowedRowNumber) {
			width = width - DynamicJasperEngine.widthRowNumbers;
		}
		int yOffset = 0;
		if (band != null) {
			yOffset = this.findYOffsetForGroupLabel(band);
		}

		final JRDesignExpression labelExp = ExpressionUtils.createStringExpression("\"" + label.getText() + "\"");
		final JRDesignTextField labelTf = new JRDesignTextField();
		labelTf.setExpression(labelExp);
		labelTf.setWidth(width);
		labelTf.setHeight(this.getFooterVariableHeight(djgroup));
		// labelTf.setX(col.getPosX().intValue()); //label starts in the
		// column-to-group-by x position
		if (this.isShowedRowNumber) {
			labelTf.setX((width / Integer.parseInt(this.hColsPositions.get(col.getName()).toString()))
					+ DynamicJasperEngine.widthRowNumbers);
		} else {
			labelTf.setX(width / Integer.parseInt(this.hColsPositions.get(col.getName()).toString()));
		}

		labelTf.setY(20);
		final int yOffsetGlabel = labelTf.getHeight();
		labelTf.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
		this.applyStyleToElement(label.getStyle(), labelTf);
		if (band == null) {
			band = new JRDesignBand();
		}
		band.addElement(labelTf);

	}

	@Override
	public JRDesignGroup getJRGroupFromDJGroup(final DJGroup group) {
		final int index = this.getReport().getColumnsGroups().indexOf(group);
		return (JRDesignGroup) this.realGroups.get(index);
	}

	protected int findYOffsetForGroupLabel(final JRDesignBand band) {
		int offset = 0;
		for (final Iterator iterator = band.getChildren().iterator(); iterator.hasNext();) {
			final JRDesignElement elem = (JRDesignElement) iterator.next();
			if ((elem.getKey() != null) && elem.getKey().startsWith("variable_for_column_")) {
				offset = elem.getY();
				break;
			}
		}
		return offset;
	}

	@Override
	protected void transformDetailBand() {

		final JRDesignSection detailSection = (JRDesignSection) this.getDesign().getDetailSection();

		// log.debug("transforming Detail Band...");
		JRDesignBand detail = null;
		if (detailSection.getBandsList().isEmpty()) {
			detail = new JRDesignBand();
			detailSection.getBandsList().add(detail);
		} else {
			detail = (JRDesignBand) detailSection.getBandsList().iterator().next();
		}

		detail.setHeight(this.getReport().getOptions().getDetailHeight().intValue());

		for (final Iterator iter = this.getVisibleColumns().iterator(); iter.hasNext();) {

			// if
			// (((DynamicJasperEngine)reportEngine).reportDialog.getSelectedPrintingColumns().size()
			// == 0) {
			// break;
			// }

			final AbstractColumn column = (AbstractColumn) iter.next();

			/**
			 * Barcode column
			 */
			if (column instanceof BarCodeColumn) {
				final BarCodeColumn barcodeColumn = (BarCodeColumn) column;
				final JRDesignImage image = new JRDesignImage(new JRDesignStyle().getDefaultStyleProvider());
				final JRDesignExpression imageExp = new JRDesignExpression();
				// imageExp.setText("ar.com.fdvs.dj.core.BarcodeHelper.getBarcodeImage("+barcodeColumn.getBarcodeType()
				// + ", "+ column.getTextForExpression()+ ", "+
				// barcodeColumn.isShowText() + ", " +
				// barcodeColumn.isCheckSum() + ", " +
				// barcodeColumn.getApplicationIdentifier() + ","+
				// column.getWidth() +", "+
				// report.getOptions().getDetailHeight().intValue() + " )" );

				// Do not pass column height and width mecause barbecue
				// generates the image with wierd dimensions. Pass 0 in both
				// cases
				String applicationIdentifier = barcodeColumn.getApplicationIdentifier();
				if ((applicationIdentifier != null) && !"".equals(applicationIdentifier.trim())) {
					applicationIdentifier = "$F{" + applicationIdentifier + "}";
				} else {
					applicationIdentifier = "\"\"";
				}
				imageExp.setText("ar.com.fdvs.dj.core.BarcodeHelper.getBarcodeImage(" + barcodeColumn.getBarcodeType()
				+ ", " + column.getTextForExpression() + ", " + barcodeColumn
				.isShowText()
				+ ", " + barcodeColumn.isCheckSum() + ", " + applicationIdentifier + ",0,0 )");

				imageExp.setValueClass(java.awt.Image.class);
				image.setExpression(imageExp);
				image.setHeight(this.getReport().getOptions().getDetailHeight().intValue());
				image.setWidth(column.getWidth().intValue());
				image.setX(column.getPosX().intValue());
				image.setScaleImage(ScaleImageEnum.getByValue(barcodeColumn.getScaleMode().getValue()));

				image.setOnErrorType(OnErrorTypeEnum.ERROR); // FIXME should we
				// provide
				// control of
				// this to the
				// user?

				if (column.getLink() != null) {
					final String name = "column_" + this.getReport().getColumns().indexOf(column);
					HyperLinkUtil.applyHyperLinkToElement((DynamicJasperDesign) this.getDesign(), column.getLink(),
							image, name);
				}

				this.applyStyleToElement(column.getStyle(), image);

				detail.addElement(image);
			}
			/**
			 * Image columns
			 */
			else if (column instanceof ImageColumn) {
				final ImageColumn imageColumn = (ImageColumn) column;
				final JRDesignImage image = new JRDesignImage(new JRDesignStyle().getDefaultStyleProvider());
				final JRDesignExpression imageExp = new JRDesignExpression();
				imageExp.setText(column.getTextForExpression());

				imageExp.setValueClassName(imageColumn.getColumnProperty().getValueClassName());
				image.setExpression(imageExp);
				image.setHeight(this.getReport().getOptions().getDetailHeight().intValue());
				image.setWidth(column.getWidth().intValue());
				image.setX(column.getPosX().intValue());
				image.setScaleImage(ScaleImageEnum.getByValue(imageColumn.getScaleMode().getValue()));

				this.applyStyleToElement(column.getStyle(), image);

				if (column.getLink() != null) {
					final String name = "column_" + this.getReport().getColumns().indexOf(column);
					HyperLinkUtil.applyHyperLinkToElement((DynamicJasperDesign) this.getDesign(), column.getLink(),
							image, name);
				}

				detail.addElement(image);
			}
			/**
			 * Regular Column
			 */
			else {
				if (this.getReport().getOptions().isShowDetailBand()) {
					final JRDesignTextField textField = this.generateTextFieldFromColumn(column,
							this.getReport().getOptions().getDetailHeight().intValue(), null);
					// hColumnTextFields.put(column.getName(),textField);
					if (column.getLink() != null) {
						final String name = "column_" + this.getReport().getColumns().indexOf(column);
						HyperLinkUtil.applyHyperLinkToElement((DynamicJasperDesign) this.getDesign(), column.getLink(),
								textField, name);
					}
					// if (column.getTitle() != null &&
					// column.getTitle().length() > 12) {
					// column.setTitle(column.getTitle().substring(0,11) +
					// "...");
					// }
					this.transformDetailBandTextField(column, textField);

					if (!((DynamicJasperEngine) this.reportEngine).isVirtualColumn(column.getName())) {
						detail.addElement(textField);
					}

				}
			}
		}
	}

	/**
	 * set up properly the final JRStyle of the column element (for detail band) upon condition style
	 * and odd-background
	 * @param jrstyle
	 * @param column
	 */
	private void setUpConditionStyles(final JRDesignStyle jrstyle, final AbstractColumn column) {

		if (this.getReport().getOptions().isPrintBackgroundOnOddRows()
				&& Utils.isEmpty(column.getConditionalStyles())) {
			final JRDesignExpression expression = new JRDesignExpression();
			expression.setValueClass(Boolean.class);
			expression.setText(AbstractLayoutManager.EXPRESSION_TRUE_WHEN_ODD);

			final Style oddRowBackgroundStyle = this.getReport().getOptions().getOddRowBackgroundStyle();

			final JRDesignConditionalStyle condStyle = new JRDesignConditionalStyle();
			condStyle.setBackcolor(oddRowBackgroundStyle.getBackgroundColor());
			condStyle.setMode(ModeEnum.OPAQUE);

			condStyle.setConditionExpression(expression);
			jrstyle.addConditionalStyle(condStyle);

			return;
		}

		if (Utils.isEmpty(column.getConditionalStyles())) {
			return;
		}

		for (final Iterator iterator = column.getConditionalStyles().iterator(); iterator.hasNext();) {
			final ConditionalStyle condition = (ConditionalStyle) iterator.next();

			if (this.getReport().getOptions().isPrintBackgroundOnOddRows()
					&& (Transparency.TRANSPARENT == condition.getStyle().getTransparency())) { // condition
				// style
				// +
				// odd
				// row
				// (only
				// if
				// conditional
				// style's
				// background
				// is
				// transparent)

				final JRDesignExpression expressionForConditionalStyle = ExpressionUtils
						.getExpressionForConditionalStyle(condition, column.getTextForExpression());
				final String expStr = JRExpressionUtil.getExpressionText(expressionForConditionalStyle);

				// ODD
				final JRDesignExpression expressionOdd = new JRDesignExpression();
				expressionOdd.setValueClass(Boolean.class);
				expressionOdd.setText(
						"new java.lang.Boolean(" + AbstractLayoutManager.EXPRESSION_TRUE_WHEN_ODD
						+ ".booleanValue() && ((java.lang.Boolean)" + expStr + ").booleanValue() )");

				final Style oddRowBackgroundStyle = this.getReport().getOptions().getOddRowBackgroundStyle();

				final JRDesignConditionalStyle condStyleOdd = this.makeConditionalStyle(condition.getStyle());
				// Utils.copyProperties(condStyleOdd,
				// condition.getStyle().transform());
				condStyleOdd.setBackcolor(oddRowBackgroundStyle.getBackgroundColor());
				condStyleOdd.setMode(ModeEnum.OPAQUE);
				condStyleOdd.setConditionExpression(expressionOdd);
				jrstyle.addConditionalStyle(condStyleOdd);

				// EVEN
				final JRDesignExpression expressionEven = new JRDesignExpression();
				expressionEven.setValueClass(Boolean.class);
				expressionEven.setText(
						"new java.lang.Boolean(" + AbstractLayoutManager.EXPRESSION_TRUE_WHEN_EVEN
						+ ".booleanValue() && ((java.lang.Boolean)" + expStr + ").booleanValue() )");

				final JRDesignConditionalStyle condStyleEven = this.makeConditionalStyle(condition.getStyle());
				condStyleEven.setConditionExpression(expressionEven);
				jrstyle.addConditionalStyle(condStyleEven);

			} else { // No odd row, just the conditional style
				final JRDesignExpression expression = ExpressionUtils.getExpressionForConditionalStyle(condition,
						column.getTextForExpression());
				final JRDesignConditionalStyle condStyle = this.makeConditionalStyle(condition.getStyle());
				condStyle.setConditionExpression(expression);
				jrstyle.addConditionalStyle(condStyle);
			}
		}

		// The last condition is the basic one
		// ODD
		if (this.getReport().getOptions().isPrintBackgroundOnOddRows()) {

			final JRDesignExpression expressionOdd = new JRDesignExpression();
			expressionOdd.setValueClass(Boolean.class);
			expressionOdd.setText(AbstractLayoutManager.EXPRESSION_TRUE_WHEN_ODD);

			final Style oddRowBackgroundStyle = this.getReport().getOptions().getOddRowBackgroundStyle();

			final JRDesignConditionalStyle condStyleOdd = new JRDesignConditionalStyle();
			condStyleOdd.setBackcolor(oddRowBackgroundStyle.getBackgroundColor());
			condStyleOdd.setMode(ModeEnum.OPAQUE);
			condStyleOdd.setConditionExpression(expressionOdd);

			jrstyle.addConditionalStyle(condStyleOdd);

			// EVEN
			final JRDesignExpression expressionEven = new JRDesignExpression();
			expressionEven.setValueClass(Boolean.class);
			expressionEven.setText(AbstractLayoutManager.EXPRESSION_TRUE_WHEN_EVEN);

			final JRDesignConditionalStyle condStyleEven = new JRDesignConditionalStyle();
			condStyleEven.setBackcolor(jrstyle.getBackcolor());
			condStyleEven.setMode(jrstyle.getModeValue());
			condStyleEven.setConditionExpression(expressionEven);

			jrstyle.addConditionalStyle(condStyleEven);
		}
	}

	@Override
	protected void layoutGroupVariables(final DJGroup group, final JRDesignGroup jgroup, final int labelOffset) {
		// log.debug("Starting groups variables layout...");

		final JRDesignSection headerSection = (JRDesignSection) jgroup.getGroupHeaderSection();
		JRDesignBand headerBand = (JRDesignBand) headerSection.getBandsList().get(0);
		if (headerBand == null) {
			headerBand = new JRDesignBand();
			headerSection.addBand(headerBand);
			// jgroup.setGroupHeader(headerBand);
		}

		final JRDesignSection footerSection = (JRDesignSection) jgroup.getGroupFooterSection();
		JRDesignBand footerBand = (JRDesignBand) footerSection.getBandsList().get(0);
		if (footerBand == null) {
			footerBand = new JRDesignBand();
			footerSection.addBand(footerBand);
			// jgroup.setGroupFooter(footerBand);
		}

		int headerOffset = 0;

		// Show the current value above the column name
		int yOffset = 0;
		final GroupLayout layout = group.getLayout();
		// Only the value in header
		final PropertyColumn column = group.getColumnToGroupBy();

		final Integer height = group.getHeaderVariablesHeight() != null ? group.getHeaderVariablesHeight()
				: this.getReport().getOptions().getDetailHeight();

		// VALUE_IN_HEADER,
		// VALUE_IN_HEADER_WITH_HEADERS,
		// VALUE_IN_HEADER_AND_FOR_EACH,
		// VALUE_IN_HEADER_AND_FOR_EACH_WITH_HEADERS
		if (layout.isShowValueInHeader() && layout.isHideColumn() && !layout.isShowColumnName()) {
			// textfield for the current value
			final JRDesignTextField currentValue = this.generateTextFieldFromColumn(column, height.intValue(), group);
			currentValue.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);

			// The width will be all the page, except for the width of the
			// header variables
			int headerVariablesWidth = this.getReport().getOptions().getPrintableWidth();

			if (!group.getHeaderVariables().isEmpty()) {
				final DJGroupVariable leftmostcol = this.findLeftMostColumn(group.getHeaderVariables());
				headerVariablesWidth = leftmostcol.getColumnToApplyOperation().getPosX().intValue();
				if (this.groupLabelsPresent(group.getHeaderVariables())) {
					currentValue.setY(height.intValue());
					currentValue.setHeight(this.getHeaderVariablesHeight(group));
				}
			}
			currentValue.setWidth(headerVariablesWidth);

			// fix the height depending on the font size
			// currentValue.setHeight(FontHelper.getHeightFor(column.getStyle().getFont()));
			// //XXX CAREFULL
			yOffset += currentValue.getHeight();

			// Move down existing elements in the band.
			LayoutUtils.moveBandsElemnts(yOffset - 1, headerBand); // Don't know
			// why, but
			// without
			// the "-1"
			// it wont
			// show the
			// headers

			if (group.getLayout().isPrintHeaders()) {
				headerOffset += group.getHeaderHeight().intValue()
						+ this.getReport().getOptions().getDetailHeight().intValue();
			}

			headerBand.addElement(currentValue);
		}
		// DEFAULT and DEFAULT_WITH_HEADER
		else if (layout.isShowValueInHeader() && !layout.isHideColumn() && !layout.isShowColumnName()) {
			headerOffset = this.changeHeaderBandHeightForVariables(headerBand, group);
			this.insertValueInHeader(headerBand, group, headerOffset);
		}
		// VALUE_IN_HEADER_WITH_HEADERS_AND_COLUMN_NAME
		else if (layout.isShowValueInHeader() && layout.isHideColumn() && layout.isShowColumnName()) {
			// Create the element for the column name
			JRDesignTextField columnNameTf;
			if (((DynamicJasperEngine) this.reportEngine).isColumnGroupInSimpleGroup(group.getName().substring(18))) {
				columnNameTf = this.createColumnNameTextField(group, column);
			} else {
				columnNameTf = this.createColumnNameTextField(group, column, true);
			}
			columnNameTf.setMode(ModeEnum.OPAQUE);
			columnNameTf.setBackcolor(DynamicJasperStyles.defaultHeaderForGroupBackgroundColor);
			columnNameTf.setForecolor(DynamicJasperStyles.defaultHeaderForGroupFontColor);
			columnNameTf.setY(columnNameTf.getY() + headerOffset);

			// textfield for the current value
			JRDesignTextField currentValue;
			if (((DynamicJasperEngine) this.reportEngine).isColumnGroupInSimpleGroup(group.getName().substring(18))) {
				currentValue = this.generateTextFieldFromColumn(column, height.intValue(), group, false);
			} else {
				currentValue = this.generateTextFieldFromColumn(column, height.intValue(), group, true);
			}

			// The width will be (width of the page) - (column name width)
			currentValue.setWidth(this.getReport().getOptions().getPrintableWidth() - columnNameTf.getWidth());
			// The x position for the current value is right next to the column
			// name
			currentValue.setX(columnNameTf.getWidth());

			// fix the height depending on the font size
			currentValue.setHeight(FontHelper.getHeightFor(column.getStyle().getFont()));
			columnNameTf.setHeight(currentValue.getHeight());

			yOffset += currentValue.getHeight();

			// Move down existing elements in the band.

			if (((DynamicJasperEngine) this.reportEngine).isColumnGroupInSimpleGroup(group.getName().substring(18))) {
				LayoutUtils.moveBandsElemnts(yOffset, headerBand);
				headerBand.addElement(columnNameTf);
				headerBand.addElement(this.createPaddingTextField(columnNameTf));
				headerBand.addElement(currentValue);
			} else {
				final List vMultiGroupColumns = this.getOtherColumnsForMultigroup(column.getName());
				int yPosition = columnNameTf.getHeight();
				yOffset = 0;
				for (int i = vMultiGroupColumns.size() - 1; i >= 0; i--) {
					final JRDesignTextField columnMultiNameTf = this.createColumnNameTextField(group,
							this.getColumnFromVirtualColumn((AbstractColumn) vMultiGroupColumns.get(i)),
							i == (vMultiGroupColumns.size() - 1) ? false : true);
					// columnMultiNameTf.setY(yPosition);
					columnMultiNameTf.setX(columnNameTf.getX());
					columnMultiNameTf.setMode(ModeEnum.OPAQUE);
					columnMultiNameTf.setBackcolor(DynamicJasperStyles.defaultHeaderForGroupBackgroundColor);
					columnMultiNameTf.setForecolor(DynamicJasperStyles.defaultHeaderForGroupFontColor);

					if (i == (vMultiGroupColumns.size() - 1)) {
						columnMultiNameTf.getLineBox().getBottomPen().setLineWidth(1f);
						columnMultiNameTf.getLineBox().getBottomPen().setLineColor(Color.BLACK);
						columnMultiNameTf.setBold(true);
					} else {
						columnMultiNameTf.getLineBox().getBottomPen().setLineWidth(0f);
						columnMultiNameTf.getLineBox().getBottomPen().setLineColor(Color.BLACK);
						columnMultiNameTf.setBold(true);
					}

					// textfield for the current value
					final JRDesignTextField currentMultiValue = this.generateTextFieldFromColumn(
							(AbstractColumn) vMultiGroupColumns.get(i), height.intValue(), group,
							i == (vMultiGroupColumns.size() - 1) ? false : true);
					currentMultiValue.setX(currentValue.getX());
					columnMultiNameTf.setWidth(currentMultiValue.getX());
					// The width will be (width of the page) - (column name
					// width)
					currentMultiValue
					.setWidth(this.getReport().getOptions().getPrintableWidth() - columnNameTf.getWidth());

					// The x position for the current value is right next to the
					// column name

					// fix the height depending on the font size
					currentMultiValue.setHeight(FontHelper.getHeightFor(column.getStyle().getFont()));
					columnMultiNameTf.setHeight(currentMultiValue.getHeight());
					yOffset += currentMultiValue.getHeight();
					yPosition += currentMultiValue.getHeight();
					// Move down existing elements in the band.
					LayoutUtils.moveBandsElemnts(currentMultiValue.getHeight(), headerBand);
					headerBand.addElement(columnMultiNameTf);
					headerBand.addElement(currentMultiValue);
				}
				LayoutUtils.moveBandsElemnts(currentValue.getHeight(), headerBand);
				columnNameTf.setBold(true);
				headerBand.addElement(columnNameTf);
				headerBand.addElement(currentValue);
				LayoutUtils.moveBandsElemnts(5, headerBand);
			}

		}

		this.placeVariableInBand(group.getHeaderVariables(), group, jgroup, DJConstants.HEADER, headerBand,
				headerOffset);
		this.placeVariableInBand(group.getFooterVariables(), group, jgroup, DJConstants.FOOTER, footerBand,
				labelOffset);
	}

	protected JRDesignElement createPaddingTextField(final JRDesignTextField columnTextField) {
		final JRDesignTextField designStaticText = new JRDesignTextField();
		designStaticText.setHeight(columnTextField.getHeight());
		designStaticText.setWidth(columnTextField.getX());
		designStaticText.setX(0);
		designStaticText.setY(columnTextField.getY());
		designStaticText.setExpression(ExpressionUtils.createStringExpression("\"" + "\""));
		designStaticText.setMode(ModeEnum.OPAQUE);
		designStaticText.setBackcolor(DynamicJasperStyles.defaultHeaderForGroupPaddingColor);
		return designStaticText;
	}

	public String getFittingTextForWidth(final int availableWidth, String text, final Font font) {
		int completeWidth = FontHelper.getWidthFor(font, text);
		while ((availableWidth < completeWidth) && (text.length() > 0)) {
			text = text.substring(0, text.length() - 1);
			completeWidth = FontHelper.getWidthFor(font, text + "...");
		}
		text += "...";
		return text;

	}

	protected List getOtherColumnsForMultigroup(final String column) {
		final List vOtherColNames = new Vector();
		final List vAllAbstractColumns = new ArrayList();
		vOtherColNames.addAll(((DynamicJasperEngine) this.reportEngine).getMultiGroupColumnsForColumn(column));
		vAllAbstractColumns.addAll(this.getReport().getColumns());
		vAllAbstractColumns.addAll(((DynamicJasperEngine) this.reportEngine).getMultiGroupColumns());
		final List vMultiGroupColumns = new Vector();
		for (int i = 0; i < vOtherColNames.size(); i++) {
			for (int j = 0; j < vAllAbstractColumns.size(); j++) {
				if (((AbstractColumn) vAllAbstractColumns.get(j)).getName().equals(vOtherColNames.get(i))) {
					vMultiGroupColumns.add(vAllAbstractColumns.get(j));
					break;
				}
			}
		}
		return vMultiGroupColumns;
	}

	/**
	 * Layout columns in groups by reading the corresponding report options.
	 * @throws LayoutException
	 */
	@Override
	protected void layoutGroups() {
		for (final Iterator iter = this.getReport().getColumnsGroups().iterator(); iter.hasNext();) {
			final DJGroup columnsGroup = (DJGroup) iter.next();
			final JRDesignGroup jgroup = this.getJRGroupFromDJGroup(columnsGroup);

			jgroup.setStartNewPage(columnsGroup.getStartInNewPage().booleanValue());
			jgroup.setStartNewColumn(columnsGroup.getStartInNewColumn().booleanValue());
			jgroup.setReprintHeaderOnEachPage(columnsGroup.getReprintHeaderOnEachPage().booleanValue());

			final JRDesignSection headerSection = (JRDesignSection) jgroup.getGroupHeaderSection();
			final JRDesignSection footerSection = (JRDesignSection) jgroup.getGroupFooterSection();

			JRDesignBand header = LayoutUtils.getBandFromSection(headerSection);
			JRDesignBand footer = LayoutUtils.getBandFromSection(footerSection);

			// double check to prevent NPE
			if (header == null) {
				header = new JRDesignBand();
				headerSection.addBand(header);
			}
			if (footer == null) {
				footer = new JRDesignBand();
				footerSection.addBand(footer);
			}

			header.setHeight(columnsGroup.getHeaderHeight().intValue());
			// footer.setHeight( getFooterVariableHeight(columnsGroup));
			footer.setHeight(columnsGroup.getFooterHeight().intValue());

			header.setSplitType(LayoutUtils.getSplitTypeFromBoolean(columnsGroup.isAllowHeaderSplit()));
			footer.setSplitType(LayoutUtils.getSplitTypeFromBoolean(columnsGroup.isAllowFooterSplit()));

			if (columnsGroup.getLayout().isPrintHeaders()) {
				boolean found = false;
				boolean skipPreviousGroupHeaders = false;
				final int groupIdx = this.getReport().getColumnsGroups().indexOf(columnsGroup);
				if (groupIdx > 0) {
					final DJGroup prevG = (DJGroup) this.getReport().getColumnsGroups().get(groupIdx - 1);
					if (!(prevG.getColumnToGroupBy() instanceof GlobalGroupColumn)) {
						skipPreviousGroupHeaders = !prevG.getLayout().isShowValueForEachRow();
					}
				}
				for (final Iterator iterator = this.getVisibleColumns().iterator(); iterator.hasNext();) {
					final AbstractColumn col = (AbstractColumn) iterator.next();

					// If in a nested group, header for column prior to this
					// groups column
					// depends on configuration
					if (col.getTitle().length() > 12) {
						final Integer width = col.getWidth();
						if ((width.intValue() * 1.5) < FontHelper.getWidthFor(col.getStyle().getFont(),
								col.getTitle())) {
							col.setTitle(this.getFittingTextForWidth((int) (width.intValue() * 1.5), col.getTitle(),
									col.getStyle().getFont()));
						}
					}

					if (col.equals(columnsGroup.getColumnToGroupBy())) {
						found = true;
					}

					if (!found && skipPreviousGroupHeaders) {
						continue;
					}

					final JRDesignTextField designTextField = this.createColumnNameTextField(columnsGroup, col);
					designTextField.setPositionType(PositionTypeEnum.FLOAT); // XXX
					// changed
					// to
					// see
					// what
					// happens
					// (must
					// come
					// from
					// the
					// column
					// position
					// property)
					designTextField.setStretchType(StretchTypeEnum.NO_STRETCH); // XXX
					// changed
					// to
					// see
					// what
					// happens
					// (must
					// come
					// from
					// the
					// column
					// property)
					header.addElement(designTextField);
				}
			}

			final DJGroupLabel label = columnsGroup.getFooterLabel();
			if (label != null /* && !footerVariables.isEmpty() */) {
				final List footerVariables = columnsGroup.getFooterVariables();
				final PropertyColumn col = columnsGroup.getColumnToGroupBy();
				// JRDesignBand band = (JRDesignBand)jgroup.getGroupFooter();
				final JRDesignBand band = footer;
				int x = 0, y = 0;
				// max width
				int width = this.getDesign().getPageWidth() - this.getDesign().getLeftMargin()
						- this.getDesign().getRightMargin();
				int height = label.getHeight();
				int yOffset = 0;
				if (label.getLabelPosition() == LabelPosition.LEFT) {
					final DJGroupVariable lmvar = this.findLeftMostColumn(footerVariables);

					x = col.getPosX().intValue(); // label starts in the
					// column-to-group-by x
					// position
					y = this.findYOffsetForGroupLabel(band);
					if (lmvar != null) {
						final AbstractColumn lmColumn = lmvar.getColumnToApplyOperation();
						width = lmColumn.getPosX().intValue() - x;
					} else {
						width -= x;
					}
					height = this.getFooterVariableHeight(columnsGroup);
				} else if (label.getLabelPosition() == LabelPosition.RIGHT) {
					final DJGroupVariable rmvar = this.findRightMostColumn(footerVariables);

					if (rmvar != null) {
						final AbstractColumn rmColumn = rmvar.getColumnToApplyOperation();
						x = rmColumn.getPosX().intValue() + rmColumn.getWidth().intValue();
					} else {
						x = col.getPosX().intValue(); // label starts in the
						// column-to-group-by x
						// position
					}
					y = this.findYOffsetForGroupLabel(band);
					width -= x;
					height = this.getFooterVariableHeight(columnsGroup);
				} else if (label.getLabelPosition() == LabelPosition.TOP) {
					x = col.getPosX().intValue(); // label starts in the
					// column-to-group-by x
					// position
					width -= x;
					yOffset = height;
				} else if (label.getLabelPosition() == LabelPosition.BOTTOM) {
					x = col.getPosX().intValue(); // label starts in the
					// column-to-group-by x
					// position
					y = this.getFooterVariableHeight(columnsGroup);
					width -= x;
				}
				this.layoutGroupFooterLabels(columnsGroup, jgroup, x, y, width, height);
				this.layoutGroupVariables(columnsGroup, jgroup, yOffset);
			} else {
				this.layoutGroupVariables(columnsGroup, jgroup, 0);
			}

			this.layoutGroupSubreports(columnsGroup, jgroup);
			this.layoutGroupCrosstabs(columnsGroup, jgroup);
		}
	}

	protected JRDesignTextField createColumnNameTextField(final DJGroup columnsGroup, final AbstractColumn col,
			final boolean requireMultiGroupStyle) {
		final JRDesignTextField designStaticText = new JRDesignTextField();
		final JRDesignExpression exp = new JRDesignExpression();
		exp.setText("\"" + this.getColumnFromVirtualColumn(col).getTitle() + "\"");
		exp.setValueClass(String.class);
		designStaticText.setExpression(exp);
		designStaticText.setHeight(columnsGroup.getHeaderHeight().intValue());
		designStaticText.setWidth(col.getWidth().intValue());
		designStaticText.setX(col.getPosX().intValue());
		designStaticText.setY(col.getPosY().intValue());
		Style headerStyle = columnsGroup.getColumnHeaderStyle(col);

		if (headerStyle == null) {
			headerStyle = columnsGroup.getDefaultColumnHeaederStyle();
		}
		if (headerStyle == null) {
			headerStyle = col.getHeaderStyle();
		}
		return designStaticText;
	}

	/**
	 * @param variables
	 * @param djGroup
	 * @param jgroup
	 * @param type (header or footer)
	 * @param band
	 * @param yOffset
	 */
	@Override
	protected void placeVariableInBand(final List variables, final DJGroup djGroup, final JRDesignGroup jgroup, final String type,
			final JRDesignBand band, final int yOffset) {
		if ((variables == null) || variables.isEmpty()) {
			return;
		}

		final boolean inFooter = DJConstants.FOOTER.equals(type);

		int height = 0;
		if (inFooter) {
			height = this.getFooterVariableHeight(djGroup);
		} else {
			height = this.getHeaderVariablesHeight(djGroup);
		}

		final Iterator it = variables.iterator();
		int yOffsetGlabel = 0;
		while (it.hasNext()) {
			final DJGroupVariable var = (DJGroupVariable) it.next();
			final AbstractColumn col = var.getColumnToApplyOperation();

			final String variableName = var.getName();

			// Add the group label
			final DJGroupLabel label = var.getLabel();
			JRDesignTextField labelTf = null;
			if (label != null) {
				JRDesignExpression labelExp;
				if (label.isJasperExpression()) {
					labelExp = ExpressionUtils.createStringExpression(label.getText());
				} else if (label.getLabelExpression() != null) {
					labelExp = ExpressionUtils.createExpression(variableName + "_labelExpression",
							label.getLabelExpression());
				} else {
					// labelExp = ExpressionUtils.createStringExpression("\""+
					// Utils.escapeTextForExpression(label.getText())+ "\"");
					labelExp = ExpressionUtils.createStringExpression("\"" + label.getText() + "\"");
				}
				labelTf = new JRDesignTextField();
				labelTf.setExpression(labelExp);
				labelTf.setWidth(col.getWidth().intValue());
				labelTf.setHeight(label.getHeight());
				labelTf.setX(col.getPosX().intValue());
				labelTf.setY(yOffset);
				yOffsetGlabel = labelTf.getHeight();
				if (inFooter) {
					labelTf.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
				}
				this.applyStyleToElement(label.getStyle(), labelTf);
				band.addElement(labelTf);

			}

			JRDesignExpression expression = new JRDesignExpression();
			final JRDesignTextField textField = new JRDesignTextField();

			if (inFooter) {
				textField.setEvaluationTime(EvaluationTimeEnum.NOW); // This
				// will
				// enable
				// textfield
				// streching
			} else {
				textField.setEvaluationTime(EvaluationTimeEnum.GROUP);
			}

			if (var.getValueExpression() != null) {
				expression = ExpressionUtils.createExpression(variableName + "_valueExpression",
						var.getValueExpression());
			} else {
				this.setTextAndClassToExpression(expression, var, col, variableName);
			}

			if ((var.getOperation() != DJCalculation.COUNT) && (var.getOperation() != DJCalculation.DISTINCT_COUNT)) {
				textField.setPattern(col.getPattern());
			}

			if (col instanceof PercentageColumn) {
				final PercentageColumn pcol = (PercentageColumn) col;
				expression.setText(pcol.getTextForExpression(djGroup, djGroup, type));
				expression.setValueClassName(pcol.getValueClassNameForExpression());
				textField.setEvaluationTime(EvaluationTimeEnum.AUTO);
			} else {
				textField.setEvaluationGroup(jgroup);
			}

			textField.setKey(variableName);
			textField.setExpression(expression);

			if (inFooter) {
				textField.setPositionType(PositionTypeEnum.FIX_RELATIVE_TO_TOP);
			}

			int posColumn = 0;
			if (this.hColsPositions.get(col.getName()) != null) {
				posColumn = Integer.parseInt(this.hColsPositions.get(col.getName()).toString());
			}

			if (this.isShowedRowNumber) {
				textField.setX(
						(((this.getReport().getOptions().getPrintableWidth() - DynamicJasperEngine.widthRowNumbers)
								/ (this.hColsPositions.size() == 0 ? 1 : this.hColsPositions
										.size()))
								* posColumn) + DynamicJasperEngine.widthRowNumbers);
			} else {
				textField.setX((this.getReport().getOptions().getPrintableWidth()
						/ (this.hColsPositions.size() == 0 ? 1 : this.hColsPositions.size())) * posColumn);
			}

			textField.setX(col.getPosX().intValue());

			// if (yOffset!=0)
			textField.setY(yOffset + yOffsetGlabel);

			textField.setHeight(0 + height); // XXX be carefull with the
			// "2+ ..."

			textField.setWidth(col.getWidth().intValue());

			textField.setKey("variable_for_column_" + this.getVisibleColumns().indexOf(col) + "_in_group_"
					+ this.getDesign().getGroupsList().indexOf(jgroup));

			// Assign the style to the element.
			// First we look for the specific element style, then the default
			// style for the group variables
			// and finally the column style.
			final Style defStyle = DJConstants.HEADER.equals(type) ? djGroup.getDefaulHeaderVariableStyle()
					: djGroup.getDefaulFooterVariableStyle();

			if (var.getStyle() != null) {
				this.applyStyleToElement(var.getStyle(), textField);
			} else if (col.getStyle() != null) {
				// Last resource is to use the column style, but a copy of it
				// because
				// the one in the internal cache can get modified by the layout
				// manager (like in the odd row case)
				Style style = col.getStyle();
				try {
					style = (Style) style.clone();
					style.setName(null); // set to null to make
					// applyStyleToElement(...) assign a
					// name
				} catch (final Exception e) {
					CustomClassicLayoutManager.logger.trace(null, e);
				}
				this.applyStyleToElement(style, textField);
			} else if (defStyle != null) {
				this.applyStyleToElement(defStyle, textField);
			}

			if (var.getPrintWhenExpression() != null) {
				final JRDesignExpression exp = ExpressionUtils.createExpression(variableName + "_printWhenExpression",
						var.getPrintWhenExpression());
				textField.setPrintWhenExpression(exp);
				if (labelTf != null) {
					labelTf.setPrintWhenExpression(exp);
				}
			}

			band.addElement(textField);

		}

		if (djGroup.getColumnToGroupBy() instanceof GlobalGroupColumn) {
			int totalWidth = 0;

			final DJGroupVariable leftmostColumn = this.findLeftMostColumn(variables);
			totalWidth = leftmostColumn.getColumnToApplyOperation().getPosX().intValue();

			final GlobalGroupColumn globalCol = (GlobalGroupColumn) djGroup.getColumnToGroupBy();

			final JRDesignTextField globalTextField = new JRDesignTextField();
			final JRDesignExpression globalExp = new JRDesignExpression();
			globalExp.setText(globalCol.getTextForExpression());
			globalExp.setValueClassName(globalCol.getValueClassNameForExpression());
			globalTextField.setExpression(globalExp);

			globalTextField.setHeight(0 + height); // XXX be carefull with the
			// "2+ ..."
			globalTextField.setWidth(totalWidth);

			globalTextField.setX(0);
			if (type.equals(DJConstants.HEADER)) {
				globalTextField.setY(yOffset);
			}
			globalTextField.setKey("global_legend_" + type);

			this.applyStyleToElement(globalCol.getStyle(), globalTextField);

			band.addElement(globalTextField);
		}
	}

	protected JRDesignTextField generateTextFieldFromColumn(final AbstractColumn col, final int height, final DJGroup group,
			final boolean requireMultiGroupStyle) {

		final JRDesignTextField textField = new JRDesignTextField();
		final JRDesignExpression exp = new JRDesignExpression();

		if ((col.getPattern() != null) && "".equals(col.getPattern().trim())) {
			textField.setPattern(col.getPattern());
		}

		if (col.getTruncateSuffix() != null) {
			textField.getPropertiesMap().setProperty(JRTextElement.PROPERTY_TRUNCATE_SUFFIX, col.getTruncateSuffix());
		}

		final List columnsGroups = this.getReport().getColumnsGroups();
		if (col instanceof PercentageColumn) {
			final PercentageColumn pcol = (PercentageColumn) col;

			if (group == null) { // we are in the detail band
				final DJGroup innerMostGroup = (DJGroup) columnsGroups.get(columnsGroups.size() - 1);
				exp.setText(pcol.getTextForExpression(innerMostGroup));
			} else {
				exp.setText(pcol.getTextForExpression(group));
			}

			textField.setEvaluationTime(EvaluationTimeEnum.AUTO);

		} else {
			exp.setText(col.getTextForExpression());
		}

		exp.setValueClassName(col.getValueClassNameForExpression());
		textField.setExpression(exp);
		textField.setWidth(col.getWidth().intValue());
		textField.setX(col.getPosX().intValue());
		textField.setY(col.getPosY().intValue());
		textField.setHeight(height);
		textField.setBlankWhenNull(col.getBlankWhenNull());

		textField.setPattern(col.getPattern());

		textField.setPrintRepeatedValues(col.getPrintRepeatedValues().booleanValue());

		textField.setPrintWhenDetailOverflows(true);

		Style columnStyle = col.getStyle();

		if (columnStyle == null) {
			columnStyle = this.getReport().getOptions().getDefaultDetailStyle();
		}

		this.applyStyleToElement(columnStyle, textField);
		final JRDesignStyle jrstyle = (JRDesignStyle) textField.getStyle();

		if (group != null) {
			final int index = columnsGroups.indexOf(group);
			final JRDesignGroup previousGroup = this.getJRGroupFromDJGroup(group);
			textField.setPrintWhenGroupChanges(previousGroup);
			/**
			 * Since a group column can share the style with non group columns, if oddRow coloring is enabled,
			 * we modified this shared style to have a colored background on odd rows. We don't want that for
			 * group columns, that's why we create our own style from the existing one, and remove proper
			 * odd-row conditional style if present
			 */
			final JRDesignStyle groupStyle = Utils.cloneStyle(jrstyle);

			groupStyle.setName(groupStyle.getFontName() + "_for_group_" + index);

			if (requireMultiGroupStyle) {
				groupStyle.getLineBox().getBottomPen().setLineWidth(0f);
			} else {
				groupStyle.getLineBox().getBottomPen().setLineWidth(1f);
				groupStyle.getLineBox().getBottomPen().setLineColor(Color.BLACK);
				textField.getLineBox().getBottomPen().setLineWidth(1f);
				textField.getLineBox().getBottomPen().setLineColor(Color.BLACK);
			}

			textField.setStyle(groupStyle);
			try {
				this.getDesign().addStyle(groupStyle);
			} catch (final JRException e) {
				CustomClassicLayoutManager.logger.trace(null, e);
			}

		} else {

			final JRDesignStyle alternateStyle = Utils.cloneStyle(jrstyle);

			alternateStyle.setName(alternateStyle.getFontName() + "_for_column_" + col.getName());
			alternateStyle.getConditionalStyleList().clear();
			textField.setStyle(alternateStyle);
			try {
				this.getDesign().addStyle(alternateStyle);
			} catch (final JRException e) {
				CustomClassicLayoutManager.logger.trace(null, e);
			}

			this.setUpConditionStyles(alternateStyle, col);
		}
		return textField;

	}

	public AbstractColumn getColumnFromVirtualColumn(final AbstractColumn virtualColumn) {
		if (((DynamicJasperEngine) this.reportEngine).isVirtualColumn(virtualColumn.getName())) {
			for (int i = 0; i < this.getVisibleColumns().size(); i++) {
				if (((AbstractColumn) this.getVisibleColumns().get(i)).getName()
						.equals(((DynamicJasperEngine) this.reportEngine)
								.getColumnFromVirtualColumn(virtualColumn.getName()))) {
					return (AbstractColumn) this.getVisibleColumns().get(i);
				}
			}
		}
		return virtualColumn;
	}

}
