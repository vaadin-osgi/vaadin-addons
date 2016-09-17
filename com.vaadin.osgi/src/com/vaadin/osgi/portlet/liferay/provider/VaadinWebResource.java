package com.vaadin.osgi.portlet.liferay.provider;

public abstract class VaadinWebResource {
	public static final String JAVAX_PORTLET_RESOURCES_INIT_PARAM = "javax.portlet.init-param.vaadin.resources.path";
	public static final String JAVAX_PORTLET_RESOURCES_INIT_VALUE = "/o/vaadin${vaadin.version}";
	public static final String JAVAX_PORTLET_RESOURCES_PATH = JAVAX_PORTLET_RESOURCES_INIT_PARAM + "="
			+ JAVAX_PORTLET_RESOURCES_INIT_VALUE;
}
