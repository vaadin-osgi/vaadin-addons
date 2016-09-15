package com.vaadin.osgi.provider;

/**
 * The mode of bidirectional ("push") communication that is in use.
 */
public enum PushMode {

	/**
	 * Push is disabled. Regular AJAX requests are used to communicate between
	 * the client and the server. Asynchronous messages from the server are not
	 * possible. {@link com.vaadin.ui.UI#push() ui.push()} throws
	 * IllegalStateException.
	 * <p>
	 * This is the default mode unless
	 * {@link com.vaadin.server.DeploymentConfiguration#getPushMode()
	 * configured} otherwise.
	 */
	DISABLED,

	/**
	 * Push is enabled. A bidirectional channel is established between the
	 * client and server and used to communicate state changes and RPC
	 * invocations. The client is not automatically updated if the server-side
	 * state is asynchronously changed; {@link com.vaadin.ui.UI#push()
	 * ui.push()} must be explicitly called.
	 */
	MANUAL,

	/**
	 * Push is enabled. Like {@link #MANUAL}, but asynchronous changes to the
	 * server-side state are automatically pushed to the client once the session
	 * lock is released.
	 */
	AUTOMATIC;

}
