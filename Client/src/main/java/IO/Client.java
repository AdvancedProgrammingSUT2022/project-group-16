package IO;

import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class Client {
    private User loggedInUser;
    public static ArrayList<User> allUsers;
    static final int SERVER_PORT = 1111;
    static DataInputStream dataInputStream;
    static DataOutputStream dataOutputStream;
    static Socket socket;
    static Client client;
    private Client(){
        try {
            socket = new Socket("localhost",SERVER_PORT);
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

    public  User getLoggedInUser() {
        return loggedInUser;
    }

    public void setLoggedInUser(User loggedInUser) {
        this.loggedInUser = loggedInUser;
    }

    public ArrayList<User> getAllUsers(){
        Request request = new Request();
        request.setAction("get all users");
        Response response = sendRequest(request);
        allUsers = (ArrayList<User>) response.getParams().get("allUsers");
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
        HashMap<String, ArrayList<Message>> chats = new HashMap<>();
        ArrayList<String> keys = (ArrayList<String>) response.getParams().get("keys");
        ArrayList<ArrayList<Message>> values = (ArrayList<ArrayList<Message>>) response.getParams().get("values");
        for(int i = 0 ; i < keys.size(); i++){
            chats.put(keys.get(i), values.get(i));
        }
        return chats;
    }

    public Response seenMessage(User sender, User receiver) {
        Request request = new Request();
        request.setAction("seen message");
        request.addParam("sender", sender);
        request.addParam("receiver", receiver);
        return sendRequest(request);
    }

    public Response editMessage(User sender, User receiver, String finalMessage, int flg) {
        Request request = new Request();
        request.setAction("edit message");
        request.addParam("sender", sender);
        request.addParam("receiver", receiver);
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
        request.addParam("sender", sender);
        request.addParam("receiver", receiver);
        request.addParam("index", flag);
        return sendRequest(request);
    }
    public Response deleteMessageForAll(User sender ,User receiver, int flag) {
        Request request = new Request();
        request.setAction("delete message for all");
        request.addParam("sender", sender);
        request.addParam("receiver", receiver);
        request.addParam("index", flag);
        return sendRequest(request);
    }

    public Response sendPublicMessage(User sender, String message) {
        publicMessage tmp = new publicMessage(sender.getUsername(), message);
        Request request = new Request();
        request.setAction("send public message");
        request.addParam("sender", sender);
        request.addParam("message", tmp);
        return sendRequest(request);
    }

    public Response sendMessage(User sender, User receiver, String message) {
        Message tmp1 = new Message(sender.getUsername(), receiver.getUsername(), message + " - d");
        Message tmp2 = new Message(sender.getUsername(), receiver.getUsername(), message);

        Request request = new Request();
        request.setAction("send message");
        request.addParam("sender", sender);
        request.addParam("receiver", receiver);
        request.addParam("senderMessage", tmp1);
        request.addParam("receiverMessage", tmp2);
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

    public User parseUser(Response response) throws MalformedURLException {
        String username = (String) response.getParams().get("username");
        String password = (String) response.getParams().get("password");
        String nickname = (String) response.getParams().get("nickname");
        URL photo = new URL((String) response.getParams().get("photo"));
        long lastTimeOfWin = (long) Math.floor((double)response.getParams().get("lastTimeOfWin"));
        String lastLogin = (String) response.getParams().get("lastLogin");
        int score = (int) Math.floor((Double) response.getParams().get("score"));
        User user = new User(username, nickname, password, photo);
        user.setLastTimeOfWin( lastTimeOfWin);
        user.setLastLogin(lastLogin);
        user.setScore(score);
        HashMap<String, ArrayList<Message>> chats = new HashMap<>();
        ArrayList<String> keys = (ArrayList<String>) response.getParams().get("keys");
        ArrayList<ArrayList<Message>> values = (ArrayList<ArrayList<Message>>) response.getParams().get("values");
        for(int i = 0 ; i < keys.size(); i++){
            chats.put(keys.get(i), values.get(i));
        }
        user.setPrivateChats(chats);
        return user;
    }
}
