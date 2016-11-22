package com.vaadin.osgi.example.services.api;

import com.vaadin.ui.Component;

public interface ISubApplication {

	/**
	 * Returns the caption of the sub application.
	 * 
	 * @return
	 */
	String getCaption();

	/**
	 * Returns the content of the sub application. Different calls to this
	 * method need to return the same instance.
	 * 
	 * @return
	 */
	Component getContent();

	/**
	 * Destroys the sup application. Any threads started need to be shutdown
	 * immediately.
	 */
	void destroy();
}
