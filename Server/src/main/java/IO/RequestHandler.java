package IO;

import Controllers.GameController;
import Models.Menu.Menu;
import Models.User;
import enums.cheatCode;
import enums.registerEnum;
import server.chatServer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
        if(!GameController.getInstance().getPlayerTurn().getUsername().equals(user.getUsername())) return notYourTurn();
        else if(request.getAction().equals("update public chats")) return updatePublicChats();
        else if(request.getAction().equals("login")) return login(request);
        else if(request.getAction().equals("register")) return register(request);
        else if(request.getAction().equals("logout")) return logout();
        else if(request.getAction().equals("get all users")) return getAllUsers();
        else if(request.getAction().equals("move CUnit")) return moveCUnit(request);
        else if(request.getAction().equals("move NCUnit")) return moveNCUnit(request);
        else if(request.getAction().equals("cheat code")) return cheatCode(request);
        else if(request.getAction().equals("next turn")) return nextTurn();
        else if(request.getAction().equals(""))
        return null;
    }

    private Response notYourTurn()
    {
        Response response = new Response();
        response.addMassage("not your turn");
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

    private Response moveCUnit(Request request)
    {
        int originX = (int) request.getParams().get("originX");
        int originY = (int) request.getParams().get("originY");
        int destinationX = (int) request.getParams().get("destinationX");
        int destinationY = (int) request.getParams().get("destinationY");

        // TODO: move combat unit in THE tile
    }
    private Response moveNCUnit(Request request)
    {
        int originX = (int) request.getParams().get("originX");
        int originY = (int) request.getParams().get("originY");
        int destinationX = (int) request.getParams().get("destinationX");
        int destinationY = (int) request.getParams().get("destinationY");

        // TODO: move non-combat unit in THE tile
    }
    private Response cheatCode(Request request)
    {
        String cheatCodeDescription = (String) request.getParams().get("description");
        Matcher matcher;
        if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseTurns)) != null)
            GameController.getInstance().increaseTurns(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseGold)) != null)
            GameController.getInstance().increaseGold(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.killEnemyUnit)) != null)
            GameController.getInstance().killEnemyUnit(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainFood)) != null)
            GameController.getInstance().increaseFood(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainTechnology)) != null)
            GameController.getInstance().addTechnology(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseHappiness)) != null)
            GameController.getInstance().increaseHappiness(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseScore)) != null)
            GameController.getInstance().increaseScore(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseHealth)) != null)
            GameController.getInstance().increaseHealth(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.winGame)) != null)
            GameController.getInstance().winGame();
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.moveUnit)) != null)
            GameController.getInstance().moveUnit(matcher);
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainBonusResource)) != null)
            GameController.getInstance().gainBonusResourceCheat();
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainStrategicResource)) != null)
            GameController.getInstance().gainStrategicResourceCheat();
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainLuxuryResource)) != null)
            GameController.getInstance().gainLuxuryResourceCheat();


    }
    private Response nextTurn()
    {
        GameController.getInstance().checkChangeTurn();

        Response response = new Response();
        response.addMassage("turn changed");

        return response;
    }
}









