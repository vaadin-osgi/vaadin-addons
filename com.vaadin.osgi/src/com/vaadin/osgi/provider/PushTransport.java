package com.vaadin.osgi.provider;

/**
 * Transport modes for Push
 */
public enum PushTransport {

	/**
	 * Websockets
	 */
	WEBSOCKET,
	/**
	 * Websockets for server to client, XHR for client to server
	 * 
	 * @since 7.6
	 */
	WEBSOCKET_XHR,
	/**
	 * HTTP long polling
	 */
	LONG_POLLING;

}
