package server;
import Models.chat.Message;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread {
    private Socket socket;

    public ServerThread(Socket socket) throws IOException {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            DataInputStream dataInputStream = new DataInputStream(socket.getInputStream());
            DataOutputStream dataOutputStream = new DataOutputStream(socket.getOutputStream());

            while (true) {
                Message message = new Gson().fromJson(dataInputStream.readUTF(), Message.class);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void addMessage(Message message) {
        chatServer.chats.add(message);
    }
}