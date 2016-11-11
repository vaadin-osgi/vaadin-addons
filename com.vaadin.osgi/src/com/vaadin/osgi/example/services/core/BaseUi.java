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
package com.vaadin.osgi.example.services.core;

import java.util.HashSet;
import java.util.Set;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.annotations.Push;
import com.vaadin.osgi.api.OSGiConstants;
import com.vaadin.osgi.example.services.api.ISubApplication;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
@Component(scope = ServiceScope.PROTOTYPE, service = UI.class, property = {
		OSGiConstants.PROP__VAADIN_UI_CLASS + "=com.vaadin.osgi.example.services.core.BaseUi",
		OSGiConstants.PROP__VAADIN_CONFIG + "=sample.services.core" })
@Push
public class BaseUi extends UI {

	Set<ISubApplication> subApps = new HashSet<>();
	private TabSheet tabSheet;
	private Label emptyLabel;
	private Tab emptyLabelTab;

	@Override
	protected void init(VaadinRequest request) {

		VerticalLayout mainLayout = new VerticalLayout();
		mainLayout.setMargin(true);
		setContent(mainLayout);

		tabSheet = new TabSheet();
		mainLayout.addComponent(tabSheet);

		emptyLabel = new Label(
				"Hey!<br> You missed to <b>start a bundle</b> providing an <b>implementation for ISubApplication</b>",
				ContentMode.HTML);

		if (subApps.isEmpty()) {
			emptyLabelTab = tabSheet.addTab(emptyLabel, "start");
		} else {
			for (ISubApplication subApp : new HashSet<>(subApps)) {
				notifySubApplicationAdded(subApp);
			}
		}

	}

	@Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.DYNAMIC, unbind = "removeSubApplication")
	void addSubApplication(ISubApplication subApplication) {
		subApps.add(subApplication);

		notifySubApplicationAdded(subApplication);
	}

	void removeSubApplication(ISubApplication subApplication) {
		subApps.remove(subApplication);

		notifySubApplicationRemoved(subApplication);
	}

	/**
	 * Does internally stuff if subApp is added.
	 * 
	 * @param subApplication
	 */
	void notifySubApplicationAdded(ISubApplication subApplication) {
		access(new Runnable() {

			@Override
			public void run() {
				if (tabSheet.getTab(emptyLabel) != null) {
					tabSheet.removeTab(emptyLabelTab);
				}
				tabSheet.addTab(subApplication.getContent(), subApplication.getCaption());
			}
		});
	}

	/**
	 * Does internally stuff if subApp is removed.
	 * 
	 * @param subApplication
	 */
	void notifySubApplicationRemoved(ISubApplication subApplication) {

		access(new Runnable() {

			@Override
			public void run() {
				Tab tab = tabSheet.getTab(subApplication.getContent());
				if (tab != null) {
					tabSheet.removeTab(tab);
				}

				if (tabSheet.getSelectedTab() == null) {
					emptyLabelTab = tabSheet.addTab(emptyLabel, "start");
				}
			}
		});
	}
}
