package util.result;

import com.google.gson.Gson;

public class Request {
    public String databaseName;
    public String sql;

    public Request(String databaseName, String sql) {
        this.databaseName = databaseName;
        this.sql = sql;
    }

    public static Request fromGson(String string) {
        Gson gson = new Gson();
        return gson.fromJson(string, Request.class);
    }
}
