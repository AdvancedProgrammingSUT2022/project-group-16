package Models.chat;

import java.util.HashMap;

public class Request {
    private String action;
    private HashMap<String, Object> params = new HashMap<>();

    public Request(String action) {
        this.action = action;
    }

    public String getAction() {
        return action;
    }

    public HashMap<String, Object> getParams() {
        return params;
    }
}
