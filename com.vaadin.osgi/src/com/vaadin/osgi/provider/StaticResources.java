package com.vaadin.osgi.provider;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings("serial")
public class StaticResources extends HttpServlet {

	static final Logger LOGGER = LoggerFactory.getLogger(StaticResources.class);

	final BundleContext context;

	public StaticResources(BundleContext context) {
		super();
		this.context = context;
	}

	@Override
	protected synchronized void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		try {
			String path = "VAADIN" + req.getPathInfo();
			URI uri = new URI(path);

			path = uri.getPath();
			LOGGER.debug("Accessing resource: " + path);
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
			LOGGER.debug("Resource not found: " + path);
			resp.sendError(404, path);
		} catch (Exception e) {
			resp.sendError(500, e.getMessage());
		}
	}

}
