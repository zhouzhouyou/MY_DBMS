package util.result;


import com.google.gson.Gson;

public class Result {
    public static final int SUCCESS = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHORIZED = 401;
    public static final int NOT_FOUND = 404;
    public static final int CONFLICT = 409;

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
