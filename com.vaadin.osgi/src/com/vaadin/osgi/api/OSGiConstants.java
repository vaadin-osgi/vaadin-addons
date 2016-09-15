package com.vaadin.osgi.api;

public interface OSGiConstants {

	/**
	 * This property is required to map the UI class to the Vaadin Server. It
	 * must be defined in the UI Component as property and also in the
	 * configuration which uses the UI-class.
	 */
	String PROP__VAADIN_CONFIG = "com.vaadin.config";

	/**
	 * This property is required to define the fully qualified UI class in the
	 * UI Component. The class is required by internal implementation.
	 */
	String PROP__VAADIN_UI_CLASS = "uiClass";

	/**
	 * The default value for {@link #PROP__VAADIN_CONFIG}.
	 */
	String DEFAULT_CONFIG_NAME = "config.default";

}
