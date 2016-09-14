package com.vaadin.osgi.api;

public interface Constants {

	/**
	 * This property is required to map the UI class to the Vaadin Server. It
	 * must match the value in the configuration.
	 */
	String PROP__VAADIN_CONFIG = "com.vaadin.config";

	/**
	 * This property is required to define the fully qualified UI class in the
	 * UI Component. The class is required by internal implementation.
	 */
	String PROP__VAADIN_UI_CLASS = "uiClass";

	/**
	 * The default name of the config.
	 */
	String DEFAULT_CONFIG = "config.default";

}
