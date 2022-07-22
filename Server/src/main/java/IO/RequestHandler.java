package IO;

import Controllers.GameController;
import Controllers.MainMenuController;
import Models.Menu.Menu;
import Models.Player.Player;
import Models.User;
import enums.cheatCode;
import enums.registerEnum;
import server.GameRoom;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.Socket;
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

    public User getUser()
    {
        return this.user;
    }
    public Socket getSocket()
    {
        return this.socket;
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
        // handle lobby requests
        else if (request.getAction().equals("new room")) return createNewRoom(request);
        else if(request.getAction().equals("get join requests")) return getJoinRequests();
        else if(request.getAction().equals("accept join request")) return acceptJoinRequest(request);
        else if(request.getAction().equals("reject join request")) return rejectJoinRequest(request);
        else if(request.getAction().equals("join room")) return joinRoom(request);
        else if(request.getAction().equals("start game")) return startGame();

        else if(request.getAction().equals("move unit")) return moveUnit(request);
        else if(request.getAction().equals("cheat code")) return cheatCode(request);
        else if(request.getAction().equals("next turn")) return nextTurn();
        else if(request.getAction().equals("found city")) return foundCity(request);
        else if(request.getAction().equals("select CUnit")) return selectCUnit(request);
        else if(request.getAction().equals("select NCUnit")) return selectNCUnit(request);

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
    // lobby requests
    private Response createNewRoom(Request request)
    {
        Response response = new Response();

        String roomID = (String) request.getParams().get("roomID");
        if(MainMenuController.getRoomByRoomID(roomID) != null)
        {
            response.addMassage("this roomID is already taken");
            return response;
        }

        MainMenuController.addToGameRooms(new GameRoom(user, roomID));
        response.addMassage("room created successfully");
        return response;
    }
    private Response getJoinRequests()
    {
        GameRoom gameRoom = MainMenuController.getRoomByAdminUsername(user.getUsername());
        StringBuilder joinRequestsSB = new StringBuilder();
        for (int i = 0; i < gameRoom.getJoinRequests().size(); i++)
            joinRequestsSB.append(i + ": " + gameRoom.getJoinRequests().get(i).getUser().getUsername()).append("\n");

        Response response = new Response();
        response.addParam("join requests", joinRequestsSB.toString());
        return response;
    }
    private Response acceptJoinRequest(Request request)
    {
        GameRoom gameRoom = MainMenuController.getRoomByAdminUsername(user.getUsername());
        RequestHandler acceptedJoinRequest = gameRoom.getJoinRequests().get((Integer) request.getParams().get("index"));
        gameRoom.removeFromJoinedRequests(acceptedJoinRequest);
        gameRoom.addToJoinedClients(acceptedJoinRequest);

        Response response = new Response();
        response.addMassage("join request accepted");
        return response;
    }
    private Response rejectJoinRequest(Request request)
    {
        GameRoom gameRoom = MainMenuController.getRoomByAdminUsername(user.getUsername());
        RequestHandler rejectedJoinRequest = gameRoom.getJoinRequests().get((Integer) request.getParams().get("index"));
        gameRoom.removeFromJoinedRequests(rejectedJoinRequest);

        Response response = new Response();
        response.addMassage("join request rejected");

        return response;
    }
    private Response joinRoom(Request request)
    {
        String roomID = (String) request.getParams().get("roomID");
        GameRoom gameRoom = MainMenuController.getRoomByRoomID(roomID);

        Response response = new Response();

        if(gameRoom == null)
        {
            response.addMassage("there is no room with this roomID");
            return response;
        }

        gameRoom.addToJoinRequests(this);
        response.addMassage("request was sent");
        return response;
    }
    private Response startGame()
    {
        GameRoom gameRoom = MainMenuController.getRoomByAdminUsername(user.getUsername());

        for (int i = 0; i < gameRoom.getJoinedClients().size(); i++)
        {
            RequestHandler joinedClient = gameRoom.getJoinedClients().get(i);
//            Player player =
        }

        // TODO: send a request to each client to start the game

        Response response = new Response();
        response.addMassage("game started");
        return response;
    }

    private Response moveUnit(Request request)
    {
        int destinationX = (int) request.getParams().get("destinationX");
        int destinationY = (int) request.getParams().get("destinationY");

        // TODO: move combat unit in THE tile
        Matcher matcher = Pattern.compile("move CUnit (?<x>\\d+) (?<y>\\d+)").matcher("move CUnit " + destinationX + " " + destinationY);
        Response response = new Response();
        response.addMassage(GameController.getInstance().moveUnit(matcher));
        response.addParam("player", GameController.getInstance().playerToJson(GameController.getInstance().getPlayerTurn()));
        return response;
    }
    private Response cheatCode(Request request)
    {
        Response response = new Response();
        String cheatCodeDescription = (String) request.getParams().get("description");
        Matcher matcher;
        if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseTurns)) != null)
            response.addMassage(GameController.getInstance().increaseTurns(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseGold)) != null)
            response.addMassage(GameController.getInstance().increaseGold(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.killEnemyUnit)) != null)
            response.addMassage(GameController.getInstance().killEnemyUnit(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainFood)) != null)
            response.addMassage(GameController.getInstance().increaseFood(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainTechnology)) != null)
            response.addMassage(GameController.getInstance().addTechnology(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseHappiness)) != null)
            response.addMassage(GameController.getInstance().increaseHappiness(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseScore)) != null)
            response.addMassage(GameController.getInstance().increaseScore(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.increaseHealth)) != null)
            response.addMassage(GameController.getInstance().increaseHealth(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.winGame)) != null)
            response.addMassage(GameController.getInstance().winGame());
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.moveUnit)) != null)
            response.addMassage(GameController.getInstance().moveUnit(matcher));
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainBonusResource)) != null)
            response.addMassage(GameController.getInstance().gainBonusResourceCheat());
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainStrategicResource)) != null)
            response.addMassage(GameController.getInstance().gainStrategicResourceCheat());
        else if((matcher = cheatCode.compareRegex(cheatCodeDescription, cheatCode.gainLuxuryResource)) != null)
            response.addMassage(GameController.getInstance().gainLuxuryResourceCheat());
        response.addParam("player", GameController.getInstance().playerToJson(GameController.getInstance().getPlayerTurn()));

        return response;
    }
    private Response nextTurn()
    {
        GameController.getInstance().checkChangeTurn();

        Response response = new Response();
        response.addMassage("turn changed");
        response.addParam("player", GameController.getInstance().playerToJson(GameController.getInstance().getPlayerTurn()));
        return response;
    }
    private Response foundCity(Request request)
    {
        Response response = new Response();
        response.addMassage(GameController.getInstance().found());
        response.addParam("player", GameController.getInstance().playerToJson(GameController.getInstance().getPlayerTurn()));

        return response;
    }
    private Response selectCUnit(Request request)
    {
        int positionX = (int) request.getParams().get("positionX");
        int positionY = (int) request.getParams().get("positionY");
        GameController.getInstance().getPlayerTurn().setSelectedUnit(GameController.getInstance().getPlayerTurn().getTileByXY(positionX, positionY).getCombatUnitInTile());

        Response response = new Response();
        response.addMassage("CUnit selected");
        return response;
    }
    private Response selectNCUnit(Request request)
    {
        int positionX = (int) request.getParams().get("positionX");
        int positionY = (int) request.getParams().get("positionY");
        GameController.getInstance().getPlayerTurn().setSelectedUnit(GameController.getInstance().getPlayerTurn().getTileByXY(positionX, positionY).getNonCombatUnitInTile());

        Response response = new Response();
        response.addMassage("NCUnit selected");
        return response;
    }
}









