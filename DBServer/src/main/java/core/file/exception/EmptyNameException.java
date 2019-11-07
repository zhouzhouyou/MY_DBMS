package core.file.exception;

public class EmptyNameException extends Exception{
    public EmptyNameException(String message) {
        super("No " + message);
    }
}
