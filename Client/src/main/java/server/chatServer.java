package server;

import IO.Client;
import IO.Request;
import IO.Response;
import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
import com.google.gson.Gson;
import enums.chatEnum;
import enums.registerEnum;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chatServer {
    private ArrayList<User> onlineUsers = new ArrayList<>();
    public static ArrayList<publicMessage> publicChats = new ArrayList<>();


    public void update() {
        Request request = new Request();
        request.setAction("update public chats");
        Response response = Client.getInstance().sendRequest(request);
        publicChats = (ArrayList<publicMessage>) response.getParams().get("chats");
    }//update arrayList with Json database

    public void writeData() {
        try {
            Writer writer = new FileWriter(chatEnum.filePath.regex);
            new Gson().toJson(publicChats, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//update Json database with arrayList

    public ArrayList<publicMessage> getPublicChats() {
        update();
        return publicChats;
    }
}
