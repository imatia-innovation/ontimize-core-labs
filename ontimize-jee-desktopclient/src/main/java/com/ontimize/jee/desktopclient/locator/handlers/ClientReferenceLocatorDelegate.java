package com.ontimize.jee.desktopclient.locator.handlers;

import java.awt.Component;
import java.util.List;

import com.ontimize.jee.common.locator.ClientReferenceLocator;
import com.ontimize.jee.common.locator.InitialContext;
import com.ontimize.jee.common.tools.proxy.AbstractInvocationDelegate;

public class ClientReferenceLocatorDelegate extends AbstractInvocationDelegate implements ClientReferenceLocator {

	@Override
	public int getChatCheckTime() {
		return 0;
	}

	@Override
	public int getMessageCheckTime() {
		return 0;
	}

	@Override
	public boolean hasChat() {
		return false;
	}

	@Override
	public void showMessageDialog(final Component component) {

	}

	@Override
	public String getUser() {
		return null;
	}

	@Override
	public InitialContext getInitialContext() {
		return null;
	}

	@Override
	public Object getReference(final String s) throws Exception {
		return null;
	}

	@Override
	public Object getLocaleId(final int i) throws Exception {
		return null;
	}

	@Override
	public void addModuleMemoryEntity(final String localEntityPackage, final List<String> localEntities) {
	}

	@Override
	public boolean isLocalMode() {
		return false;
	}

	@Override
	public boolean isAllowCertificateLogin() {
		return false;
	}

}
