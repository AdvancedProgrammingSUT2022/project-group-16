import com.google.gson.Gson;

import java.util.HashMap;

public class Request {
    private String action;
    private HashMap<String, Object> params = new HashMap<>();

    public String getAction() {
        return action;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }

    public void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Request fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Request.class);
    }
}
