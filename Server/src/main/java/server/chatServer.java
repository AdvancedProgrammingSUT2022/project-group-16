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

public class chatServer {
    private ArrayList<User> onlineUsers = new ArrayList<>();
    public static ArrayList<publicMessage> publicChats = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6665);
        while (true)
        {
            Socket socket = serverSocket.accept();
            ServerThread myThread = new ServerThread(socket);
            myThread.start();
        }
    }
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
}
