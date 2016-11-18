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
package com.vaadin.osgi.servlet.api;

public interface Constants {

	/**
	 * The name of the provider for capabilities and requirements.
	 */
	String OSGI_PROVIDER_NAME = "com.vaadin.osgi.servlet";

	/**
	 * The version of the provider for capabilities and requirements.
	 */
	String OSGI_PROVIDER_VERSION = "1.0.0";

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
