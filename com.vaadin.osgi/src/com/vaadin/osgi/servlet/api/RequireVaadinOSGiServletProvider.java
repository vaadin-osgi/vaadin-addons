package com.vaadin.osgi.servlet.api;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import org.osgi.namespace.implementation.ImplementationNamespace;

import aQute.bnd.annotation.headers.RequireCapability;

/**
 * Require an implementation for the Vaadin OSGi provider.
 */
@RequireCapability(ns = ImplementationNamespace.IMPLEMENTATION_NAMESPACE, filter = "(&("
		+ ImplementationNamespace.IMPLEMENTATION_NAMESPACE + "=" + OSGiConstants.OSGI_PROVIDER_NAME
		+ ")${frange;${version;==;" + OSGiConstants.OSGI_PROVIDER_VERSION + "}})")
@Retention(RetentionPolicy.CLASS)
public @interface RequireVaadinOSGiServletProvider {

}
