package com.vaadin.osgi.provider;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.Servlet;

import org.osgi.framework.Filter;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceObjects;
import org.osgi.framework.ServiceReference;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.context.ServletContextHelper;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.osgi.api.Constants;
import com.vaadin.ui.UI;

import osgi.enroute.configurer.api.RequireConfigurerExtender;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = {
		"com.vaadin.osgi.provider" })
@Designate(ocd = Configuration.class, factory = true)
@RequireConfigurerExtender
public class VaadinServerProvider {

	static final Logger LOGGER = LoggerFactory.getLogger(VaadinServerProvider.class);

	private Configuration config;
	private ComponentContext context;

	private ServiceRegistration<ServletContextHelper> contextReg;

	private ServiceTracker<UI, ServiceObjects<UI>> uiTracker;

	private ServiceRegistration<Servlet> servletReg;

	@Activate
	void activate(Configuration config, ComponentContext context) {
		this.config = config;
		this.context = context;

		activateVaadinServer(config);
	}

	private void activateVaadinServer(Configuration config) {

		uiTracker = new ServiceTracker<>(context.getBundleContext(), createUIFilter(config),
				new ServiceTrackerCustomizer<UI, ServiceObjects<UI>>() {
					@Override
					public ServiceObjects<UI> addingService(ServiceReference<UI> reference) {
						ServiceObjects<UI> serviceObjects = context.getBundleContext().getServiceObjects(reference);
						doActivateVaadinServer(serviceObjects);
						return serviceObjects;
					}

					@Override
					public void modifiedService(ServiceReference<UI> reference, ServiceObjects<UI> service) {

					}

					@Override
					public void removedService(ServiceReference<UI> reference, ServiceObjects<UI> service) {
						doDeactivateVaadinServer();
					}
				});
		uiTracker.open();
	}

	private Filter createUIFilter(Configuration config) {
		String filter = String.format("(&(objectClass=com.vaadin.ui.UI)(%s=%s))", Constants.PROP__VAADIN_CONFIG,
				config.configName());
		try {
			return context.getBundleContext().createFilter(filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Activates the context path based on the config.
	 */
	private void doActivateContextPath() {
		// Translate the config to proper http whiteboard properties
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, config.configName());
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, config.contextPath());

		// create a new helper service and pass in the http whiteboard
		// properties
		contextReg = context.getBundleContext().registerService(ServletContextHelper.class, new ServletContextHelper() {
		}, properties);
	}

	/**
	 * Activates the Vaadin server based on the config.
	 * 
	 * @param serviceObjects
	 */
	protected void doActivateVaadinServer(ServiceObjects<UI> serviceObjects) {

		// first, do activate the context path
		doActivateContextPath();

		OSGiServlet servlet = new OSGiServlet(new OSGiUIProvider(serviceObjects), config);

		// Translate the config to proper http whiteboard properties
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_SELECT,
				String.format("(%s=%s)", HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, config.configName()));
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_NAME, config.configName());
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN, config.alias());

		servletReg = context.getBundleContext().registerService(Servlet.class, servlet, properties);
	}

	protected void doDeactivateVaadinServer() {
		if (servletReg != null) {
			servletReg.unregister();
			servletReg = null;
		}
	}

	@Deactivate
	void deactivate() {
		uiTracker.close();
		uiTracker = null;

		if (servletReg != null) {
			servletReg.unregister();
			servletReg = null;
		}

		contextReg.unregister();
		contextReg = null;
	}
}
