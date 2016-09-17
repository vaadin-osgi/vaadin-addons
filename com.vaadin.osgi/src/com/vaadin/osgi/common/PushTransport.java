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
package com.vaadin.osgi.common;

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
