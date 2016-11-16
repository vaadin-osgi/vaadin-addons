package com.vaadin.osgi.example.services.addressbook;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.osgi.example.services.api.ISubApplication;
import com.vaadin.ui.Label;

@Component(scope=ServiceScope.PROTOTYPE)
public class AddressBookSubApplication implements ISubApplication {

	Label content = new Label("AddressBook");
	private AddressbookComponent comp;
	
	@Override
	public String getCaption() {
		return "Addressbook";
	}

	@Override
	public com.vaadin.ui.Component getContent() {
		if(comp != null) {
			return comp;
		}
		
		comp = new AddressbookComponent();
		comp.init();
		return comp;
	}

}
