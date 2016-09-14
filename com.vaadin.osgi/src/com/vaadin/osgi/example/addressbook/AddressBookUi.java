package com.vaadin.osgi.example.addressbook;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.osgi.api.Constants;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

@SuppressWarnings("serial")
@Component(scope = ServiceScope.PROTOTYPE, service = UI.class, property = {
		Constants.PROP__VAADIN_UI_CLASS + "=com.vaadin.osgi.example.addressbook.AddressBookUi",
		Constants.PROP__VAADIN_CONFIG + "=sample.addressbook" })
public class AddressBookUi extends UI {

	@Override
	protected void init(VaadinRequest request) {
		setContent(new Label("AddressBook"));
	}

}
