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

    public static Result buildObjectAlreadyExistsResult() {
        return buildFailResult("object already exists");
    }

    public static Result buildObjectAlreadyExistsResult(String object) {
        return buildFailResult(object + " already exists");
    }

    public static Result buildInvalidNameResult(String name) {
        return buildFailResult("name: " + name + " is invalid");
    }

    public static Result buildInvalidValueConvertResult(String targetType, String value) {
        return buildFailResult("can't convert " + value + " to " + targetType);
    }

    public static Result buildObjectNotExistsResult() {
        return new Result(NOT_FOUND, "object not exists");
    }

    public static Result buildObjectNotExistsResult(String object) {
        return new Result(NOT_FOUND, object + " not exists");
    }

    public static Result buildObjectOccupiedResult() {
        return new Result(CONFLICT, "object occupied");
    }

    public static Result buildUnauthorizedResult() {
        return new Result(UNAUTHORIZED, null);
    }

    public static Result buildUnauthorizedResult(String msg) {
        return new Result(UNAUTHORIZED, msg);
    }
}
