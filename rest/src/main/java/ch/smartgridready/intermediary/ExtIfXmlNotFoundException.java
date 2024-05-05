package ch.smartgridready.intermediary;

class ExtIfXmlNotFoundException extends RuntimeException {

	ExtIfXmlNotFoundException(String name) {
		super("Could not find externalInterfaceXxml: " + name);
	}
}
