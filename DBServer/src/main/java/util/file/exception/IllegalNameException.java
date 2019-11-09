package util.file.exception;

public class IllegalNameException extends Exception{
    public IllegalNameException(String message) {
        super("illegal name: " + message);
    }
}
