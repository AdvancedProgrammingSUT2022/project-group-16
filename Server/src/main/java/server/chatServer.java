package server;
import Models.Menu.Menu;
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
import java.util.HashMap;

public class chatServer {
    private HashMap<User, Socket> onlineUsers = new HashMap<>();
    public static ArrayList<publicMessage> publicChats = new ArrayList<>();

    public void update()
    {
        try {
            String arr = (new BufferedReader(new FileReader(chatEnum.filePath.regex))).readLine();
            if(arr != null)
            {
                arr = arr.substring(1,arr.length() - 1);
                String regex = "},";
                String[] splitedArr = arr.split(regex);
                for(int i = 0; i < splitedArr.length; i++)
                {
                    if(i != splitedArr.length - 1)
                        splitedArr[i] += "}";
                    publicChats.add(new Gson().fromJson(splitedArr[i], publicMessage.class));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//update arrayList with Json database

    public void writeData()
    {
        try {
            Writer writer = new FileWriter(chatEnum.filePath.regex);
            new Gson().toJson(publicChats, writer);
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }//update Json database with arrayList

    public ArrayList<publicMessage> getPublicChats() {
        return publicChats;
    }

    public HashMap<User, Socket> getOnlineUsers() {
        return onlineUsers;
    }

    public void addOnlineUser(User user, Socket socket) {
        this.onlineUsers.put(user, socket);
    }

    public void removeOnlineUser(User user) {
        this.onlineUsers.remove(user);
    }
}
