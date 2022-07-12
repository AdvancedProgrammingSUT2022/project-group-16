package server;
import Models.Menu.Menu;
import Models.User;
import Models.chat.Request;
import Models.chat.Respond;
import com.google.gson.Gson;
import jdk.jfr.internal.tool.Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {
    private final Socket socket;

    public ServerThread(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());
            boolean isRunning = true;

            while (isRunning) {
                String request = dataInputStream.readUTF();
//                switch (request) {
//                    case "loginUser":
//                        chatServer.onlineUsers.add(Menu.loggedInUser);
//                        for(User user : chatServer.onlineUsers)
//                            System.out.println(user.getUsername());
//                        send(dataOutputStream, "hey");
//                        break;
//                    case "exit":
//                        isRunning = false;
//                        break;
            }
            dataInputStream.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void send(DataOutputStream dataOutputStream, String respond) throws IOException {
        dataOutputStream.writeUTF(respond);
        dataOutputStream.flush();
    }

    private Respond respond(String message) {
        return new Respond(message);
    }
}