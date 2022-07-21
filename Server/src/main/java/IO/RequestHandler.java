package IO;

import Controllers.ProfileController;
import Models.Menu.Menu;
import Models.User;
import Models.chat.Message;
import Models.chat.publicMessage;
import enums.registerEnum;
import javafx.scene.image.ImageView;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;

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
        else if(request.getAction().equals("make new chat")) return makeNewChat(request);
        else if(request.getAction().equals("get user private chats")) return getUserPrivateChats((String) request.getParams().get("username"));
        else if(request.getAction().equals("seen message")) return seenMessage(request);
        else if(request.getAction().equals("edit message")) return editMessage(request);
        else if(request.getAction().equals("get user")) return getUser((String)request.getParams().get("username"));
        else if(request.getAction().equals("delete message for sender")) return deleteMessageForSender(request);
        else if(request.getAction().equals("delete message for all")) return deleteMessageForAll(request);
        else if(request.getAction().equals("send public message")) return sendPublicMessage(request);
        else if(request.getAction().equals("send message")) return sendMessage(request);
        else if(request.getAction().equals("change nickname")) return changeNickname((String)request.getParams().get("nickname"));
        else if(request.getAction().equals("change password")) return changePassword(request);
        else if(request.getAction().equals("change photo")) return changePhoto((URL)request.getParams().get("url"));

        return null;
    }

    private Response changePhoto(URL url) {
        Menu.loggedInUser.setPhoto(url);
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response changePassword(Request request) {
        String current = (String) request.getParams().get("current");
        String newPass = (String) request.getParams().get("new");
        ProfileController profileController = new ProfileController();
        String message = profileController.matchNewPassword(current, newPass);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }

    private Response changeNickname(String nickname) {
        ProfileController profileController = new ProfileController();
        String message = profileController.changeNickname(nickname);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }

    private Response sendMessage(Request request) {
        User receiver = (User) request.getParams().get("receiver");
        User sender = (User) request.getParams().get("sender");
        Message tmp1 = (Message) request.getParams().get("senderMessage");
        Message tmp2 = (Message) request.getParams().get("receiverMessage");
        sender.getPrivateChats().get(receiver.getUsername()).add(tmp1);
        receiver.getPrivateChats().get(sender.getUsername()).add(tmp2);
        Server.registerController.updateDatabase();
        return new Response();
    }

    private Response sendPublicMessage(Request request) {
        publicMessage publicMessage = (publicMessage) request.getParams().get("message");
        User sender = (User) request.getParams().get("sender");
        Server.chatServer.getPublicChats().add(publicMessage);
        Server.chatServer.writeData();
        return new Response();
    }

    private Response deleteMessageForAll(Request request) {
        User receiver = (User) request.getParams().get("receiver");
        User sender = (User) request.getParams().get("sender");
        int index = (int) request.getParams().get("index");
        sender.getPrivateChats().get(receiver.getUsername()).get(index).setMessage("#deleted");
        receiver.getPrivateChats().get(sender.getUsername()).get(index).setMessage("#deleted");
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response deleteMessageForSender(Request request) {
        User receiver = (User) request.getParams().get("receiver");
        User sender = (User) request.getParams().get("sender");
        int index = (int) request.getParams().get("index");
        sender.getPrivateChats().get(receiver.getUsername()).get(index).setMessage("#deleted");
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response getUser(String username) {
        Response response = new Response();
        response.addParam("user", Server.registerController.getUserByUsername(username));
        return response;
    }

    private Response editMessage(Request request) {
        User receiver = (User) request.getParams().get("receiver");
        User sender = (User) request.getParams().get("sender");
        String finalMessage = (String) request.getParams().get("message");
        int index = (int) request.getParams().get("index");
        sender.getPrivateChats().get(receiver).get(index).setMessage(finalMessage);
        receiver.getPrivateChats().get(sender.getUsername()).get(index).setMessage(finalMessage);
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response seenMessage(Request request){
        User receiver = (User) request.getParams().get("receiver");
        User sender = (User) request.getParams().get("sender");
        int j = receiver.getPrivateChats().get(sender.getUsername()).size() - 1;
        while (j >= 0) {
            String message = receiver.getPrivateChats().get(sender.getUsername()).get(j).getMessage();
            if(message.endsWith(" - d")) {
                receiver.getPrivateChats().get(sender.getUsername()).get(j).
                        setMessage(message.substring(0, message.length() - 4) + " - s");
                Server.registerController.writeDataOnJson();
            }
            j--;
        }
        return new Response();
    }

    private Response getUserPrivateChats(String username){
        Response response = new Response();
        response.addParam("chats",Server.registerController.getUserByUsername(username).getPrivateChats());
        return response;
    }

    private Response makeNewChat(Request request) {
        Response response = new Response();
        response.setStatus(400);
        User sender = (User) request.getParams().get("sender");
        User receiver = null;
        String username = (String) request.getParams().get("username");
        for(User user : Menu.allUsers) {
            if (user.getUsername().equals(username) &&
                    !sender.getUsername().equals(user.getUsername()) &&
                    !sender.getPrivateChats().containsKey(user.getUsername())) {
                sender.getPrivateChats().put(username, new ArrayList<>());
                user.getPrivateChats().put(sender.getUsername(), new ArrayList<>());
                response.setStatus(200);
                response.addParam("receiver", user);
                receiver = user;
                break;
            }
        }
        sender.getPrivateChats().put(receiver.getUsername(), new ArrayList<>());
        receiver.getPrivateChats().put(sender.getUsername(), new ArrayList<>());
        return response;
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
