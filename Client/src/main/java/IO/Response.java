package IO;

import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashMap;

public class Response {
    private int status = 200;
    private String massage;
    private HashMap<String, Object> params = new HashMap<>();
    private ArrayList<User> users;
    private HashMap<String, ArrayList<Message>> privateChats = new HashMap<>();
    private ArrayList<publicMessage> publicChats = new ArrayList<>();

    public ArrayList<publicMessage> getPublicChats() {
        return publicChats;
    }

    public void setPublicChats(ArrayList<publicMessage> publicChats) {
        this.publicChats = publicChats;
    }

    public HashMap<String, ArrayList<Message>> getPrivateChats() {
        return privateChats;
    }

    public void setPrivateChats(HashMap<String, ArrayList<Message>> privateChats) {
        this.privateChats = privateChats;
    }

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

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
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
