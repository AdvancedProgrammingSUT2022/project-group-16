package IO;

import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private User loggedInUser;
    public static ArrayList<User> allUsers = new ArrayList<>();
    static final int SERVER_PORT = 8569;
    static DataInputStream dataInputStream;
    static DataOutputStream dataOutputStream;
    public static Socket socket;
    public static Socket listenerSocket;
    static Client client;
    private Client(){
        try {
            socket = new Socket("localhost",SERVER_PORT);
            listenerSocket = new Socket("localhost", SERVER_PORT);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());
            dataInputStream = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static Client getInstance(){
        if(client == null) client = new Client();
        return client;
    }

    public  static User getUserByUsername(String username){
        for (User user : Client.getInstance().getAllUsers()) {
            if(user.getUsername().equals(username)) return user;
        }
        return null;
    }
    public  User getLoggedInUser() {
        updateLoggedInUser();
        return loggedInUser;
    }


    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public void updateLoggedInUser(){
        this.loggedInUser = getUser(loggedInUser.getUsername()).getUsers().get(0);
    }

    public ArrayList<User> getAllUsers(){
        allUsers.clear();
        Request request = new Request();
        request.setAction("get all users");
        Response response = sendRequest(request);
        allUsers = response.getUsers();
        return allUsers;
    }

    public Response sendRequest(Request request){
        try {
            dataOutputStream.writeUTF(request.toJson());
            dataOutputStream.flush();
            Response response = Response.fromJson(dataInputStream.readUTF());
            return response;
        }catch (IOException e){
            System.out.println("server is disconnected");
            System.exit(-1);
        }
        return null;
    }

    public void logout(){
        Request request = new Request();
        request.setAction("logout");
        sendRequest(request);
        this.loggedInUser = null;
    }
    public Response checkLogin(String username, String password){
        Request request = new Request();
        request.setAction("login");
        request.addParam("username", username);
        request.addParam("password", password);
        Response response = sendRequest(request);
        return response;
    }
    public Response checkRegister(String username, String password, String nickname){
        Request request = new Request();
        request.setAction("register");
        request.addParam("username", username);
        request.addParam("nickname", nickname);
        request.addParam("password", password);
        Response response = Client.getInstance().sendRequest(request);
        return response;
    }

    public Response makeNewChat(String username, User sender) {
        Request request = new Request();
        request.setAction("make new chat");
        request.addParam("username", username);
        request.addParam("sender", sender);
        return sendRequest(request);
    }
    public HashMap<String, ArrayList<Message>> getUserPrivateChats(String username){
        Request request = new Request();
        request.setAction("get user private chats");
        request.addParam("username", username);
        Response response = sendRequest(request);
        return response.getUsers().get(0).getPrivateChats();
    }

    public Response seenMessage(User sender, User receiver) {
        Request request = new Request();
        request.setAction("seen message");
        request.addParam("sender", sender.getUsername());
        request.addParam("receiver", receiver.getUsername());
        return sendRequest(request);
    }

    public Response editMessage(User sender, User receiver, String finalMessage, int flg) {
        Request request = new Request();
        request.setAction("edit message");
        request.addParam("sender", sender.getUsername());
        request.addParam("receiver", receiver.getUsername());
        request.addParam("message", finalMessage);
        request.addParam("index", flg);
        return sendRequest(request);
    }

    public Response getUser(String username) {
        Request request = new Request();
        request.setAction("get user");
        request.addParam("username", username);
        return sendRequest(request);
    }

    public Response deleteMessageForSender(User sender , User receiver, int flag) {
        Request request = new Request();
        request.setAction("delete message for sender");
        request.addParam("sender", sender.getUsername());
        request.addParam("receiver", receiver.getUsername());
        request.addParam("index", flag);
        return sendRequest(request);
    }
    public Response deleteMessageForAll(User sender ,User receiver, int flag) {
        Request request = new Request();
        request.setAction("delete message for all");
        request.addParam("sender", sender.getUsername());
        request.addParam("receiver", receiver.getUsername());
        request.addParam("index", flag);
        return sendRequest(request);
    }

    public Response sendPublicMessage(User sender, String message) {
        publicMessage tmp = new publicMessage(sender.getUsername(), message);
        Request request = new Request();
        request.setAction("send public message");
        request.addParam("sender", sender.getUsername());
        request.addPublicMessages(tmp);
        return sendRequest(request);
    }

    public Response sendMessage(User sender, User receiver, String message) {
        Message senderMessage = new Message(sender.getUsername(), receiver.getUsername(), message + " - d");
        Message receiverMessage = new Message(sender.getUsername(), receiver.getUsername(), message);

        Request request = new Request();
        request.setAction("send message");
        request.addParam("sender", sender.getUsername());
        request.addParam("receiver", receiver.getUsername());
        request.addPrivateMessages(senderMessage);
        request.addPrivateMessages(receiverMessage);
        return sendRequest(request);
    }

    public Response changeNickname(String nickname) {
        Request request = new Request();
        request.setAction("change nickname");
        request.addParam("nickname", nickname);
        return sendRequest(request);
    }

    public Response changePassword(String currentPass, String newPass) {
        Request request = new Request();
        request.setAction("change password");
        request.addParam("current", currentPass);
        request.addParam("new", newPass);
        return sendRequest(request);
    }

    public Response setPhoto(URL url) {
        Request request = new Request();
        request.setAction("change photo");
        request.addParam("url", url);
        return sendRequest(request);
    }

}
