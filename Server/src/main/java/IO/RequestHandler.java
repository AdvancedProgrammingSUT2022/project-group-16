package IO;

import Controllers.GameController;
import Controllers.ProfileController;
import Controllers.MainMenuController;
import Models.City.BuildingType;
import Models.Menu.Menu;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.User;
import com.google.gson.Gson;
import enums.cheatCode;
import Models.chat.Message;
import Models.chat.publicMessage;
import enums.gameCommands.selectCommands;
import enums.gameCommands.unitCommands;
import enums.gameEnum;
import enums.registerEnum;
import server.GameRoom;
import server.chatServer;

import java.io.*;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URL;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RequestHandler  extends Thread{
    private User user;
    private Socket socket;
    private Socket listenerSocket;
    private DataInputStream inputStream;
    private DataOutputStream outputStream;
    private DataInputStream listenerSocketDIS;
    private DataOutputStream listenerSocketDOS;
    private GameRoom gameRoom;

    public RequestHandler(Socket socket, Socket listenerSocket) throws IOException {
        this.socket = socket;
        this.listenerSocket = listenerSocket;
        this.inputStream = new DataInputStream(socket.getInputStream());
        this.outputStream = new DataOutputStream(socket.getOutputStream());
        this.listenerSocketDIS = new DataInputStream(listenerSocket.getInputStream());
        this.listenerSocketDOS = new DataOutputStream(listenerSocket.getOutputStream());
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

    private Response handleRequest(Request request) throws MalformedURLException {
        if(GameController.getInstance().getPlayerTurn() != null && !GameController.getInstance().getPlayerTurn().getUsername().equals(user.getUsername())) return notYourTurn();
        if(request.getAction().equals("update public chats")) return updatePublicChats();
        else if(request.getAction().equals("login")) return login(request);
        else if(request.getAction().equals("register")) return register(request);
        else if(request.getAction().equals("logout")) return logout();
        else if(request.getAction().equals("get all users")) return getAllUsers();

        else if(request.getAction().equals("user online")) return isOnline(request);
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
        else if(request.getAction().equals("change photo")) return changePhoto(new URL((String) request.getParams().get("url")));

            // handle lobby requests
        else if (request.getAction().equals("new room")) return createNewRoom(request);
        else if(request.getAction().equals("get join requests")) return getJoinRequests();
        else if(request.getAction().equals("get joined clients")) return getJoinedClients();
        else if(request.getAction().equals("accept join request")) return acceptJoinRequest(request);
        else if(request.getAction().equals("reject join request")) return rejectJoinRequest(request);
        else if(request.getAction().equals("public rooms")) return publicRooms(request);
        else if(request.getAction().equals("join room")) return joinRoom(request);
        else if(request.getAction().equals("find room")) return findRoom(request);
        else if(request.getAction().equals("start game")) return startGame();

        else if(request.getAction().equals("move unit")) return moveUnit(request);
        else if(request.getAction().equals("cheat code")) return cheatCode(request);
        else if(request.getAction().equals("next turn")) return nextTurn();
        else if(request.getAction().equals("found city")) return foundCity(request);
        else if(request.getAction().equals("select CUnit")) return selectCUnit(request);
        else if(request.getAction().equals("select NCUnit")) return selectNCUnit(request);
        else if(request.getAction().equals("checkChangeTurn")) return checkChangeTurn();
        else if(request.getAction().equals("getMap")) return getMap(request);
        else if(request.getAction().equals("getGameMap")) return getGameMap();
        else if(request.getAction().equals("getYear")) return getYear();
        else if(request.getAction().equals("showResearch")) return showResearch();
        else if(request.getAction().equals("sleep")) return sleep();
        else if(request.getAction().equals("stayAlert")) return stayAlert();
        else if(request.getAction().equals("alert")) return alert();
        else if(request.getAction().equals("fortify")) return fortify();
        else if(request.getAction().equals("fortifyTillHeal")) return fortifyTillHeal();
        else if(request.getAction().equals("garrison")) return garrison();
        else if(request.getAction().equals("setUp")) return setUp(request);
        else if(request.getAction().equals("pillage")) return pillage();
        else if(request.getAction().equals("destroyCity")) return destroyCity(request);
        else if(request.getAction().equals("attachCity")) return attachCity(request);
        else if(request.getAction().equals("attackCity")) return attackCity(request);
        else if(request.getAction().equals("found")) return found();
        else if(request.getAction().equals("cancel")) return cancel();
        else if(request.getAction().equals("wake")) return wake();
        else if(request.getAction().equals("delete")) return delete();
        else if(request.getAction().equals("road")) return road();
        else if(request.getAction().equals("railRoad")) return railRoad();
        else if(request.getAction().equals("farm")) return farm();
        else if(request.getAction().equals("mine")) return mine();
        else if(request.getAction().equals("tradingPost")) return tradingPost();
        else if(request.getAction().equals("lumberMill")) return lumberMill();
        else if(request.getAction().equals("pasture")) return pasture();
        else if(request.getAction().equals("plantation")) return plantation();
        else if(request.getAction().equals("camp")) return camp();
        else if(request.getAction().equals("quarry")) return quarry();
        else if(request.getAction().equals("factory")) return factory();
        else if(request.getAction().equals("removeFeature")) return removeFeature();
        else if(request.getAction().equals("removeRoute")) return removeRoute();
        else if(request.getAction().equals("repair")) return repair();
        else if(request.getAction().equals("buildUnit")) return buildUnit(request);
        else if(request.getAction().equals("buildBuilding")) return buildBuilding(request);
        else if(request.getAction().equals("buyUnit")) return buyUnit(request);
        else if(request.getAction().equals("buyTile")) return buyTile(request);
        else if(request.getAction().equals("lockCitizenToTile")) return lockCitizenToTile(request);
        else if(request.getAction().equals("unlockCitizenToTile")) return unlockCitizenToTile(request);
        else if(request.getAction().equals("isGameEnd")) return isGameEnd();



        //friendShip
        else if (request.getAction().equals("search username")) return searchFriends(request);
        else if (request.getAction().equals("send friendShip")) return sendFriendInv(request);
        else if (request.getAction().equals("friend requests")) return friendRequests(request);
        else if ((request.getAction().equals("accept friend"))) return acceptFriend(request);
        else if ((request.getAction().equals("reject friend"))) return rejectFriend(request);

        return null;
    }
    private Response checkChangeTurn()
    {
        Response response = new Response();
        String message = GameController.getInstance().checkChangeTurn();
        if(message == null){
            response.addMassage("turn changed successfully");
        }else{
            response.addMassage(message);
            response.setStatus(400);
        }

        return response;
    }

    private Response getMap(Request request){
        Response response = new Response();
        String username = (String) request.getParams().get("username");
        for (Player player : GameController.getInstance().getPlayers()) {
            if(player.getUsername().equals(username)){
                response.setMap(player.getMap());
                return response;
            }
        }
        return null;
    }

    private Response getGameMap(){
        Response response = new Response();
        response.setGameMap(GameController.getInstance().getMap());
        return response;
    }

    private Response getYear(){
        Response response = new Response();
        response.addParam("year", GameController.getInstance().getYear());
        return response;
    }

    private Response showResearch(){
        Response response = new Response();
        response.addParam("research", GameController.getInstance().showResearch());
        return response;
    }


    private Response sleep(){
        String message = GameController.getInstance().sleep();
        Response response = new Response();
        if(!message.equals(gameEnum.slept.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response stayAlert(){
        String message = GameController.getInstance().stayAlert();
        Response response = new Response();
        if(!message.equals(unitCommands.activeUnit.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response alert(){
        String message = GameController.getInstance().alert();
        Response response = new Response();
        if(!message.equals(unitCommands.alerted.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response fortify(){
        String message = GameController.getInstance().fortify();
        Response response = new Response();
        if(!message.equals(unitCommands.fortifyActivated)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response fortifyTillHeal(){
        String message = GameController.getInstance().fortifyTilHeal();
        Response response = new Response();
        if(!message.equals(unitCommands.fortifyHealActivated.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response garrison(){
        String message = GameController.getInstance().garrison();
        Response response = new Response();
        if(!message.equals(unitCommands.garissonSet.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response setUp(Request request){
        int x = (int) Math.floor((Double) request.getParams().get("x"));
        int y = (int) Math.floor((Double) request.getParams().get("y"));
        String message = GameController.getInstance().setup(x,y);
        Response response = new Response();
        if(!message.equals(unitCommands.setupSuccessful.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response pillage(){
        String message = GameController.getInstance().pillage();
        Response response = new Response();
        if(!message.equals(unitCommands.destroyImprovement.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response destroyCity(Request request){
        String name = (String) request.getParams().get("city name");
        String message = GameController.getInstance().destroyCity(GameController.getInstance().getCityByName(name)); ;
        Response response = new Response();
        if(!message.equals(unitCommands.destroyCity.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response attachCity(Request request){
        int x = (int) Math.floor((Double) request.getParams().get("x"));
        int y = (int) Math.floor((Double) request.getParams().get("y"));
        String message = GameController.getInstance().attackCity(x, y);
        Response response = new Response();
        if(!message.equals(unitCommands.destroyCity.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response attackCity(Request request){
        String name = (String) request.getParams().get("city name");
        String message = GameController.getInstance().attachCity(GameController.getInstance().getCityByName(name)); ;
        Response response = new Response();
        response.addMassage(message);
        return response;
    }

    private Response found(){
        String message = GameController.getInstance().found();
        Response response = new Response();
        if(!message.equals(unitCommands.cityBuilt.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response cancel(){
        String message = GameController.getInstance().cancel();
        Response response = new Response();
        if(!message.equals(unitCommands.cancelCommand.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response wake(){
        String message = GameController.getInstance().wake();
        Response response = new Response();
        if(!message.equals(gameEnum.wokeUp.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response delete(){
        String message = GameController.getInstance().delete();
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response road(){
        String message = GameController.getInstance().road();
        Response response = new Response();
        if(!message.equals(unitCommands.roadBuilt.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response railRoad(){
        String message = GameController.getInstance().railRoad();
        Response response = new Response();
        if(!message.equals(unitCommands.railRoadBuilt.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response farm(){
        String message = GameController.getInstance().railRoad();
        Response response = new Response();
        if(!message.equals(unitCommands.farmBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response mine(){
        String message = GameController.getInstance().mine();
        Response response = new Response();
        if(!message.equals(unitCommands.mineBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response tradingPost(){
        String message = GameController.getInstance().tradingPost();
        Response response = new Response();
        if(!message.equals(unitCommands.tradingPostBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }

    private Response lumberMill(){
        String message = GameController.getInstance().lumberMill();
        Response response = new Response();
        if(!message.equals(unitCommands.lumberMillBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response pasture(){
        String message = GameController.getInstance().pasture();
        Response response = new Response();
        if(!message.equals(unitCommands.pastureBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response camp(){
        String message = GameController.getInstance().camp();
        Response response = new Response();
        if(!message.equals(unitCommands.campBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response plantation(){
        String message = GameController.getInstance().plantation();
        Response response = new Response();
        if(!message.equals(unitCommands.plantationBuild.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response quarry(){
        String message = GameController.getInstance().quarry();
        Response response = new Response();
        if(!message.equals("quarry built successfully")) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response factory(){
        String message = GameController.getInstance().factory();
        Response response = new Response();
        if(!message.equals("factory built successfully")) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response removeFeature(){
        String message = GameController.getInstance().removeFeature();
        Response response = new Response();
        if(!message.equals("feature removed successfully")) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response removeRoute(){
        String message = GameController.getInstance().removeRoute();
        Response response = new Response();
        if(!message.equals(unitCommands.roadRemoved.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response repair(){
        String message = GameController.getInstance().repair();
        Response response = new Response();
        if(!message.equals(unitCommands.repairedSuccessful.regex)) response.setStatus(400);
        response.addMassage(message);
        return response;
    }
    private Response buildUnit(Request request){
        String type = (String) request.getParams().get("type");
        String message = GameController.getInstance().buildUnit(type);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response buildBuilding(Request request){
        String type = (String) request.getParams().get("type");
        String message = GameController.getInstance().buildBuilding(BuildingType.valueOf(type));
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response buyTile(Request request){
        String command = (String) request.getParams().get("coordinates");
        String message = GameController.getInstance().buyTile(command);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response buyUnit(Request request){
        String type = (String) request.getParams().get("type");
        String message = GameController.getInstance().buyUnit(type);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response lockCitizenToTile(Request request){
        String coordinates = (String) request.getParams().get("coordinates");
        String message = GameController.getInstance().lockCitizenToTile(coordinates);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response unlockCitizenToTile(Request request){
        String coordinates = (String) request.getParams().get("coordinates");
        String message = GameController.getInstance().lockCitizenToTile(coordinates);
        Response response = new Response();
        response.addMassage(message);
        return response;
    }
    private Response isGameEnd(){
        Response response = new Response();
        response.addParam("player", GameController.getInstance().playerToJson(GameController.getInstance().isGameEnd()));
        return response;
    }


    private Response moveUnit(Request request){
        int destinationX = (int) request.getParams().get("destinationX");
        int destinationY = (int) request.getParams().get("destinationY");

        // TODO: move combat unit in THE tile
        Matcher matcher = Pattern.compile("move CUnit (?<x>\\d+) (?<y>\\d+)").matcher("move CUnit " + destinationX + " " + destinationY);
        Response response = new Response();
        response.addMassage(GameController.getInstance().moveUnit(matcher));
        if(response.getMassage() == null){
            response.addMassage("successful");
        }else{
            response.setStatus(400);
        }
        response.addParam("player", GameController.getInstance().playerToJson(GameController.getInstance().getPlayerTurn()));
        return response;
    }
    private Response notYourTurn()
    {
        Response response = new Response();
        response.addMassage("not your turn");
        return response;
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
        User receiver =  Server.registerController.getUserByUsername((String) request.getParams().get("receiver"));
        User sender = Server.registerController.getUserByUsername((String) request.getParams().get("sender"));
        Message tmp1 =  request.getPrivateMessages().get(0);
        Message tmp2 =  request.getPrivateMessages().get(1);
        sender.getPrivateChats().get(receiver.getUsername()).add(tmp1);
        receiver.getPrivateChats().get(sender.getUsername()).add(tmp2);
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response sendPublicMessage(Request request) {
        publicMessage publicMessage = request.getPublicMessages().get(0);
        User sender =  Server.registerController.getUserByUsername((String) request.getParams().get("sender"));
        Server.chatServer.getPublicChats().add(publicMessage);
        Server.chatServer.writeData();
        return new Response();
    }

    private Response deleteMessageForAll(Request request) {
        User receiver =  Server.registerController.getUserByUsername((String) request.getParams().get("receiver"));
        User sender = Server.registerController.getUserByUsername((String) request.getParams().get("sender"));
        int index = (int) Math.floor((Double) request.getParams().get("index"));
        sender.getPrivateChats().get(receiver.getUsername()).get(index).setMessage("#deleted");
        receiver.getPrivateChats().get(sender.getUsername()).get(index).setMessage("#deleted");
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response deleteMessageForSender(Request request) {
        User receiver =  Server.registerController.getUserByUsername((String) request.getParams().get("receiver"));
        User sender = Server.registerController.getUserByUsername((String) request.getParams().get("sender"));
        int index = (int) Math.floor((Double) request.getParams().get("index"));
        sender.getPrivateChats().get(receiver.getUsername()).get(index).setMessage("#deleted");
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response getUser(String username) {
        Response response = new Response();
        response.addUser(Server.registerController.getUserByUsername(username));
        return response;
    }

    private Response editMessage(Request request) {
        User receiver =  Server.registerController.getUserByUsername((String) request.getParams().get("receiver"));
        User sender = Server.registerController.getUserByUsername((String) request.getParams().get("sender"));
        String finalMessage = (String) request.getParams().get("message");
        int index = (int) Math.floor((Double) request.getParams().get("index"));
        sender.getPrivateChats().get(receiver.getUsername()).get(index).setMessage(finalMessage);
        receiver.getPrivateChats().get(sender.getUsername()).get(index).setMessage(finalMessage);
        Server.registerController.writeDataOnJson();
        return new Response();
    }

    private Response seenMessage(Request request){
        User receiver =  Server.registerController.getUserByUsername((String) request.getParams().get("receiver"));
        User sender = Server.registerController.getUserByUsername((String) request.getParams().get("sender"));
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
        response.addUser(Server.registerController.getUserByUsername(username));

        return response;
    }

    private Response isOnline(Request request) {
        Response response = new Response();

        for (User user : Server.chatServer.getOnlineUsers().keySet())
            response.getParams().put(user.getUsername(), user);
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
        response.setUsers(Menu.allUsers);
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
            Menu.loggedInUser = user;
            response.addUser(user);
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
            Menu.loggedInUser = user;
            response.addUser(user);
            addOnlineUser(username);
            Server.registerController.writeDataOnJson();
        }
        return response;
    }

    private Response updatePublicChats() {
        Response response = new Response();
        response.setPublicChats( Server.chatServer.getPublicChats());
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
        boolean isPrivate = (boolean) request.getParams().get("private");
        int capacity = Integer.parseInt((String) request.getParams().get("capacity"));

        if(MainMenuController.getRoomByRoomID(roomID) != null)
        {
            response.addMassage("this roomID is already taken");
            return response;
        }

        this.gameRoom = new GameRoom(this, roomID, isPrivate, capacity);

        MainMenuController.addToGameRooms(this.gameRoom);
        response.addMassage("room created successfully");
        return response;
    }
    private Response getJoinRequests()
    {
        ArrayList<String> joinRequests = new ArrayList<>();
        for (RequestHandler joinRequest : gameRoom.getJoinRequests())
            joinRequests.add(joinRequest.getUser().getUsername());


        Response response = new Response();
        response.addParam("joinRequests", joinRequests);
        return response;
    }
    private Response getJoinedClients()
    {
        Response response = new Response();
        ArrayList<RequestHandler> joinedClients = gameRoom.getJoinedClients();

        System.out.println(joinedClients);

        ArrayList<String> joinedClientsUsernames = new ArrayList<>();
        ArrayList<String> joinedClientsNicknames = new ArrayList<>();

        for (RequestHandler joinedClient : joinedClients) {
            joinedClientsUsernames.add(joinedClient.getUser().getUsername());
            joinedClientsNicknames.add(joinedClient.getUser().getNickname());
        }

        response.addParam("joinedClientsUsernames", joinedClientsUsernames);
        response.addParam("joinedClientsNicknames", joinedClientsNicknames);
        return response;
    }
    private Response acceptJoinRequest(Request request)
    {
        RequestHandler acceptedJoinRequest = gameRoom.getJoinRequests().get(Integer.parseInt((String) request.getParams().get("index")));
        gameRoom.removeFromJoinedRequests(acceptedJoinRequest);
        gameRoom.addToJoinedClients(acceptedJoinRequest);

        Response response = new Response();
        response.addMassage("join request accepted");
        return response;
    }
    private Response rejectJoinRequest(Request request)
    {
        RequestHandler rejectedJoinRequest = gameRoom.getJoinRequests().get(Integer.parseInt((String) request.getParams().get("index")));
        gameRoom.removeFromJoinedRequests(rejectedJoinRequest);

        Response response = new Response();
        response.addMassage("join request rejected");

        return response;
    }
    private Response publicRooms(Request request)
    {
        Response response = new Response();

        ArrayList<String> id = new ArrayList<>();
        ArrayList<String> capacity = new ArrayList<>();
        ArrayList<String> currPlayers = new ArrayList<>();
        ArrayList<String> names = new ArrayList<>();

        for (GameRoom gameRoom : MainMenuController.gameRooms) {
            String adminUsername = gameRoom.getRoomAdmin().user.getUsername();
            names = new ArrayList<>();
            names.add("admin: " + adminUsername);

            if (!gameRoom.isPrivate()) {
                id.add(gameRoom.getRoomID());
                capacity.add(String.valueOf((gameRoom.getCapacity())));
                currPlayers.add(String.valueOf(gameRoom.getJoinedClients().size()));
                for (RequestHandler requestHandler : gameRoom.getJoinedClients())
                    names.add(requestHandler.user.getUsername());
            }
        }

        response.addMassage("successful");
        response.getParams().put("id", id);
        response.getParams().put("capacity", capacity);
        response.getParams().put("joinedClients", currPlayers);
        response.getParams().put("names", names);

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
        if (gameRoom.getJoinedClients().size() == gameRoom.getCapacity() - 1)
        {
            response.addMassage("room is full");
            return response;
        }

        gameRoom.addToJoinRequests(this);
        gameRoom.getRoomAdmin().notifyNewJoinRequestToAdmin(this);
        response.addMassage("request was sent");
        return response;
    }
    private Response findRoom(Request request)
    {
        Response response = new Response();
        boolean isSent = false;

        for (GameRoom gameRoom : MainMenuController.gameRooms)
            if (gameRoom.getJoinedClients().size() < gameRoom.getCapacity() - 1 && !gameRoom.isPrivate()) {
                gameRoom.addToJoinRequests(this);
                gameRoom.getRoomAdmin().notifyNewJoinRequestToAdmin(this);
                isSent = true;
                response.addMassage("request was sent");
            }
        if (!isSent)
            response.addMassage("there is no public room");

        return response;
    }
    private void notifyNewJoinRequestToAdmin(RequestHandler joinRequestSender)
    {
        try
        {
            listenerSocketDOS.writeUTF("you have a new join request from " + joinRequestSender.getUser().getUsername());
            listenerSocketDOS.flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
    private Response startGame()
    {
        GameController.getInstance().addPlayer(new Player(Civilization.values()[0], user.getUsername(), user.getNickname(), user.getPassword(),user.getScore()));

        for (int i = 0; i < gameRoom.getJoinedClients().size(); i++)
        {
            RequestHandler joinedClient = gameRoom.getJoinedClients().get(i);
            GameController.getInstance().addPlayer(new Player(Civilization.values()[i + 1], joinedClient.getUser().getUsername(), joinedClient.getUser().getNickname(), joinedClient.getUser().getPassword(), joinedClient.getUser().getScore()));
        }

        GameController.getInstance().initGame();

        try
        {
            listenerSocketDOS.writeUTF("game started");
            for (RequestHandler joinedClient : gameRoom.getJoinedClients())
                joinedClient.listenerSocketDOS.writeUTF("game started");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        Response response = new Response();
        response.addMassage("game started");
        return response;
    }
    private void notifyGameStarted()
    {
        try
        {
            listenerSocketDOS.writeUTF("game started");
            listenerSocketDOS.flush();
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
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

    private Response searchFriends(Request request)
    {
        Response response = new Response();
        String searchedText = (String) request.getParams().get("username");
        int length = searchedText.length();

        ArrayList<String> acceptedUsernames = new ArrayList<>();

        for (User user : Menu.allUsers)
            if ((user.getUsername().length() > length && searchedText.equals(user.getUsername().substring(0, length))))
                acceptedUsernames.add(user.getUsername());

        response.getParams().put("usersFound", acceptedUsernames);
        return response;
    }

    private Response sendFriendInv(Request request)
    {
        Response response = new Response();
        String receiverUsername = (String) request.getParams().get("username");

        User receiver = Server.registerController.getUserByUsername(receiverUsername);
        if (!receiver.getFriendRequests().contains(Menu.loggedInUser.getUsername()) &&
                !receiver.getUsername().equals(Menu.loggedInUser.getUsername())) {
            receiver.getFriendRequests().add(Menu.loggedInUser.getUsername());
            Server.registerController.writeDataOnJson();
            response.addMassage("invitation sent");
        }
        else response.addMassage("failed:(");

        return response;
    }

    private Response friendRequests(Request request) {
        Response response = new Response();
        ArrayList<String> friendRequests = new ArrayList<>(Menu.loggedInUser.getFriendRequests());

        response.getParams().put("usernames", friendRequests);

        return response;
    }

    private Response acceptFriend(Request request) {
        Response response = new Response();
        String username = (String) request.getParams().get("username");
        Menu.loggedInUser.getFriends().add(username);
        Menu.loggedInUser.getFriendRequests().remove(username);
        Server.registerController.writeDataOnJson();
        return response;
    }

    private Response rejectFriend(Request request) {
        Response response = new Response();
        String username = (String) request.getParams().get("username");
        Menu.loggedInUser.getFriendRequests().remove(username);
        Server.registerController.writeDataOnJson();
        return response;
    }
}









