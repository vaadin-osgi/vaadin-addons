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
package com.vaadin.osgi.example.addressbook;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.annotations.Push;
import com.vaadin.osgi.servlet.api.Constants;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Component(scope = ServiceScope.PROTOTYPE, service = UI.class, property = {
		Constants.PROP__VAADIN_UI_CLASS + "=com.vaadin.osgi.example.addressbook.AddressBookUi",
		Constants.PROP__VAADIN_CONFIG + "=sample.addressbook" })
@Push
public class AddressBookUi extends UI {

	@Override
	protected void init(VaadinRequest request) {
		setContent(new Label("AddressBook"));
	}

}
