package IO;

import com.google.gson.Gson;

import java.util.HashMap;

public class Response {
    private int status = 200;
    private String massage;
    private HashMap<String, Object> params = new HashMap<>();



    public HashMap<String, Object> getParams() {
        return params;
    }

    public void addParam(String key, Object value) {
        this.params.put(key, value);
    }

    public int getStatus() {
        return status;
    }

    public String getMassage() {
        return massage;
    }

    public void addMassage(String massage) {
        this.massage = massage;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    public static Response fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, Response.class);
    }

    public void setStatus(int status) {
        this.status = status;
    }

}
