package com.ontimize.module;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.w3c.dom.Node;

import com.ontimize.jee.common.builder.CustomNode;

public class OModule {

	protected String id;

	protected String resources;

	protected String clientBaseClasspath = "";

	protected List<CustomNode> formManagers = new ArrayList<CustomNode>();

	protected CustomNode toolbar;

	protected CustomNode menu;

	protected String toolbarListener;

	protected String menuListener;

	protected String localEntityPackage;

	protected List<String> localEntities;

	// Server
	protected String serverPackage;

	protected Properties entityProperties;

	protected String entityClass;

	protected Node remoteReferences;

	protected Node references;

	public OModule(final String id) {
		this.id = id;
	}

	public String getClientBaseClasspath() {
		return this.clientBaseClasspath;
	}

	public void setClientBaseClasspath(final String clientPackage) {
		this.clientBaseClasspath = clientPackage;
		if ((this.clientBaseClasspath == null) || (this.clientBaseClasspath.length() == 0)) {
			this.clientBaseClasspath = "";
		} else {
			this.clientBaseClasspath = this.clientBaseClasspath.replace('.', '/').concat("/");
		}
	}

	public List<CustomNode> getFormManagers() {
		return this.formManagers;
	}

	public void setFormManagers(final List<CustomNode> formManagers) {
		this.formManagers = formManagers;
	}

	public CustomNode getToolbar() {
		return this.toolbar;
	}

	public void setToolbar(final CustomNode toolbar) {
		this.toolbar = toolbar;
	}

	public String getToolbarListener() {
		return this.toolbarListener;
	}

	public void setToolbarListener(final String toolbarListener) {
		this.toolbarListener = toolbarListener;
	}

	public String getId() {
		return this.id;
	}

	public void setId(final String id) {
		this.id = id;
	}

	public String getResources() {
		return this.resources;
	}

	public void setResources(final String resources) {
		this.resources = resources;
	}

	public CustomNode getMenu() {
		return this.menu;
	}

	public void setMenu(final CustomNode menu) {
		this.menu = menu;
	}

	public String getMenuListener() {
		return this.menuListener;
	}

	public void setMenuListener(final String menuListener) {
		this.menuListener = menuListener;
	}

	public String getLocalEntityPackage() {
		return this.localEntityPackage;
	}

	public void setLocalEntityPackage(final String localEntityPackage) {
		this.localEntityPackage = localEntityPackage;
	}

	public List<String> getLocalEntities() {
		return this.localEntities;
	}

	public void setLocalEntities(final List<String> localEntities) {
		this.localEntities = localEntities;
	}

	public String getServerPackage() {
		return this.serverPackage;
	}

	public void setServerPackage(final String serverPackage) {
		this.serverPackage = serverPackage;
	}

	public Properties getEntityProperties() {
		return this.entityProperties;
	}

	public void setEntityProperties(final Properties entityProperties) {
		this.entityProperties = entityProperties;
	}

	public String getEntityClass() {
		return this.entityClass == null ? "com.ontimize.db.DefaultTableEntity" : this.entityClass;
	}

	public void setEntityClass(final String entityClass) {
		this.entityClass = entityClass;
	}

	public Node getRemoteReferences() {
		return this.remoteReferences;
	}

	public void setRemoteReferences(final Node remoteReferences) {
		this.remoteReferences = remoteReferences;
	}

	public Node getReferences() {
		return this.references;
	}

	public void setReferences(final Node references) {
		this.references = references;
	}

	@Override
	public boolean equals(final Object obj) {
		if (obj instanceof OModule) {
			return this.id.equals(((OModule) obj).getId());
		}
		return false;
	}

	@Override
	public int hashCode() {
		return this.id.hashCode();
	}

}
