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
package com.vaadin.osgi.provider;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.server.Constants;
import com.vaadin.server.DefaultDeploymentConfiguration;
import com.vaadin.server.DeploymentConfiguration;
import com.vaadin.server.ServiceException;
import com.vaadin.server.UIProvider;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.server.VaadinSession;

@SuppressWarnings("serial")
public class OSGiServlet extends VaadinServlet {

	static final Logger LOGGER = LoggerFactory.getLogger(OSGiServlet.class);

	private UIProvider uiProvider;
	private Configuration config;

	public OSGiServlet(UIProvider uiProvider, Configuration config) {
		this.uiProvider = uiProvider;
		this.config = config;
	}

	@Override
	protected DeploymentConfiguration createDeploymentConfiguration(Properties initParameters) {
		fillParameters(initParameters);
		return new DefaultDeploymentConfiguration(getClass(), initParameters);
	}

	private void fillParameters(Properties initParameters) {
		if (config.widgetset() != null && !config.widgetset().trim().isEmpty()) {
			initParameters.setProperty(Constants.PARAMETER_WIDGETSET, config.widgetset());
		}
		initParameters.setProperty(SERVLET_PARAMETER_PRODUCTION_MODE, Boolean.toString(config.productionMode()));

		if (config.pushMode() != null) {
			initParameters.setProperty(SERVLET_PARAMETER_PUSH_MODE, config.pushMode().name());
		}

		if (config.resourceCacheTime() >= 0) {
			initParameters.setProperty(SERVLET_PARAMETER_RESOURCE_CACHE_TIME,
					Integer.toString(config.resourceCacheTime()));
		}

		if (config.heartbeatInterval() != 0) {
			initParameters.setProperty(SERVLET_PARAMETER_HEARTBEAT_INTERVAL,
					Integer.toString(config.heartbeatInterval()));
		}
	}

	@Override
	protected VaadinServletService createServletService(DeploymentConfiguration deploymentConfiguration)
			throws ServiceException {
		try {

			LocalVaadinServletService service = new LocalVaadinServletService(this, deploymentConfiguration);
			service.init();
			service.setClassLoader(new ClassLoader() {
				@Override
				public Class<?> loadClass(String name) throws ClassNotFoundException {
					try {
						Class<?> loadClass = OSGiServlet.class.getClassLoader().loadClass(name);
						return loadClass;
					} catch (Exception e) {
						LOGGER.error("{}", e);
						throw e;
					}
				}
			});

			return service;
		} catch (Exception e) {
			LOGGER.error("{}", e);
			throw e;
		}
	}

	/**
	 * Local servlet service the carry the config and to add the
	 * {@link UIProvider} to the session.
	 */
	class LocalVaadinServletService extends VaadinServletService {

		public LocalVaadinServletService(VaadinServlet servlet, DeploymentConfiguration deploymentConfiguration)
				throws ServiceException {
			super(servlet, deploymentConfiguration);
		}

		Configuration getConfiguration() {
			return config;
		}

		@Override
		protected VaadinSession createVaadinSession(VaadinRequest request) throws ServiceException {
			VaadinSession session = new VaadinSession(request.getService());
			session.addUIProvider(uiProvider);
			return session;
		}
	}
}
