package com.vaadin.osgi.provider;

import org.osgi.framework.Bundle;
import org.osgi.framework.ServiceObjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.osgi.api.OSGiConstants;
import com.vaadin.osgi.provider.OSGiServlet.LocalVaadinServletService;
import com.vaadin.server.ClientConnector.DetachEvent;
import com.vaadin.server.ClientConnector.DetachListener;
import com.vaadin.server.UIClassSelectionEvent;
import com.vaadin.server.UICreateEvent;
import com.vaadin.server.UIProvider;
import com.vaadin.shared.communication.PushMode;
import com.vaadin.shared.ui.ui.Transport;
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

		// we need to load the UI class for the ui provider
		Bundle registringBundle = serviceObjects.getServiceReference().getBundle();
		try {
			_uiClass = (Class<UI>) registringBundle.loadClass(
					(String) serviceObjects.getServiceReference().getProperty(OSGiConstants.PROP__VAADIN_UI_CLASS));
		} catch (ClassNotFoundException e) {
			LOGGER.error("{}", e);
		}
	}

	@Override
	public Class<? extends UI> getUIClass(UIClassSelectionEvent event) {
		return _uiClass;
	}

	@Override
	public UI createInstance(UICreateEvent event) {
		final ServiceObjects<UI> serviceObjects = _serviceObjects;
		final UI ui = serviceObjects.getService();

		LocalVaadinServletService service = (LocalVaadinServletService) event.getRequest().getService();
		applyPageTitle(service, ui);

		ui.addDetachListener(new DetachListener() {
			@Override
			public void detach(DetachEvent event) {
				serviceObjects.ungetService(ui);
				LOGGER.debug("unregistered UI " + ui.toString());
			}
		});
		return ui;
	}

	private void applyPageTitle(LocalVaadinServletService service, UI ui) {
		String title = service.getConfiguration().pageTitle();
		if (title != null && !title.equals("")) {
			ui.getPage().setTitle(title);
		}
	}

	@Override
	public String getTheme(UICreateEvent event) {
		LocalVaadinServletService service = (LocalVaadinServletService) event.getRequest().getService();
		String theme = service.getConfiguration().theme();
		if (theme == null || theme.equals("")) {
			theme = super.getTheme(event);
		}
		return theme;
	}

	@Override
	public String getPageTitle(UICreateEvent event) {
		LocalVaadinServletService service = (LocalVaadinServletService) event.getRequest().getService();
		String title = service.getConfiguration().pageTitle();
		if (title == null || title.equals("")) {
			title = super.getPageTitle(event);
		}
		return title;
	}

	@Override
	public PushMode getPushMode(UICreateEvent event) {
		LocalVaadinServletService service = (LocalVaadinServletService) event.getRequest().getService();
		com.vaadin.osgi.provider.PushMode pushMode = service.getConfiguration().pushMode();
		if (pushMode == null) {
			return super.getPushMode(event);
		}
		return PushMode.valueOf(pushMode.name());
	}

	@Override
	public Transport getPushTransport(UICreateEvent event) {
		LocalVaadinServletService service = (LocalVaadinServletService) event.getRequest().getService();
		com.vaadin.osgi.provider.PushTransport pushTransport = service.getConfiguration().pushTransport();
		if (pushTransport == null) {
			return super.getPushTransport(event);
		}
		return Transport.valueOf(pushTransport.name());
	}

}
