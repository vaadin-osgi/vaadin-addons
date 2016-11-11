package com.vaadin.osgi.example.services.addressbook;

import org.osgi.service.component.annotations.Component;

import com.vaadin.osgi.example.services.api.ISubApplication;
import com.vaadin.ui.Label;

@Component
public class AddressBookSubApplication implements ISubApplication {

	Label content = new Label("AddressBook");
	
	@Override
	public String getCaption() {
		return "Addressbook";
	}

	@Override
	public com.vaadin.ui.Component getContent() {
		return content;
	}

}
