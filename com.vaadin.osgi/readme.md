# 

These bundles offer a Vaadin OSGi R6 brige based on OSGi prototype scope. It is heavily based on Configuration Manager. Configurations can easily be done by Apache felix webconsole or by a JSON File.

The main goal of this implementation is to provide a very simple way to create OSGi-Applications based on Vaadin. The AddressBookUi sample shows how easy it is, to create a new application.

## Your own application

To create your own application, you need to define 2 things.

1.  UI-Class annotated properly
2.  A configuration used by ConfigAdmin

### UI-Class

	@Component(scope = ServiceScope.PROTOTYPE, service = UI.class, property = {
		Constants.PROP__VAADIN_UI_CLASS + "=com.vaadin.osgi.example.addressbook.AddressBookUi",
		Constants.PROP__VAADIN_CONFIG + "=sample.addressbook" })
	public class AddressBookUi extends UI {
		@Override
		protected void init(VaadinRequest request) {
			setContent(new Label("AddressBook"));
		}
	}
 Just define a Vaadin UI as explained in Vaadin documentation. Then you need to annotate the class with the `@Component` annotation.
 The `@Component` annotation tells the OSGi framework, that there is an OSGi-Service (scope=prototype) available to create many instances of the 	 UI class.
 
 Additionally you need to add two properties to the service registration:
 
*   Constants.PROP__VAADIN_UI_CLASS - the value must be the fully qualified name of the UI class. The property is a workaround to provide the UiProvider with the UI class.
*   Constants.PROP__VAADIN_CONFIG - a unique ID which wires the UI class with the configuration from ConfigAdmin.

### Configuration
There are 3 ways available to create a proper configuration:


1.  Use the Apache felix webconsole
2.  Create a JSON file with configuration
3.  Use the API of the ConfigAdmin

#### Apache felix webconsole
Its a pretty simple way to configure a Vaadin application. If you have implemented the UI class, then remember the value of the property `Constants.PROP__VAADIN_CONFIG` 
Start your OSGi application and point a browser to <http://localhost:8080/system/console/configMgr>. Use "admin" and "admin" for the credentials.

You find a row called "Vaadin Server Config". Press the "add" button.

A form is shown. Put the value of `Constants.PROP__VAADIN_CONFIG` (eg. sample.addressbook) into the first field "config name" and specify an alias; eg. "/addr".
All other fields are optional. If you press the "save" button, your Vaadin application will startup.

Point your browser to <http://localhost:8080/addr> and your Vaadin UI shows up. If you specified a context path, you also need to add the context path to your URL. Eg. <http://localhost:8080/myapp/addr>.

#### JSON file
The Vaadin Addon uses the enRoute simple configurer. This bundle is aware about JSON files located in the folder /configuration. You need to specify the the values from the configuration (same as in webconsole) in the JSON file.

On startup of the server, the enRoute simple configurer finds the configuration and will do all required calls to the ConfigAdmin for you.

#### API of ConfigAdmin
If you want to have control about the Vaadin instances in your code, just use the API of the ConfigAdmin.


## References

