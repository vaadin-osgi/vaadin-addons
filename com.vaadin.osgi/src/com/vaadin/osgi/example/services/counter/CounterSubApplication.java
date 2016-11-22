package com.vaadin.osgi.example.services.counter;

import java.util.concurrent.atomic.AtomicBoolean;

import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ServiceScope;

import com.vaadin.osgi.example.services.api.ISubApplication;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;

@Component(scope=ServiceScope.PROTOTYPE)
public class CounterSubApplication extends CustomComponent implements ISubApplication {

	TextField counterText;
	UI ui;
	AtomicBoolean active = new AtomicBoolean(true);
	
	@Override
	public void attach() {
		super.attach();

		counterText = new TextField();
		counterText.setValue(Integer.toString(0));
		setCompositionRoot(counterText);
		
		ui = getUI();

		ui.addDetachListener(e -> {
			active.set(false);
		});
		
		new Thread(new Runnable() {
			private int count;

			@Override
			public void run() {
				while (active.get()) {
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
				System.out.println("Stopped counter");
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

	public void destroy() {
		active.set(false);
	}
	
}
