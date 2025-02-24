package com.ontimize.db.query;

import java.util.ResourceBundle;

import com.ontimize.gui.ApplicationManager;
import com.ontimize.jee.common.db.ContainsExtendedSQLConditionValuesProcessor;
import com.ontimize.jee.common.db.SQLStatementBuilder.Expression;
import com.ontimize.jee.common.db.SQLStatementBuilder.Field;
import com.ontimize.jee.common.db.query.ParameterField;

public class ContainsSQLConditionValuesProcessorHelper {

	public static String renderQueryConditionsExpressBundle(final Expression e, final ResourceBundle bundle) {
		final StringBuilder sb = new StringBuilder();
		renderQueryConditionsExpress(e, sb, bundle);
		return sb.toString();
	}

	public static void renderQueryConditionsExpress(final Expression e, final StringBuilder sb, final ResourceBundle bundle) {
		if (e == null) {
			return;
		}

		if (e.getLeftOperand() instanceof Expression) {
			sb.append("(" + renderQueryConditionsExpressBundle((Expression) e.getLeftOperand(), bundle));
			sb.append(" " + e.getOperator().toString() + " ");
			sb.append(renderQueryConditionsExpressBundle((Expression) e.getRightOperand(), bundle) + ")");
		} else {
			sb.append("(");
			if (e.getLeftOperand() != null) {
				// Simple expressions
				sb.append(ApplicationManager.getTranslation(e.getLeftOperand().toString(), bundle));
				if (e.getOperator() != null) {
					sb.append(" " + e.getOperator().toString());
				} else {
					sb.append(" null ");
				}
				if (e.getRightOperand() != null) {
					if (e.getRightOperand() instanceof ParameterField) {
						if (((ParameterField) e.getRightOperand()).getValue() == null) {
							sb.append(" {Parameter=?} ");
						} else {
							sb.append(" {Parameter='" + ((ParameterField) e.getRightOperand()).getValue() + "'} ");
						}
					} else if (e.getRightOperand() instanceof Field) {
						sb.append(ApplicationManager.getTranslation(e.getRightOperand().toString(), bundle));
					} else if (e.getRightOperand() instanceof String) {
						sb.append(
								" '" + ApplicationManager.getTranslation(e.getRightOperand().toString(), bundle) + "'");
					} else if (e.getRightOperand() instanceof Expression) {
						sb.append(" " + ContainsExtendedSQLConditionValuesProcessor
								.createQueryConditionsExpress((Expression) e.getRightOperand()));
					}
				}
			}
			sb.append(")");
		}
	}

}
