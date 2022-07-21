package IO;

import Models.User;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Client {
    private User loggedInUser;
    public static ArrayList<User> allUsers;
    static final int SERVER_PORT = 444;
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
        if(allUsers == null){
            Request request = new Request();
            request.setAction("get all users");
            Response response = sendRequest(request);
            allUsers = (ArrayList<User>) response.getParams().get("allUsers");
        }
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
        Response response = Client.getInstance().sendRequest(request);
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
}
