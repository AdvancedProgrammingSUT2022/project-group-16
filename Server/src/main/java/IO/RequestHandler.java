package IO;

import Models.Menu.Menu;
import Models.User;
import enums.registerEnum;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;

public class RequestHandler  extends Thread{
    private User user;
    private Socket socket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;

    public RequestHandler(Socket socket) throws IOException {
        this.socket = socket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
    }

    @Override
    public void run() {
        super.run();
        try {
            while (true) {
                Request request = Request.fromJson(inputStream.readUTF());
                Response response = handleRequest(request);
                outputStream.writeUTF(response.toJson());
                outputStream.flush();
            }
        }catch (EOFException e){
            System.out.println("client disconnected");
        }
        catch (IOException ignored) {
            ignored.printStackTrace();
        }
    }

    private Response handleRequest(Request request){
        if(request.getAction().equals("update public chats")) return updatePublicChats();
        else if(request.getAction().equals("login")) return login(request);
        else if(request.getAction().equals("register")) return register(request);
        else if(request.getAction().equals("logout")) return logout();
        else if(request.getAction().equals("get all users")) return getAllUsers();
        return null;
    }

    private Response getAllUsers() {
        Response response = new Response();
        response.addParam("allUsers",Menu.allUsers);
        return response;
    }

    private Response logout() {
        Server.chatServer.removeOnlineUser(this.user);
        return new Response();
    }

    private Response register(Request request) {
        Response response = new Response();
        String username = (String) request.getParams().get("username");
        String password = (String) request.getParams().get("password");
        String nickname = (String) request.getParams().get("nickname");
        String message = Server.registerController.createUser(username, password, nickname, Server.registerController.guestImage);
        response.addMassage(message);
        if(response.getMassage().equals(registerEnum.successfulCreate.regex)){
            this.user = Server.registerController.getUserByUsername(username);
            response.addParam("user", Server.registerController.getUserByUsername(username));
            addOnlineUser(username);
        }else{
            response.setStatus(400);
        }
        return response;
    }

    private Response login(Request request) {
        Response response = new Response();
        String username = (String) request.getParams().get("username");
        String password = (String) request.getParams().get("password");
        if(Server.registerController.getUserByUsername(username) == null) response.setStatus(401);
        else if(Server.registerController.isPasswordCorrect(username, password)) response.setStatus(402);
        else{
            this.user = Server.registerController.getUserByUsername(username);
            response.addParam("user", Server.registerController.getUserByUsername(username));
            addOnlineUser(username);
        }
        return response;
    }

    private Response updatePublicChats() {
        Response response = new Response();
        response.addParam("chats", Server.chatServer.getPublicChats());
        return response;
    }
    public void addOnlineUser(String username){
        LocalDateTime now = LocalDateTime.now();
        Server.registerController.getUserByUsername(username).setLastLogin(Server.timeAndDate.format(now));
        Server.chatServer.addOnlineUser(Server.registerController.getUserByUsername(username), socket);
        Server.registerController.writeDataOnJson();
    }
}
