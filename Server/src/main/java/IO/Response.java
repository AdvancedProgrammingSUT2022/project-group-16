package IO;

import Models.Player.TileState;
import Models.Terrain.Tile;
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
    private ArrayList<User> users = new ArrayList<>();
    private HashMap<String, ArrayList<Message>> privateChats = new HashMap<>();
    private ArrayList<publicMessage> publicChats = new ArrayList<>();
    private HashMap<Tile, TileState> Map = new HashMap<>();
    private ArrayList<Tile> GameMap = new ArrayList<>();

    public HashMap<Tile, TileState> getMap() {
        return Map;
    }

    public void setMap(HashMap<Tile, TileState> map) {
        Map = map;
    }

    public ArrayList<publicMessage> getPublicChats() {
        return publicChats;
    }

    public void setPublicChats(ArrayList<publicMessage> publicChats) {
        this.publicChats = publicChats;
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
    public void addUser(User user){
        this.users.add(user);
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

    public void setPrivateChat(HashMap<String, ArrayList<Message>> privateChats) {
    }

    public HashMap<String, ArrayList<Message>> getPrivateChats() {
        return privateChats;
    }

    public void setGameMap(ArrayList<Tile> map) {
        this.GameMap = map;
    }

    public ArrayList<Tile> getGameMap() {
        return GameMap;
    }
}
