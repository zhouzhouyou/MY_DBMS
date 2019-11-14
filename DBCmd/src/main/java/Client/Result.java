package Client;

import com.google.gson.Gson;

public class Result {
    static final int SUCCESS = 200;
    static final int BAD_REQUEST = 400;
    static final int UNAUTHORIZED = 401;
    static final int NOT_FOUND = 404;
    static final int CONFLICT = 409;

    public int code;
    public Object data;

    public Result(int code, Object data) {
        this.code = code;
        this.data = data;
    }

    public static Result formGson(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, Result.class);
    }
}
