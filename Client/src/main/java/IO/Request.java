package IO;

import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class Request {
    private String action;
    private HashMap<String, Object> params = new HashMap<>();
    private ArrayList<publicMessage> publicMessages = new ArrayList<>();
    private ArrayList<Message> privateMessages = new ArrayList<>();


    public ArrayList<publicMessage> getPublicMessages() {
        return publicMessages;
    }

    public ArrayList<Message> getPrivateMessages() {
        return privateMessages;
    }

    public void addPrivateMessages(Message message) {
        this.privateMessages.add(message);
    }
    public void addPublicMessages(publicMessage message) {
        this.publicMessages.add(message);
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action){
        this.action = action;
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
