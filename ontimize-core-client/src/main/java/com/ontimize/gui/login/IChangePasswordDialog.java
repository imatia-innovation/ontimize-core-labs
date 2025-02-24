package com.ontimize.gui.login;

import java.awt.Window;

import com.ontimize.gui.Application;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public interface IChangePasswordDialog extends Internationalization {

    public static final String ENCRYPT = "encrypt";

    public static final String LOGIN_ICON = "loginicon";

    public static final String CHANGE_PASSWORD_ICON = "changepasswordicon";

    public static final String CHANGE_PASSWORD_DIALOG_CLASS = "changepassworddialogclass";

    public static final String PASSWORD_LEVEL = "securitylevel";

    public static final String SECURITY_BUTTON = "securitybutton";

    public static final String SECURITY_LABEL = "securitymessage";

    public static final String SECURITY_PASSWORD_PARAMETERS = "SecurityPasswordParameters";

    public static final String WINDOW_TITLE = "ChangePasswordDialog.title";

    public static final String CHANGE_PASSWORD_TEXT = "changepasswordtext";

    public EntityReferenceLocator getEntityReferenceLocator();

    public Application getApplication();

    public String getPasswordValue();

    public EntityResult setPassword(String password);

    public void showChangePassword();

    public void showChangePasswordInParentLocation(Window w);

}
