package com.vaadin.osgi.provider;

import java.util.Dictionary;
import java.util.Hashtable;

import javax.servlet.ServletContext;

import org.osgi.framework.ServiceRegistration;
import org.osgi.service.cm.ConfigurationAdmin;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.metatype.MetaTypeService;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.osgi.provider.ContextPathFactory.Config;

/**
 * This Factory works as an OSGi-service bridge.
 * <p>
 * Every instance of it uses a single input from {@link ConfigurationAdmin} to
 * create a single {@link ServletContext} for the passed {@link Config}. <br>
 * Note: One instance of {@link ContextPathFactory this class} is created for
 * each {@link ServletContext}. The different instances are configured by Apache
 * WebConsole or a <code>config/config.json</code> inside any bundle. This
 * yields that OSGi-Http will create a new {@link ServletContext}
 * <p>
 * {@link Config} acts as the data which is being shared between this factory
 * and the config admin. On startup and every time a config is added, a new
 * instance about {@link ContextPathFactory} is created an initiated with the
 * {@link Config} passed to {@link #activate(Config, ComponentContext)
 * #activate}.
 * <p>
 * During the {@link #activate(Config, ComponentContext) #activate} the passed
 * {@link Config Config} from configuration admin are used to create and
 * register a Dummy-Service ({@link ServletContextHelper}). This service will
 * yield the {@link HttpWhiteboardConstants OSGi Http Whiteboard} implementation
 * to create a new {@link ServletContext}. The type of the service (its class)
 * has no influcence about the resulting {@link ServletContext}. Just the
 * properties passed at service registration define the servlet context. And
 * these service properties are derived from the {@link Config Config}.
 * <p>
 * This means, that configs are translated to proper registered
 * {@link HttpWhiteboardConstants OSGi Http Whiteboard} services.
 */
@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Designate(ocd = Config.class, factory = true)
public class ContextPathFactory {

	static final Logger LOGGER = LoggerFactory.getLogger(ContextPathFactory.class);

	/**
	 * The Config annotation used by the {@link MetaTypeService}. The metatype
	 * information is being compiled to XML by IDE Tooling or Build. Apache
	 * WebConsole will use it to create the UI for managing configurations used
	 * by the {@link ConfigurationAdmin}.
	 */
	@ObjectClassDefinition(name = "Servlet Contexts", description = "Configures many servlet contexts")
	@interface Config {

		@AttributeDefinition(name = "context name", defaultValue = "Default", description = "The unique name of the context", required = true)
		String contextName() default "Default";

		@AttributeDefinition(name = "context path", defaultValue = "/", description = "The context path which should be created in the web server", required = false)
		String contextPath() default "/";

	}

	private ServiceRegistration<ServletContextHelper> reg;

	@Activate
	void activate(Config config, ComponentContext context) {
		// Translate the config to proper http whiteboard properties
		Dictionary<String, Object> properties = new Hashtable<>();
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_NAME, config.contextName());
		properties.put(HttpWhiteboardConstants.HTTP_WHITEBOARD_CONTEXT_PATH, config.contextPath());

		// create a new helper service and pass in the http whiteboard
		// properties
		reg = context.getBundleContext().registerService(ServletContextHelper.class, new ServletContextHelper(),
				properties);
	}

	@Deactivate
	void deactivate() {
		reg.unregister();
		reg = null;
	}

	/**
	 * A helper class to satisfy add the {@link HttpWhiteboardConstants OSGi
	 * Http Whiteboard} pattern. Only the properties passed during service
	 * registration is important. This class is just a service dummy.
	 */
	public static class ServletContextHelper {

	}
}
