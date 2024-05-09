package ch.smartgridready.intermediary.exception;

public class ExtIfXmlNotFoundException extends RuntimeException {

	public ExtIfXmlNotFoundException(String name) {
		super("Could not find externalInterfaceXxml: " + name);
	}
}
