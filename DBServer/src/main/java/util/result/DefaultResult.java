package util.result;

import static util.result.ResultCode.BAD_REQUEST;

public class DefaultResult extends Result{
    public DefaultResult(String message) {
        super(BAD_REQUEST, message);
    }

    public DefaultResult() {
        super(BAD_REQUEST, null);
    }
}
