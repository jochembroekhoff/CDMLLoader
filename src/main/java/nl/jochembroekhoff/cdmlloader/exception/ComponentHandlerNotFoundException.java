package nl.jochembroekhoff.cdmlloader.exception;

public class ComponentHandlerNotFoundException extends Exception {
    public ComponentHandlerNotFoundException(String m) {
        super("No component handler found for component " + m);
    }
}
