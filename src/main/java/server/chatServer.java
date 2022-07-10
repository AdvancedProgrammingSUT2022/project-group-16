package server;

import Models.chat.Message;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class chatServer {
    public static ArrayList<Message> chats = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        ServerSocket serverSocket = new ServerSocket(6666);
        while (true) {
            Socket socket = serverSocket.accept();
            ServerThread myThread = new ServerThread(socket);
            myThread.start();
        }



    }
}
