package com.vaadin.osgi.example.services.counter;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.osgi.example.services.api.ISubApplication;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

@Component(scope=ServiceScope.PROTOTYPE)
public class CounterSubApplication extends CustomComponent implements ISubApplication {

	private TextField counterText;
	private UI ui;

	@Override
	public void attach() {
		super.attach();

		counterText = new TextField();
		counterText.setValue(Integer.toString(0));
		setCompositionRoot(counterText);
		
		ui = getUI();

		new Thread(new Runnable() {
			private int count;

			@Override
			public void run() {
				while (true) {
					count++;
					ui.access(new Runnable() {

						@Override
						public void run() {
							counterText.setValue(Integer.toString(count));
						}
					});
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
					}
				}
			}
		}).start();

	}

	@Override
	public String getCaption() {
		return "Counter";
	}

	@Override
	public com.vaadin.ui.Component getContent() {
		return this;
	}

}
