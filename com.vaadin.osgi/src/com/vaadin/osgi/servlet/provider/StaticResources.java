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
package com.vaadin.osgi.servlet.provider;

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
