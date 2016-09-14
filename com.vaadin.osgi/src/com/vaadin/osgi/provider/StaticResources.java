package com.vaadin.osgi.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.http.whiteboard.HttpWhiteboardConstants;
import org.osgi.service.log.LogService;

@SuppressWarnings("serial")
@Component(name = "osgi.enroute.vaadin.resources", //
property = {
		HttpWhiteboardConstants.HTTP_WHITEBOARD_SERVLET_PATTERN + "=/VAADIN", Constants.SERVICE_RANKING
				+ ":Integer=100" }, service = Servlet.class, configurationPolicy = ConfigurationPolicy.OPTIONAL, immediate = true)
public class StaticResources extends HttpServlet {
	BundleContext context;

	@Reference
	LogService logger;

	@Activate
	void init(BundleContext context) {
		this.context = context;
	}

	@Override
	protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String path = "VAADIN" + req.getPathInfo();
			URI uri = new URI(path);

			path = uri.getPath();
			logger.log(LogService.LOG_DEBUG, "Accessing resource: " + path);
			System.out.print(path);

			for (Bundle b : context.getBundles()) {
				URL entry = b.getEntry(path);
				if (entry != null) {
					System.out.println(" " + b);
					try (InputStream in = entry.openStream()) {
						byte[] buffer = new byte[10000];
						for (int n = in.read(buffer); n > 0; n = in.read(buffer))
							resp.getOutputStream().write(buffer, 0, n);

						return;
					}
				}
			}
			logger.log(LogService.LOG_DEBUG, "Resource not found: " + path);
			resp.sendError(404, path);
		} catch (Exception e) {
			resp.sendError(500, e.getMessage());
		}
	}

}
