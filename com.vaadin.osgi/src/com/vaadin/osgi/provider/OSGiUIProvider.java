package com.vaadin.osgi.provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
public class OSGiUIProvider extends UIProvider {

	static final Logger LOGGER = LoggerFactory.getLogger(OSGiUIProvider.class);

	ServiceObjects<UI> _serviceObjects;
	Class<UI> _uiClass;
	UI _ui;

	@SuppressWarnings("unchecked")
	public OSGiUIProvider(ServiceObjects<UI> serviceObjects) {
		super();

		_serviceObjects = serviceObjects;
		Bundle registringBundle = serviceObjects.getServiceReference().getBundle();
		// TODO OSGi - non final idea...
		try {
			_uiClass = (Class<UI>) registringBundle
					.loadClass((String) serviceObjects.getServiceReference().getProperty("uiClass"));
		} catch (ClassNotFoundException e) {
			LOGGER.error("{}", e);
		}
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return _uiClass;
	}

	@SuppressWarnings("serial")
	@Override
	public UI createInstance(UICreateEvent event) {
		final ServiceObjects<UI> serviceObjects = _serviceObjects;
		final UI ui = serviceObjects.getService();

		ui.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				serviceObjects.ungetService(ui);
				if (LOGGER.isDebugEnabled()) {
					LOGGER.debug("unregistered UI " + ui.toString());
				}
			}
		});
		return ui;
	}

	// public UI getDefaultUI() {
	// return _ui;
	// }

	public String getDefaultDisplayName() {
		String name = _uiClass.getName();
		int beginIndex = name.lastIndexOf(".");
		beginIndex = beginIndex > 0 ? beginIndex + 1 : 0;

		name = name.substring(beginIndex);

		return name;
	}

	public String getPortletName() {
		return _uiClass.getName();
	}

}
