/*
 * Copyright 2016 Florian Pirchner <florian.pirchner@gmail.com>, Sampsa Sohlman.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.osgi.portlet.liferay.provider;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.portlet.Portlet;

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
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.osgi.api.OSGiConstants;
import com.vaadin.ui.UI;

import osgi.enroute.configurer.api.RequireConfigurerExtender;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE, configurationPid = {
		"com.vaadin.osgi.portlet.liferay.provider" })
@Designate(ocd = Configuration.class, factory = true)
@RequireConfigurerExtender
public class VaadinPortletProvider {

	static final Logger LOGGER = LoggerFactory.getLogger(VaadinPortletProvider.class);

	Configuration config;
	ComponentContext context;

	ServiceTracker<UI, ServiceObjects<UI>> uiTracker;

	private ServiceRegistration<Portlet> portletReg;

	@Activate
	void activate(Configuration config, ComponentContext context) {
		this.config = config;
		this.context = context;

		activateVaadinPortlet(config);
	}

	protected void activateVaadinPortlet(Configuration config) {

		uiTracker = new ServiceTracker<>(context.getBundleContext(), createUIFilter(config),
				new ServiceTrackerCustomizer<UI, ServiceObjects<UI>>() {
					@Override
					public ServiceObjects<UI> addingService(ServiceReference<UI> reference) {
						ServiceObjects<UI> serviceObjects = context.getBundleContext().getServiceObjects(reference);
						doActivateVaadinPortlet(serviceObjects);
						return serviceObjects;
					}

					@Override
					public void modifiedService(ServiceReference<UI> reference, ServiceObjects<UI> service) {

					}

					@Override
					public void removedService(ServiceReference<UI> reference, ServiceObjects<UI> service) {
						shutdownPortlet();
					}
				});
		uiTracker.open();
	}

	/**
	 * Create a filter to find the proper UI {@link ServiceObjects} based on the
	 * {@link OSGiConstants#PROP__VAADIN_CONFIG}
	 * 
	 * @param config
	 * @return
	 */
	protected Filter createUIFilter(Configuration config) {
		String filter = String.format("(&(objectClass=com.vaadin.ui.UI)(%s=%s))", OSGiConstants.PROP__VAADIN_CONFIG,
				config.configName());
		try {
			return context.getBundleContext().createFilter(filter);
		} catch (InvalidSyntaxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * Activates the Vaadin portlet based on the config.
	 * 
	 * @param serviceObjects
	 */
	protected void doActivateVaadinPortlet(ServiceObjects<UI> serviceObjects) {

		OSGiUIProvider provider = new OSGiUIProvider(serviceObjects);

		Dictionary<String, Object> properties = new Hashtable<String, Object>();
		ServiceReference<UI> ref = serviceObjects.getServiceReference();
		copyProperty(ref, properties, "com.liferay.portlet.display-category", "category.vaadin");
		copyProperty(ref, properties, "javax.portlet.name", provider.getPortletName());
		copyProperty(ref, properties, "javax.portlet.display-name", config.displayName());
		copyProperty(ref, properties, "javax.portlet.security-role-ref", new String[] { "power-user", "user" });
		copyProperty(ref, properties, VaadinWebResource.JAVAX_PORTLET_RESOURCES_INIT_PARAM,
				VaadinWebResource.JAVAX_PORTLET_RESOURCES_INIT_VALUE);

		portletReg = context.getBundleContext().registerService(Portlet.class, new OSGiPortlet(provider, config),
				properties);

		// TODO Sampsa - do you need this? - see contextPath!
		LOGGER.debug("Registered Vaadin portlet with alias '" + config.alias() + "' under contextpath '"
				+ config.contextPath() + "'");
	}

	public void copyProperty(ServiceReference<UI> serviceReference, Dictionary<String, Object> properties, String key,
			Object defaultValue) {

		Object value = serviceReference.getProperty(key);
		if (value != null) {
			properties.put(key, value);
		} else if (value == null && defaultValue != null) {
			properties.put(key, defaultValue);
		}
	}

	@Deactivate
	void deactivate() {
		uiTracker.close();
		uiTracker = null;

		shutdownPortlet();
	}

	private void shutdownPortlet() {
		if (portletReg != null) {
			portletReg.unregister();
			portletReg = null;
		}
	}
}
