package com.vaadin.osgi.provider;

import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import com.vaadin.osgi.api.Constants;

@ObjectClassDefinition(name = "Vaadin Server Config", description = "Configures the Vaadin Server")
public @interface Configuration {

	@AttributeDefinition(name = "config name", defaultValue = Constants.DEFAULT_CONFIG, description = "The unique name of the config. Add the property com.vaadin.config to the UI component to address the proper server instance.", required = true)
	String configName() default Constants.DEFAULT_CONFIG;

	@AttributeDefinition(name = "description", defaultValue = "", description = "Description about the application", required = false)
	String description() default "";

	@AttributeDefinition(name = "context path", defaultValue = "/", description = "The context path which should be created in the web server", required = false)
	String contextPath() default "/";

	@AttributeDefinition(name = "alias", defaultValue = "/", description = "Alias to access application", required = true)
	String alias() default "/";

//	@AttributeDefinition(name = "uiClass", defaultValue = "", description = "The fully qualified name of the UI class. It is used to wire the server with the UI", required = true)
//	String uiClassName() default "";

	@AttributeDefinition(name = "widgetset", defaultValue = "", description = "The widgetset that should be used. Or empty for default widgetset.", required = false)
	String widgetset() default "";

	@AttributeDefinition(name = "pageTitle", defaultValue = "My Vaadin App", description = "The title of the application", required = false)
	String pageTitle() default "My Vaadin App";

	@AttributeDefinition(name = "theme", defaultValue = "valo", description = "The theme of the application", required = false)
	String theme() default "valo";

	@AttributeDefinition(name = "productionMode", description = "False, if debug mode is used. True for production mode.", required = false)
	boolean productionMode() default false;

	@AttributeDefinition(name = "heartbeatInterval", description = "The heartbeat interval in seconds. Enter 0 for Vaadin default. Or a negative value to turn of the heartbeat.", required = false)
	int heartbeatInterval() default 0;

	@AttributeDefinition(name = "resourceCacheTime", description = "The time a browser may cache a resource in seconds. Enter -1 for Vaadin default.", required = false)
	int resourceCacheTime() default -1;

	@AttributeDefinition(name = "pushMode", description = "Whether and how push should be used in Vaadin", required = false)
	PushMode pushMode() default PushMode.DISABLED;

	@AttributeDefinition(name = "pushTransport", description = "The type of transport used with push", required = false)
	PushTransport pushTransport() default PushTransport.WEBSOCKET;
}
