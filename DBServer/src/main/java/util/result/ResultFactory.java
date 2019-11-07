package util.result;

public class ResultFactory {
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;


    public static Result buildFailResult(Object data) {
        return new Result(BAD_REQUEST, data);
    }

    public static Result buildSuccessResult(Object data) {
        return new Result(SUCCESS, data);
    }
}
