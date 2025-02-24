package com.ontimize.gui.login;

import com.ontimize.gui.Application;
import com.ontimize.gui.i18n.Internationalization;
import com.ontimize.jee.common.locator.EntityReferenceLocator;

public interface ILoginDialog extends Internationalization {

    public static final String ENCRYPT = "encrypt";

    public static final String LOGIN_ICON = "loginicon";

    public static final String LOGIN_TEXT = "loginText";

    public static final String REMEMBER_PASSWORD = "allowRememberPassword";

    public static final String REMEMBER_LAST_LOGIN = "remember_last_login";

    public static final String LAST_LOGIN = "last_login";

    public static final String DNS_OPTIONS = "dns_options";

    public static final String CONNECT_TO = "connect_to";

    public static final String WRONG_LOGIN_KEY = "unauthorized_user";

    public static final String ERROR_LOGIN_KEY = "error_login";

    public static final String WINDOW_TITLE = "mainapplication.access_control";

    public static final String CONNECTING_KEY = "mainapplication.connecting";

    public static final String CONNECT_TO_TOOLTIP = "mainapplication.select_server_to_connect";

    public static final String CONNECT_TO_KEY = "mainapplication.connect_to";

    public boolean login();

    public EntityReferenceLocator getEntityReferenceLocator();

    public Application getApplication();

    public boolean isLoggedIn();

    public boolean isRememberPassword();

    /**
     * Returns a String with password value
     * @return
     */
    public String getPasswordValue();

    public boolean isRememberLogin();

    /**
     * Returns a String with user value
     * @return
     */
    public String getUserValue();

    public boolean isServerSelection();

    public String getConnectedServer();

    /**
     * Check the user and password. If the user and password are valid then start session for this user.
     * @return a boolean with true whether the user and password are valid.
     * @throws Exception
     */
    public boolean checkLogin() throws Exception;

}
