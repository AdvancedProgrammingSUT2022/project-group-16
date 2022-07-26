import Controllers.CommandHandler;
import IO.Client;
import IO.Response;
import Models.City.Citizen;
import Models.City.City;
import Models.City.CityState;
import Models.Player.Notification;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.*;
import Models.City.Construction;
import Models.Player.*;
import Models.Terrain.Position;
import Models.Terrain.Tile;
import Models.TypeAdapters.*;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.Unit;
import Models.Units.UnitState;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.cheatCode;
import enums.gameCommands.infoCommands;
import enums.gameEnum;
import enums.mainCommands;
import javafx.animation.AnimationTimer;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Matcher;

public class Game extends Application {

    public Label year;
    public Label turn;
    private Hex[][] hexagons;
    private CommandHandler commandHandler = CommandHandler.getInstance();
    ArrayList<Hex> playerTurnTiles = new ArrayList<>();
    private boolean needUpdateScience = false;
    private boolean needUpdateProduction = true;
    public AudioClip audioClip = new AudioClip(Game.class.getResource("audio/gameAudios/click.mp3").toExternalForm());
    private final AudioClip tradeAccepted = new AudioClip(Game.class.getResource("audio/gameAudios/tradeAccept.mp3").toExternalForm());
    private final AudioClip gameDemo = new AudioClip(Game.class.getResource("audio/2.mp3").toExternalForm());
    private final AudioClip tradeOffered = new AudioClip(Game.class.getResource("audio/gameAudios/tradeOffered.mp3").toExternalForm());
    private final AudioClip notification = new AudioClip(Game.class.getResource("audio/gameAudios/notification.wav").toExternalForm());

    private boolean isCPressed = false;
    private boolean isShiftPressed = false;
    private boolean isAutoSaveOn = false;
    private boolean isAlertTechOn = false;
    private boolean isAlertDiplomacyOn = false;
    private Double selectedCoins = 0.0;
    private Resource selectedResource = null;
    private final ImageView techAlert = new ImageView();
    private final ImageView diplomacyAlert = new ImageView();
    @FXML
    private Pane pane;
    private Pane hexagonsPane;
    private AnimationTimer animationTimer = new AnimationTimer()
    {
        @Override
        public void handle(long l)
        {
            handleMovingMap();
        }
    };
    private static final double MOVING_MAP_SPEED = 5;
    // for map
    private static boolean movingUP;
    private static boolean movingLeft;
    private static boolean movingDown;
    private static boolean movingRight;

	private Gson gson;
    private Socket socket = Client.socket;
    private Socket listenerSocket = Client.listenerSocket;
    private DataInputStream socketDIS;
    private DataOutputStream socketDOS;
    private DataInputStream listenerSocketDIS;

    {
        try
        {
            socketDIS = new DataInputStream(socket.getInputStream());
            socketDOS = new DataOutputStream(socket.getOutputStream());
            listenerSocketDIS = new DataInputStream(listenerSocket.getInputStream());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }



    @Override
    public void start(Stage stage) throws Exception {
        ChatMenu.isGameStarted = true;
        Pane root = FXMLLoader.load(new URL(getClass().getResource("fxml/game.fxml").toExternalForm()));
        Scene scene = new Scene(root);
        scene.setOnKeyPressed(this::onKeyPressed);
        scene.setOnKeyReleased(this::onKeyReleased);
        stage.setScene(scene);
        root.requestFocus();
        stage.show();
        audioClip.play();
    }

    public void setPlayer(Player player)
    {
        commandHandler.setPlayer(player);
    }
    public void setSockets(Socket socket, Socket listenerSocket)
    {
        this.socket = socket;
        this.listenerSocket = listenerSocket;
        try
        {
            socketDIS = new DataInputStream(socket.getInputStream());
            socketDOS = new DataOutputStream(socket.getOutputStream());
            listenerSocketDIS = new DataInputStream(listenerSocket.getInputStream());
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        commandHandler.setSocket(socket);
    }

    private void cheatCode() {
        TextField textField = new TextField();
        textField.setStyle("-fx-background-color: black;" +
                "-fx-text-fill: white;" +
                "-fx-border-width: 2;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 4;" +
                "-fx-background-radius: 6");
        textField.setPromptText("cheat code...");
        pane.getChildren().add(textField);

        /*cheat codes*/
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                Matcher matcher;
                String command = textField.getText();
                if((matcher = cheatCode.compareRegex(command, cheatCode.increaseGold)) != null)
                    commandHandler.increaseGold(command);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) {
                    commandHandler.increaseTurns(command);
                    if (commandHandler.isGameEnd() != null) {
                        winPanel(commandHandler.isGameEnd(), commandHandler.isGameEnd().getGameScore());
                    }
                    setInformationStyles();
                    updateYear();
                    updateTurnNumber();
                    updateScreen();
                }
                else if((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null)
                    commandHandler.increaseFood(command);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null)
                    commandHandler.addTechnology(command);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.gainAllTechnologies)) != null)
                    commandHandler.gainAllTechnologies(command);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseHappiness)) != null)
                    commandHandler.increaseHappiness(command);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.killEnemyUnit)) != null)
                    commandHandler.killEnemyUnit(command);
//                else if((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null)
//                    commandHandler.moveUnit(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseHealth)) != null)
                    commandHandler.increaseHealth(command);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseScore)) != null)
                    commandHandler.increaseScore(command);
                else if(cheatCode.compareRegex(command, cheatCode.winGame) != null) {
                    commandHandler.winGame();
                    winPanel(commandHandler.getPlayer(), 5);
                }
                else if(cheatCode.compareRegex(command, cheatCode.gainBonusResource) != null)
                    commandHandler.gainBonusResourceCheat(command);
                else if(cheatCode.compareRegex(command, cheatCode.gainStrategicResource) != null)
                    commandHandler.gainStrategicResourceCheat(command);
                else if(cheatCode.compareRegex(command, cheatCode.gainLuxuryResource) != null)
                    commandHandler.gainLuxuryResourceCheat(command);
                else if(command.equals("a")) {
                    ((Settler) commandHandler.getPlayer().getUnits().get(1)).createCity();
                    commandHandler.getPlayer().getCities().get(0).addPopulation(4);
                }
                else if(command.equals("b")) {
                    System.out.println(selectedCoins);
                }
                pane.getChildren().remove(textField);
                setInformationStyles();
                pane.requestFocus();

                updateScreen();
            }
        });
    }
    @FXML
    private void initialize() {
        Platform.runLater(this::loadGame);
    }
    private void loadGame()
    {
        setPlayer(MultiplayerMenu.player);
        setSockets(Client.socket, Client.listenerSocket);

        hexagonsPane = (Pane) pane.getChildren().get(0);
        hexagonsPane.setLayoutX(100);
        hexagonsPane.setLayoutY(45);
        hexagonsPane.setPrefWidth(commandHandler.getPlayer().MAP_SIZE * 90);

        hexagonsPane.setPrefHeight(commandHandler.getPlayer().MAP_SIZE * 100);
        Hex.setPane(hexagonsPane);
        hexagons = new Hex[commandHandler.getPlayer().MAP_SIZE][commandHandler.getPlayer().MAP_SIZE];
        updateScreen();

        //cheatCode
        pane.addEventHandler(KeyEvent.KEY_PRESSED, (key) -> {
            if(key.getCode() == KeyCode.C)
                isCPressed = true;
            if(key.getCode() == KeyCode.SHIFT)
                isShiftPressed = true;
            if(isShiftPressed && isCPressed) {
                cheatCode();
            }
        });
        pane.addEventHandler(KeyEvent.KEY_RELEASED, (key) -> {
            if(key.getCode() == KeyCode.C)
                isCPressed = false;
            if(key.getCode() == KeyCode.SHIFT)
                isShiftPressed = false;
        });

//        setInformationStyles();
        pane.getChildren().get(12).setOnMousePressed(mouseEvent -> {
            showTechnologies();
            audioClip.play();
        });

        try {
            techAlert.setFitHeight(25);
            techAlert.setFitWidth(25);
            techAlert.setImage(new Image(String.valueOf(new
                    URL(getClass().getResource("photos/gameIcons/alert.png").toExternalForm()))));
            diplomacyAlert.setFitHeight(25);
            diplomacyAlert.setFitWidth(25);
            diplomacyAlert.setImage(new Image(String.valueOf(new
                    URL(getClass().getResource("photos/gameIcons/alert.png").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        animationTimer.start();

        // run listener
        Runnable listenerRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                Response messageFromServer;

                while (true)
                {
                    try
                    {
                        messageFromServer = Response.fromJson(listenerSocketDIS.readUTF());
                    }
                    catch (IOException e)
                    {
                        throw new RuntimeException(e);
                    }

                    if(messageFromServer.getMassage().equals("update"))
                    {
                        Player updatedPlayer = commandHandler.jsonToPlayer((String) messageFromServer.getParams().get("player"));
                        commandHandler.setPlayer(updatedPlayer);
                        updateScreen();
                    }
                }
            }
        };
        Thread listenerThread = new Thread(listenerRunnable);
        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    public void updateScreen()
    {
//        System.out.println(commandHandler.getPlayer().getUsername() + ":\n");
//        for (Tile tile : commandHandler.getPlayer().getMap().keySet())
//            if(tile.getCombatUnitInTile() != null)
//                System.out.println(tile.getPosition().X + "," + tile.getPosition().Y);

        final Game tmpGame = this;
        Runnable updateScreenRunnable = new Runnable()
        {
            @Override
            public void run()
            {
                if(commandHandler.getPlayer().getIsYourTurn() == false)
                    pane.setDisable(true);
                else
                    pane.setDisable(false);

                hexagonsPane.getChildren().clear();

                // update tiles
                int x = 0;
                for(int i = 0; i < commandHandler.getPlayer().MAP_SIZE; i++)
                {
                    int y = (i % 2 == 0 ? 0 : 45);
                    for(int j = 0; j < commandHandler.getPlayer().MAP_SIZE ; j++){
                        hexagons[j][i] = new Hex(new Position(x, y), commandHandler.getPlayer(), tmpGame);
                        y += 100;
                    }
                    x += 90;
                }

                generateMapForPlayer(commandHandler.getPlayer());
                setInformationStyles();
            }
        };

        Platform.runLater(updateScreenRunnable);
    }

    private void onKeyPressed(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.UP)
        {
            Game.movingUP = true;
        }
        else if(keyEvent.getCode() == KeyCode.LEFT)
            Game.movingLeft = true;
        else if(keyEvent.getCode() == KeyCode.DOWN)
            Game.movingDown = true;
        else if(keyEvent.getCode() == KeyCode.RIGHT)
            Game.movingRight = true;
    }
    private void onKeyReleased(KeyEvent keyEvent)
    {
        if(keyEvent.getCode() == KeyCode.UP)
            Game.movingUP = false;
        else if(keyEvent.getCode() == KeyCode.LEFT)
            Game.movingLeft = false;
        else if(keyEvent.getCode() == KeyCode.DOWN)
            Game.movingDown = false;
        else if(keyEvent.getCode() == KeyCode.RIGHT)
            Game.movingRight = false;
    }
    private void handleMovingMap()
    {
        if(Game.movingUP && hexagonsPane.getLayoutY() < 45)
            hexagonsPane.setLayoutY(hexagonsPane.getLayoutY() + MOVING_MAP_SPEED);
        if(Game.movingLeft && hexagonsPane.getLayoutX() < 100)
            hexagonsPane.setLayoutX(hexagonsPane.getLayoutX() + MOVING_MAP_SPEED);
        if(Game.movingDown && hexagonsPane.getLayoutY() > -1000)
            hexagonsPane.setLayoutY(hexagonsPane.getLayoutY() - MOVING_MAP_SPEED);
        if(Game.movingRight && hexagonsPane.getLayoutX() > -700)
            hexagonsPane.setLayoutX(hexagonsPane.getLayoutX() - MOVING_MAP_SPEED);
    }


    public void generateMapForPlayer(Player player){
        playerTurnTiles.clear();
        for (Tile tile : player.getMap().keySet()) {
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileState(player.getMap().get(tile));
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTile(tile);
            playerTurnTiles.add(hexagons[tile.getPosition().X][tile.getPosition().Y]);
        }
        playerTurnTiles.forEach(Hex::addHex);
    }
    public void changeTurn(MouseEvent mouseEvent) {
        // here
        String result = commandHandler.checkChangeTurn();
        if (result != null && result.equals("game Ended"))
            winPanel(commandHandler.isGameEnd(), commandHandler.isGameEnd().getGameScore());

        //TODO: fix bugs
        updateScreen();
        setInformationStyles();
        pane.getChildren().remove(diplomacyAlert);
        pane.getChildren().remove(techAlert);
        if (commandHandler.getPlayer().getResearchingTechnology() != null )
            pane.getChildren().remove(techAlert);
        else if (!pane.getChildren().contains(techAlert)) {
            pane.getChildren().add(techAlert);
            notification.play();
            setCoordinates(pane, 50, 290);
        }
        if (commandHandler.getPlayer().getTradeRequests().size() == 0)
            pane.getChildren().remove(diplomacyAlert);
        else if (!pane.getChildren().contains(diplomacyAlert)){
            pane.getChildren().add(diplomacyAlert);
            notification.play();
            setCoordinates(pane, 1210, 335);
        }
        // TODO: check for update yearCounter and turnCounter
    }

    private void VboxStyle(VBox box) {
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #572e2e;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 150");
    }
    private void labelStyle(Label label) {
        label.setStyle("-fx-text-fill: white;" +
                "-fx-font-size: 18;");
    }
    private VBox informationVbox(String information, double index) {
        VBox box = new VBox();
        box.setLayoutX(70);
        box.setLayoutY((index - 12) * 55 + 20);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        VboxStyle(box);
        Label label = new Label();
        label.setText(information);
        labelStyle(label);
        box.getChildren().add(label);
        return box;
    }
    private void updateYear() {
        // here
        year.setText(String.valueOf(commandHandler.getYear()));
    }
    private void updateTurnNumber() {
        // here
        turn.setText(String.valueOf(commandHandler.getTurnCounter()));
    }
    private VBox scienceInformationStyle() {
        VBox box = new VBox();
        box.setLayoutX(70);
        box.setLayoutY(295);
        box.setSpacing(5);
        box.setAlignment(Pos.TOP_LEFT);
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #572e2e;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 600;");
        Label label = new Label();
        // here
        label.setText(commandHandler.showResearch());
        labelStyle(label);
        box.getChildren().add(label);
        return box;
    }
    private VBox productionInformationStyle() {
        VBox box = new VBox();
        box.setLayoutX(70);
        box.setLayoutY(75);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #572e2e;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 200");
        return updateProductionYield(box);
    }
    private VBox updateProductionYield(VBox box) {
        for(int i = box.getChildren().size() - 1; i >= 0; i--)
            box.getChildren().remove(box.getChildren().get(0));
        if(commandHandler.getPlayer().getCities().size() == 0) {
            Label label = new Label();
            label.setText("production.y");
            labelStyle(label);
            box.getChildren().add(label);
            Label label1 = new Label();
            label1.setText("nothing...");
            labelStyle(label1);
            box.getChildren().add(label1);
        }
        else {
            Label title = new Label();
            title.setText("production.y");
            labelStyle(title);
            box.getChildren().add(title);
            for (City city : commandHandler.getPlayer().getCities()) {
                Label label = new Label();
                label.setText(city.getName() + " - " + city.getProductionYield());
                labelStyle(label);
                box.getChildren().add(label);
            }
        }
        return box;
    }
    private void setHoverForInformationTitles(ImageView tmp, VBox information) {
        tmp.setOnMouseMoved(mouseEvent -> {
            if(information.getChildren().get(0).getClass() == Label.class &&
                    ((Label) information.getChildren().get(0)).getText().split(" ")[0].equals("Research") && needUpdateScience) {
                needUpdateScience = false;
                // here
                ((Label) information.getChildren().get(0)).setText(commandHandler.showResearch());
            }
            if(information.getChildren().get(0).getClass() == Label.class &&
                    ((Label) information.getChildren().get(0)).getText().split(" ")[0].equals("production.y") && needUpdateProduction) {
                needUpdateProduction = false;
                pane.getChildren().remove(information);
                pane.getChildren().add(updateProductionYield(information));
            }
            if(!pane.getChildren().contains(information)) {
                fade(information).play();
                pane.getChildren().add(pane.getChildren().size() - 2, information);
            }
        });
        tmp.setOnMouseExited(mouseEvent -> pane.getChildren().remove(information));
    }
    private FadeTransition fade(Node node) {
        FadeTransition ft = new FadeTransition();
        ft.setNode(node);
        ft.setDuration(new Duration(200));
        ft.setFromValue(0);
        ft.setToValue(1);
        return ft;
    }
    private void setInformationStyles() {
        setHoverForInformationTitles((ImageView) pane.getChildren().get(7), informationVbox(String.valueOf(commandHandler.getPlayer().getGold()), 12));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(8), productionInformationStyle());
        setHoverForInformationTitles((ImageView) pane.getChildren().get(9), informationVbox(String.valueOf(commandHandler.getPlayer().getFood()), 14));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(10), informationVbox(String.valueOf(commandHandler.getPlayer().getPopulation()), 15));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(11), informationVbox(String.valueOf(commandHandler.getPlayer().getHappiness()), 16));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(12), scienceInformationStyle());
        setHoverForInformationTitles((ImageView) pane.getChildren().get(14), panelsVbox("cities", 20));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(16), panelsVbox("units", 75));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(18), panelsVbox("military", 130));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(20), panelsVbox("demographics", 185));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(22), panelsVbox("notifications", 240));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(24), panelsVbox("economics", 295));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(26), panelsVbox("diplomacy", 350));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(29), informationVbox("menu", 24));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(31), informationVbox("Technology Tree", 18));
        if (commandHandler.getPlayer().getHappiness() < 50) {
            try {
                ((ImageView) pane.getChildren().get(11)).setImage(new Image(String.valueOf(new URL("photos/gameIcons/Malcontent.png"))));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
        }
        if (commandHandler.getPlayer().getResearchingTechnology() == null && !isAlertTechOn) {
            isAlertTechOn = true;
            pane.getChildren().add(techAlert);
            notification.play();
            setCoordinates(pane, 50, 290);

        }
        else if (!isAlertTechOn)
            pane.getChildren().remove(techAlert);
        if (commandHandler.getPlayer().getTradeRequests().size() != 0 && !isAlertDiplomacyOn) {
            isAlertDiplomacyOn = true;
            pane.getChildren().add(diplomacyAlert);
            notification.play();
            setCoordinates(pane, 1210, 335);
        }
        else if (!isAlertDiplomacyOn)
            pane.getChildren().remove(diplomacyAlert);
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showGainedTechnologies(VBox box)
    {
        addLabelToBox(infoCommands.gained.regex, box);
        ArrayList<Technology> tmp = commandHandler.getPlayer().getTechnologies();
        if(tmp.size() == 0)
            addLabelToBox(infoCommands.nothing.regex, box);
        else
            for (int i = 0; i < tmp.size(); i++)
                addLabelToBox((i + 1) + ": " + tmp.get(i).toString(), box);
        addLabelToBox(" ", box);
    }
    private void addLabelToBox(String line, VBox box) {
        Label label = new Label();
        label.setText(line);
        labelStyle(label);
        box.getChildren().add(label);
    }
    private void addLabelToPane(String line, Pane box) {
        Label label = new Label();
        label.setText(line);
        labelStyle(label);
        box.getChildren().add(label);
    }
    private boolean isValidNumber(String number) {
        for(int i = 0; i < number.length(); i++)
            if(number.charAt(i) > 57 || number.charAt(i) < 48)
                return false;
        return true;
    }
    private void updateBox(Pane box) {
        pane.getChildren().remove(box);
        pane.getChildren().add(box);
    }
    private VBox panelsVbox(String information, double y) {
        VBox box = new VBox();
        box.setLayoutX(1050);
        box.setLayoutY(y);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        VboxStyle(box);
        Label label = new Label();
        label.setText(information);
        labelStyle(label);
        box.getChildren().add(label);
        return box;
    }
    private void showTechnologies()
    {
        //pane style
        Pane list = new Pane();
        panelsPaneStyle(list, 350, 800,false);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 15, 35);
        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        addLabelToBox(infoCommands.numberOfCup.regex + commandHandler.getPlayer().getCup(), box);
        showGainedTechnologies(box);
        addLabelToBox(infoCommands.chooseTechnology.regex, box);

        //find candidates
        int max = 0;
        int flag = -1;
        AtomicInteger number = new AtomicInteger(-1);
        Player tmp = commandHandler.getPlayer();
        ArrayList<Technology> candidateTechs = new ArrayList<>();
        for(int i = 0; i < Technology.values().length; i++)
            if(tmp.getTechnologies().containsAll(Technology.values()[i].requiredTechnologies) &&
                    !tmp.getTechnologies().contains(Technology.values()[i]))
            {
                if(tmp.getResearchingTechnology() != null &&
                        Technology.values()[i].equals(tmp.getResearchingTechnology())) {
                    addLabelToBox((max + 1) + ": " + Technology.values()[i].toString() +
                            infoCommands.currResearch.regex, box);
                    flag = max + 1;
                    int finalMax = max;
                    int finalFlag2 = flag;
                    int finalI = i;
                    box.getChildren().get(box.getChildren().size() - 1).setOnMouseMoved(mouseEvent ->
                    {
                        if(list.getChildren().get(list.getChildren().size() - 1).getClass() != Pane.class)
                            technologyInformationBox(Technology.values()[finalI], finalI, list, tmp);
                    });
                    box.getChildren().get(box.getChildren().size() - 1).setOnMouseExited(mouseEvent ->
                            list.getChildren().remove(list.getChildren().size() - 1));
                    box.getChildren().get(box.getChildren().size() - 1).setOnMouseClicked(mouseEvent ->
                            selectTechnology(list, box, number, finalMax, finalFlag2, candidateTechs, tmp));
                }
                else {
                    addLabelToBox((max + 1) + ": " + Technology.values()[i].toString(), box);
                    int finalMax = max;
                    int finalFlag2 = flag;
                    int finalI = i;
                    box.getChildren().get(box.getChildren().size() - 1).setOnMouseMoved(mouseEvent ->
                    {
                        if(list.getChildren().get(list.getChildren().size() - 1).getClass() != Pane.class)
                            technologyInformationBox(Technology.values()[finalI], finalI, list, tmp);
                    });
                    box.getChildren().get(box.getChildren().size() - 1).setOnMouseExited(mouseEvent ->
                            list.getChildren().remove(list.getChildren().size() - 1));
                    box.getChildren().get(box.getChildren().size() - 1).setOnMouseClicked(mouseEvent ->
                            selectTechnology(list, box, number, finalMax, finalFlag2, candidateTechs, tmp));
                }
                candidateTechs.add(Technology.values()[i]);
                max++;
            }
        pane.getChildren().add(list);
        setCoordinates(pane, 465, -1);
        box.setOnScroll((ScrollEvent event) -> {
            double yScale = 30;
            double deltaY = event.getDeltaY();
            if (deltaY < 0)
                yScale *= -1;
            if((box.getLayoutY() + box.getHeight() > 650 && yScale < 0) || box.getLayoutY() < 40 && yScale > 0)
                box.setLayoutY(box.getLayoutY() + yScale);
        });
    }
    private void technologyInformationBox(Technology technology, int i, Pane parent, Player player) {
        Pane list = new Pane();
        list.setLayoutX(-280);
        list.setLayoutY(180);
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(String.valueOf(new URL(getClass()
                    .getResource("photos/backgrounds/icons/frontGamePage.jpg").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(250);
        imageView.setFitHeight(80);
        list.getChildren().add(0, imageView);
        imageView.setStyle("-fx-background-radius: 8;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;");
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinates(list, -20, -20);
        addLabelToPane(infoCommands.requiredTurns.regex +
                            (technology.cost / 10 - player.getResearchingTechCounter()[i]), box);
        // here
        if(commandHandler.requiredTechForBuilding(technology) != null)
            addLabelToBox(infoCommands.willGain.regex + commandHandler.
                    requiredTechForBuilding(technology).name(), box);
        if(commandHandler.requiredTechForImprovement(technology) != null)
            addLabelToBox(infoCommands.willGain.regex + commandHandler.
                    requiredTechForImprovement(technology).name(), box);
        parent.getChildren().add(list);
    }
    private void selectTechnology(Pane list, VBox box, AtomicInteger number, int max, int flag, ArrayList<Technology> candidateTechs, Player tmp) {
        number.set(max + 1);
        int boxActualSize = 4 + candidateTechs.size() + tmp.getTechnologies().size();
        if (tmp.getTechnologies().size() == 0)
            boxActualSize++;

        if(box.getChildren().size() != boxActualSize)
            box.getChildren().remove(box.getChildren().size() - 1);
        int flg = -1;

        for(int j = 0; j < Technology.values().length; j++)
            if(Technology.values()[j] == candidateTechs.get(number.get() - 1)) flg = j;

        if(number.get() == flag) {
            addLabelToBox(infoCommands.alreadyResearching.regex, box);
            updateBox(list);
        }
        else if(tmp.getCup() >= candidateTechs.get(number.get() - 1).cost / 10 - tmp.getResearchingTechCounter()[flg])
        {
            tmp.setResearchingTechnology(candidateTechs.get(number.get() - 1));
            addLabelToBox(infoCommands.choose.regex + candidateTechs.get(number.get() - 1).name() + infoCommands.successful.regex, box);
            tmp.reduceCup();
            isAlertTechOn = false;
            pane.getChildren().remove(techAlert);
            pane.getChildren().remove(list);
            needUpdateScience = true;
            showTechnologies();
        }
        else {
            addLabelToBox(infoCommands.enoughCup.regex + candidateTechs.get(number.get() - 1).name(), box);
            updateBox(list);
        }
    }
    private void panelsVboxStyle(VBox box) {
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(340);
        box.setLayoutY(180);
        box.setPrefWidth(600);
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: rgb(68,30,30);" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;");
    }
    private void panelsPaneStyle2(Pane box) {
        box.setLayoutX(340);
        box.setLayoutY(130);
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: rgb(0,7,114);" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 600");
    }
    private VBox printCities(Player player)
    {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);
        int destroyedCities = 0;
        for(City city : player.getSeizedCities())
            if(city.getState() == CityState.DESTROYED)
                destroyedCities++;
        int size = player.getCities().size() + player.getSeizedCities().size() - destroyedCities;
        addLabelToBox(infoCommands.cities.regex, box);
        if(size == 0)
            addLabelToBox(infoCommands.nothing.regex, box);
        else
        {
            for (int i = 0; i < player.getCities().size(); i++)
            {
                if(player.getCities().get(i) == player.getCurrentCapitalCity())
                    addLabelToBox(player.getCities().get(i).getName() + " (capital city)", box);
                else
                    addLabelToBox(player.getCities().get(i).getName(), box);
            }
            int attachedCities = 0;
            for (int i = 0; i < player.getSeizedCities().size() - destroyedCities; i++)
            {
                if(player.getSeizedCities().get(i).getState() == CityState.ATTACHED) {
                    addLabelToBox((attachedCities + player.getCities().size() + 1) + ": " +
                            player.getSeizedCities().get(i).getName() + " (attached)", box);
                    attachedCities++;
                }
            }
        }
        return box;
    }
    public void showAllCities()
    {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 450, 500, false);
        VBox box = new VBox();
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        list.setLayoutX(400);
        box.setLayoutX(200);
        ArrayList<City> tmp = new ArrayList<>();
        for(City city : commandHandler.getPlayer().getSeizedCities())
            if(city.getState() == CityState.ATTACHED)
                tmp.add(city);
        box.getChildren().add(printCities(commandHandler.getPlayer()));
        box.getChildren().add(new Label());
        addLabelToBox(infoCommands.searchEconomic.regex.substring(1), box);
        box.getChildren().get(box.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            pane.getChildren().remove(list);
            audioClip.play();
            showEconomics();
        });
        list.getChildren().add(exitButtonStyle());
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        box.setLayoutX(150);
        box.setLayoutY(15);

        for (int i = 1; i < ((VBox) box.getChildren().get(0)).getChildren().size(); i++) {
            Node node = ((VBox) box.getChildren().get(0)).getChildren().get(i);
            int finalI = i;
            node.setOnMousePressed(mouseEvent -> {
                audioClip.play();
                if (finalI <= commandHandler.getPlayer().getCities().size()) {
                    commandHandler.getPlayer().setSelectedCity(commandHandler.getPlayer().getCities().get(finalI - 1));
                    showCity();
                    commandHandler.getPlayer().setSelectedCity(null);
                }
                else {
                    commandHandler.getPlayer().setSelectedCity(tmp.get(finalI - commandHandler.getPlayer().getCities().size() - 1));
                    showCity();
                }
            });
        }
        list.getChildren().add(box);
        pane.getChildren().add(list);
    }
    private void winPanel(Player player, int score) {
        AudioClip victory = new AudioClip(Game.class.getResource("audio/victory.mp3").toExternalForm());
        victory.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 250, 250, false);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(5);
        list.getChildren().add(box);
        pane.getChildren().add(list);
        setCoordinates(list, 15, 20);
        setCoordinates(pane, 515, 235);

        addLabelToBox(player.getUsername() + " won the game", box);
        addLabelToBox("civilization: " + player.getCivilization().name(), box);
        addLabelToBox("civilization leader: " + player.getCivilization().leaderName, box);
        addLabelToBox("score: " + score, box);
        // here
        addLabelToBox("win year: " + commandHandler.getYear(), box);
        addLabelToBox("you will gain " + score / 10 + " score:)", box);
    }
    public void showEconomics()
    {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 1040, 500, false);
        list.setLayoutX(100);
        list.setLayoutY(110);
        ArrayList<City> n = commandHandler.getPlayer().getCities();
        VBox names = new VBox(), population = new VBox(), PF = new VBox(),
                foodY = new VBox(), cupY = new VBox(), goldY = new VBox(),
                productionY = new VBox(), coordinates = new VBox(),
                construction = new VBox(), remainingTurns = new VBox(), attached = new VBox();
        list.getChildren().addAll(names, population, PF, foodY, cupY, goldY,
                productionY, coordinates, construction, remainingTurns, attached);
        for(int i = 0; i < 11; i++) {
            ((VBox) list.getChildren().get(list.getChildren().size() - 1 - i)).setSpacing(5);
            ((VBox) list.getChildren().get(list.getChildren().size() - 1 - i)).setAlignment(Pos.CENTER);
        }
        for(City city : commandHandler.getPlayer().getSeizedCities())
            if(city.getState() == CityState.ATTACHED)
                n.add(city);
        if(n.size() != 0) {
            addLabelToBox("city name", names);
            addLabelToBox("population" , population);
            addLabelToBox("PF", PF);
            addLabelToBox("food.y", foodY);
            addLabelToBox("cup.y", cupY);
            addLabelToBox("gold.y", goldY);
            addLabelToBox("production.y", productionY);
            addLabelToBox("position", coordinates);
            addLabelToBox("c.Construction", construction);
            addLabelToBox("turns", remainingTurns);
            addLabelToBox("attached cities", attached);
        }
        for (City city : n) {
            addLabelToBox(city.getName(), names);
            addLabelToBox(String.valueOf(city.getCitizens().size()), population);
            addLabelToBox(String.valueOf(city.getCombatStrength()), PF);
            addLabelToBox(String.valueOf(city.getFoodYield()), foodY);
            addLabelToBox(String.valueOf(city.getCupYield()), cupY);
            addLabelToBox(String.valueOf(city.getGoldYield()), goldY);
            addLabelToBox(String.valueOf(city.getProductionYield()), productionY);
            addLabelToBox(city.getCapitalTile().getPosition().X + "," + city.getCapitalTile().getPosition().Y, coordinates);
            if(city.getCurrentConstruction() == null) {
                addLabelToBox("-", construction);
                addLabelToBox("-", remainingTurns);
            }
            else {
                addLabelToBox(String.valueOf(city.getCurrentConstruction()), construction);
                addLabelToBox(String.valueOf(city.getCurrentConstruction().getTurnTillBuild()), remainingTurns);
            }
            if (city.getState() == CityState.ATTACHED)
                addLabelToBox("attached", attached);

            else
                addLabelToBox("not attached", attached);
        }
        //coordinates
        setCoordinatesBox(list, names, 25, 60);
        setCoordinatesBox(list, population, 140, 60);
        setCoordinatesBox(list, PF, 240, 60);
        setCoordinatesBox(list, foodY, 280, 60);
        setCoordinatesBox(list, cupY, 350, 60);
        setCoordinatesBox(list, goldY, 420, 60);
        setCoordinatesBox(list, productionY, 480, 60);
        setCoordinatesBox(list, coordinates, 595, 60);
        setCoordinatesBox(list, construction, 670, 60);
        setCoordinatesBox(list, remainingTurns, 805, 60);
        setCoordinatesBox(list, attached, 895, 60);

        addLabelToBox("", productionY);
        addLabelToBox(infoCommands.searchCity.regex.substring(1), productionY);
        productionY.getChildren().get(productionY.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            pane.getChildren().remove(list);
            audioClip.play();
            showAllCities();
        });
        list.getChildren().add(exitButtonStyle());
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        pane.getChildren().add(list);
    }
    private void setCoordinatesBox(Pane list, VBox box, double x, double y) {
        list.getChildren().get(list.getChildren().indexOf(box)).setLayoutX(x);
        list.getChildren().get(list.getChildren().indexOf(box)).setLayoutY(y);
    }
    public void showNotifications(int listNumber)
    {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 400, 500, false);
        VBox box = new VBox();
        box.setSpacing(5);
        addLabelToPane("notification panel", list);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(145);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        ArrayList<Notification> tmp = commandHandler.getPlayer().getNotifications();
        ImageView rightArrow = new ImageView();
        ImageView leftArrow = new ImageView();
        try {
            rightArrow.setImage(new Image(String.valueOf(new URL(getClass().
                    getResource("photos/gameIcons/ArrowRight.png").toExternalForm()))));
            leftArrow.setImage(new Image(String.valueOf(new URL(getClass().
                    getResource("photos/gameIcons/ArrowRight.png").toExternalForm()))));
            leftArrow.setRotate(180);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        list.getChildren().addAll(rightArrow, leftArrow);
        list.getChildren().get(list.getChildren().indexOf(rightArrow)).setLayoutX(220);
        list.getChildren().get(list.getChildren().indexOf(leftArrow)).setLayoutX(165);
        list.getChildren().get(list.getChildren().indexOf(rightArrow)).setOnMousePressed(mouseEvent -> {
            audioClip.play();
            if(commandHandler.getPlayer().getNotifications().size() - listNumber > 4) {
                pane.getChildren().remove(list);
                showNotifications(listNumber + 4);
            }
        });
        list.getChildren().get(list.getChildren().indexOf(leftArrow)).setOnMousePressed(mouseEvent -> {
            audioClip.play();
            if(listNumber > 0) {
                pane.getChildren().remove(list);
                showNotifications(listNumber - 4);
            }
        });
        list.getChildren().get(list.getChildren().indexOf(rightArrow)).setLayoutY(400);
        list.getChildren().get(list.getChildren().indexOf(leftArrow)).setLayoutY(400);
        int number = tmp.size();
        if(number == 0)
            addLabelToBox(infoCommands.nothing.regex, box);
        for(int i = 0; i < 4; i++) {
            if(i + listNumber < tmp.size()) {
                addLabelToBox((i + listNumber + 1) + ":", box);
                addLabelToBox("        " + tmp.get(i + listNumber).getMessage(), box);
                addLabelToBox("        " + infoCommands.sendMessage.regex + tmp.get(i + listNumber).getSendingTurn(), box);
            }
        }
        list.getChildren().add(exitButtonStyle());
        box.setLayoutX(40);
        box.setLayoutY(40);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(5);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(5);
        list.setLayoutX(400);
        list.setLayoutY(75);
        list.getChildren().add(box);
        pane.getChildren().add(list);
    }
    public void showUnits()
    {
        audioClip.play();
        Pane box = new Pane();
        panelsPaneStyle(box, 600, 500, false);
        box.prefWidth(300);
        ArrayList<Unit> tmp = commandHandler.getPlayer().getUnits();
        VBox names = new VBox(), coordinates = new VBox(), unitState = new VBox();
        names.setSpacing(5);
        coordinates.setSpacing(5);
        unitState.setSpacing(5);
        box.getChildren().addAll(names, coordinates, unitState);
        names.setAlignment(Pos.CENTER);
        unitState.setAlignment(Pos.CENTER);
        int max = commandHandler.getPlayer().getUnits().size();
        if(max != 0) {
            Label label = new Label();
            labelStyle(label);
            label.setText("choose unit number to change active/inactive");
            box.getChildren().add(label);
            setCoordinates(box, 150, 10);
        }
        if(max != 0) {
            addLabelToBox("Type", names);
            addLabelToBox("coordinates", coordinates);
            addLabelToBox("unit state", unitState);
        }
        for (int i = 0; i < max; i++)
        {
            Unit unit = commandHandler.getPlayer().getUnits().get(i);
            addLabelToBox((i + 1) + ": " + unit.toString().toLowerCase(), names);
            addLabelToBox(unit.getTile().getPosition().X + "," + unit.getTile().getPosition().Y, coordinates);
            addLabelToBox(unit.getUnitState().symbol, unitState);
        }
        addLabelToBox((max + 1) + ": go to Military panel", names);
        addLabelToBox(" ", coordinates);
        addLabelToBox(" ", coordinates);
        //coordinates
        box.getChildren().get(box.getChildren().indexOf(names)).setLayoutX(40);
        box.getChildren().get(box.getChildren().indexOf(names)).setLayoutY(40);
        box.getChildren().get(box.getChildren().indexOf(coordinates)).setLayoutX(250);
        box.getChildren().get(box.getChildren().indexOf(coordinates)).setLayoutY(40);
        box.getChildren().get(box.getChildren().indexOf(unitState)).setLayoutX(450);
        box.getChildren().get(box.getChildren().indexOf(unitState)).setLayoutY(40);

        TextField textField = new TextField();
        textField.setPrefWidth(150);
        coordinates.getChildren().add(textField);
        box.getChildren().add(exitButtonStyle());
        setCoordinates(box, 20, 20);
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if (keyName.equals("Enter")) {
                if (isValidNumber(textField.getText())) {
                    int tmpNumber = Integer.parseInt(textField.getText());
                    if ((tmpNumber > max + 1 || tmpNumber < 1) && (coordinates.getChildren().get(coordinates.getChildren().size() - 1).getClass() == TextField.class)) {
                        addLabelToBox(mainCommands.pickBetween.regex + "1 and " + (max + 1), coordinates);
                    }
                    else if ((tmpNumber > max + 1 || tmpNumber < 1) && (coordinates.getChildren().get(coordinates.getChildren().size() - 1).getClass() == Label.class &&
                            !((Label) coordinates.getChildren().get(coordinates.getChildren().size() - 1)).getText().split(" ")[0].equals("please"))) {
                        coordinates.getChildren().remove(coordinates.getChildren().size() - 1);
                        addLabelToBox(mainCommands.pickBetween.regex + "1 and " + (max + 1), coordinates);
                    }
                    else if (tmpNumber <= max + 1) {
                        if (tmpNumber == max + 1) {
                            pane.getChildren().remove(box);
                            showMilitary(commandHandler.getPlayer());
                        }
                        else {
                            if (tmp.get(tmpNumber - 1).getUnitState().equals(UnitState.ACTIVE))
                                tmp.get(tmpNumber - 1).setUnitState(UnitState.SLEEPING);
                            else
                                tmp.get(tmpNumber - 1).setUnitState(UnitState.ACTIVE);
                            pane.getChildren().remove(box);
                            showUnits();
                        }
                    }
                }
                textField.setText(null);
            }
            });
        pane.getChildren().add(box);
    }
    private void setCoordinates(Pane box, double x, double y) {
        box.getChildren().get(box.getChildren().size() - 1).setLayoutX(x);
        box.getChildren().get(box.getChildren().size() - 1).setLayoutY(y);
    }
    private ImageView checkmarkButtonStyle(String type, Player player, AtomicReference<Double> number) {
        ImageView checkmark = new ImageView();
        checkmark.setOnMousePressed(mouseEvent -> {
            tradeOffered.play();
            for (int i = 0; i < 4; i++)
                pane.getChildren().remove(pane.getChildren().size() - 1);
            for(int i = 0; i < pane.getChildren().size(); i++)
                pane.getChildren().get(i).setDisable(false);
            selectedCoins = number.get();
            if(type.equals("buy"))
                player.getTradeRequests().add(new TradeRequest(commandHandler.getPlayer() ,String.format("%.0f", selectedCoins), selectedResource.getRESOURCE_TYPE().name()));
            else if(type.equals("sell"))
                player.getTradeRequests().add(new TradeRequest(commandHandler.getPlayer() ,selectedResource.getRESOURCE_TYPE().name(), String.format("%.0f", selectedCoins)));
            selectedResource = null;
            selectedCoins = 0.0;
        });
        try {
            checkmark.setImage(new Image(String.valueOf(new URL(getClass().getResource("photos/gameIcons/panelsIcons/Checkmark.png").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        checkmark.setFitHeight(25);
        checkmark.setFitWidth(25);
        pane.requestFocus();
        return checkmark;
    }
    private ImageView exitButtonStyle() {
        ImageView exitButton = new ImageView();
        exitButton.setOnMouseMoved(mouseEvent -> {
            exitButton.setFitHeight(28);
            exitButton.setFitWidth(28);
        });
        exitButton.setOnMouseExited(mouseEvent -> {
            exitButton.setFitHeight(25);
            exitButton.setFitWidth(25);
        });
        exitButton.setOnMousePressed(mouseEvent -> {
            audioClip.play();
            pane.getChildren().remove(pane.getChildren().size() - 1);
            for(int i = 0; i < pane.getChildren().size(); i++)
                pane.getChildren().get(i).setDisable(false);
        });
        try {
            exitButton.setImage(new Image(String.valueOf(new URL(getClass().getResource("photos/gameIcons/panelsIcons/Close.png").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        exitButton.setFitHeight(25);
        exitButton.setFitWidth(25);
        pane.requestFocus();
        return exitButton;
    }
    public void showMilitary(Player player)
    {
        VBox box = new VBox();
        panelsVboxStyle(box);
        if (player.getUnits().size() == 0)
            addLabelToBox("you have not any unit", box);
        else
            box.getChildren().add(showAllUnits(player));
        pane.getChildren().add(box);
    }
    private Pane showAllUnits(Player player)
    {
        Pane box = new Pane();
        panelsPaneStyle(box, 600, 500, false);
        int max = player.getUnits().size();
        VBox names = new VBox(), coordinates = new VBox(),
                power = new VBox(), MP = new VBox(), health = new VBox(), unitState = new VBox();
        names.setAlignment(Pos.CENTER);
        coordinates.setAlignment(Pos.CENTER);
        power.setAlignment(Pos.CENTER);
        MP.setAlignment(Pos.CENTER);
        health.setAlignment(Pos.CENTER);
        unitState.setAlignment(Pos.CENTER);
        if(max != 0) {
            addLabelToBox("Type", names);
            addLabelToBox("coordinates", coordinates);
            addLabelToBox("power", power);
            addLabelToBox("MP", MP);
            addLabelToBox("health", health);
            addLabelToBox("state", unitState);
        }
        for (int i = 0; i < max; i++)
        {
            Unit unit = player.getUnits().get(i);
            addLabelToBox(unit.toString().toLowerCase(), names);
            addLabelToBox(unit.getTile().getPosition().X + "," + unit.getTile().getPosition().Y, coordinates);
            addLabelToBox(String.valueOf(unit.getPower()), power);
            addLabelToBox(String.valueOf(unit.getMovementPoints()), MP);
            addLabelToBox(String.valueOf(unit.getHealth()), health);
            addLabelToBox(unit.getUnitState().symbol, unitState);
        }
        names.getChildren().add(new Label()); coordinates.getChildren().add(new Label());
        power.getChildren().add(new Label()); health.getChildren().add(new Label());
        MP.getChildren().add(new Label()); unitState.getChildren().add(new Label());
        box.getChildren().addAll(names, coordinates, power, MP, health, unitState);
        box.getChildren().get(box.getChildren().indexOf(names)).setLayoutX(40);
        box.getChildren().get(box.getChildren().indexOf(names)).setLayoutY(10);
        box.getChildren().get(box.getChildren().indexOf(coordinates)).setLayoutX(175);
        box.getChildren().get(box.getChildren().indexOf(coordinates)).setLayoutY(10);
        box.getChildren().get(box.getChildren().indexOf(power)).setLayoutX(300);
        box.getChildren().get(box.getChildren().indexOf(power)).setLayoutY(10);
        box.getChildren().get(box.getChildren().indexOf(MP)).setLayoutX(390);
        box.getChildren().get(box.getChildren().indexOf(MP)).setLayoutY(10);
        box.getChildren().get(box.getChildren().indexOf(health)).setLayoutX(450);
        box.getChildren().get(box.getChildren().indexOf(health)).setLayoutY(10);
        box.getChildren().get(box.getChildren().indexOf(unitState)).setLayoutX(530);
        box.getChildren().get(box.getChildren().indexOf(unitState)).setLayoutY(10);
        box.getChildren().add(exitButtonStyle());
        box.getChildren().get(box.getChildren().size() - 1).setLayoutX(15);
        box.getChildren().get(box.getChildren().size() - 1).setLayoutY(15);
        return box;
    }
    private void panelsPaneStyle(Pane list, double width, double height, boolean hasBackground) {
        list.setLayoutX(340);
        list.setLayoutY(180);
        ImageView imageView = new ImageView();
        try {
            if(hasBackground) {
                Rectangle background = new Rectangle();
                background.setWidth(2000);
                background.setHeight(1000);
                background.setStyle("-fx-fill: rgba(255,255,255,0.51)");
                list.getChildren().add(background);
                setCoordinates(list, -500, -250);
            }
            imageView.setImage(new Image(String.valueOf(new URL(getClass()
                    .getResource("photos/backgrounds/icons/frontGamePage.jpg").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        if(hasBackground)
            list.getChildren().add(1, imageView);
        else
            list.getChildren().add(0, imageView);
        imageView.setStyle("-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-pref-height: 500;");
        list.setPrefWidth(width);
    }
    private void showCity()
    {
        Pane list = new Pane();
        panelsPaneStyle2(list);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(6);
        City tmp = commandHandler.getPlayer().getSelectedCity();
        int flg = -1;
        for(int i = 0; i < Technology.values().length; i++)
            if(Technology.values()[i] == commandHandler.getPlayer().getResearchingTechnology()) flg = i;
        addLabelToBox(infoCommands.cityName.regex + tmp.getName(), box);
        addLabelToBox(gameEnum.foodYield.regex + tmp.getFoodYield(), box);
        addLabelToBox(gameEnum.production.regex + tmp.getProductionYield(), box);
        addLabelToBox(gameEnum.goldYield.regex + tmp.getGoldYield(), box);
        addLabelToBox(gameEnum.cupYield.regex + tmp.getCupYield(), box);
        addLabelToBox(infoCommands.size.regex + tmp.getTerritory().size(), box);
        addLabelToBox(gameEnum.population.regex + tmp.getCitizens().size(), box);
        addLabelToBox(gameEnum.power.regex + tmp.getCombatStrength(), box);
        if(flg > -1) {
            addLabelToBox(infoCommands.currentResearching.regex + commandHandler.getPlayer().
                    getResearchingTechnology().name(), box);
            addLabelToBox(infoCommands.remainingTurns.regex + (commandHandler.getPlayer().getResearchingTechnology().
                    cost - commandHandler.getPlayer().getResearchingTechCounter()[flg]), box);
        }
        else {
            addLabelToBox(infoCommands.currentResearching.regex + infoCommands.nothing.regex, box);
            addLabelToBox(infoCommands.remainingTurns.regex + "-", box);
        }
        addLabelToBox(gameEnum.employedCitizens.regex + (tmp.employedCitizens()), box);
        addLabelToBox(gameEnum.unEmployedCitizens.regex + (commandHandler.getPlayer().
                getTotalPopulation() - tmp.employedCitizens()), box);
        if(tmp.getCurrentConstruction() != null) {
            addLabelToBox(gameEnum.currentConstruction.regex + tmp.getCurrentConstruction().toString(), box);
            addLabelToBox(infoCommands.remainingTurns.regex + tmp.getCurrentConstruction().getTurnTillBuild(), box);
        }
        else
            addLabelToBox(gameEnum.currentConstruction.regex + infoCommands.nothing.regex, box);
        list.getChildren().add(box);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(175);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(exitButtonStyle());
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        pane.getChildren().add(list);
    }

    public void notifications(MouseEvent mouseEvent) {
        showNotifications(0);
    }
    public void showDemographic(Player tmp)
    {
        //define pane and box
        Pane list = new Pane();
        panelsPaneStyle(list, 1000, 500, false);
        list.setLayoutX(180);
        list.setLayoutY(100);
        VBox cities = printCities(tmp), information = new VBox(), players = new VBox();
        cities.setAlignment(Pos.TOP_LEFT);
        cities.setSpacing(5);
        information.setAlignment(Pos.TOP_LEFT);
        information.setSpacing(5);
        list.getChildren().addAll(cities, players, information);

        //exit button
        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);

        //civilization information and coordinates
        addLabelToBox("general information" , information);
        addLabelToPane(infoCommands.civilizationName.regex + tmp.getCivilization().name(), list);
        setCoordinates(list, 350, 10);
        setCoordinatesBox(list, cities, 40, 40);
        setCoordinatesBox(list, players, 500, 300);
        addLabelToBox(gameEnum.population.regex + tmp.getTotalPopulation(), information);
        addLabelToBox(gameEnum.happiness.regex + tmp.getHappiness(), information);
//        System.out.println(tmp.get); //TODO: resource
        addLabelToBox(gameEnum.food.regex + tmp.getFood(), information);
        addLabelToBox(gameEnum.cup.regex + tmp.getCup(), information);
        addLabelToBox(gameEnum.cupIncome.regex + tmp.incomeCup(), information);
        addLabelToBox(gameEnum.gold.regex + tmp.getGold(), information);
        addLabelToBox(gameEnum.goldIncome.regex + tmp.incomeGold(), information);
        setCoordinatesBox(list, information, 500, 40);

        //units
        addLabelToPane("Go to Military panel", list);
        setCoordinates(list, 40, 300);
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            audioClip.play();
            pane.getChildren().remove(list);
            showMilitary(tmp);
        });

        //other players
        addLabelToBox("you can also see the other players information\nchoose a civilization: ", players);
        int number = 0;
        ArrayList<Player> newArr = new ArrayList<>();
        // here
        for(Player player : commandHandler.getPlayers())
            if(!player.getCivilization().equals(commandHandler.getPlayer().getCivilization())) {
                addLabelToBox((number + 1) + ": " + player.getCivilization().name(), players);
                players.getChildren().get(players.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
                    audioClip.play();
                    pane.getChildren().remove(list);
                    showDemographic(player);
                });
                newArr.add(player);
                number++;
            }

        //score board
        addLabelToPane("Go to score board", list);
        setCoordinates(list, 40, 325);
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            audioClip.play();
            pane.getChildren().add(showScoreBoard());
            setCoordinates(pane, 500, 120);
            pane.getChildren().get(pane.getChildren().size() - 2).setDisable(true);
        });

        pane.getChildren().add(list);
    }
    public Pane showScoreBoard()
    {
        // here
        Pane list = new Pane();
        panelsPaneStyle(list, 300, 250, false);
        VBox box = new VBox();
        list.getChildren().add(box);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        setCoordinatesBox(list, box, 75, 10);
        addLabelToBox(infoCommands.scoreBoard.regex, box);
        ArrayList<Player> players = commandHandler.getPlayers();
        players.sort((o1, o2) -> {
            if (o1.getGameScore() == o2.getGameScore())
                return 0;
            return o1.getGameScore() < o2.getGameScore() ? -1 : 1;
        });
        int number = players.size();
        for(int i = number - 1; i >= 0; i--)
            addLabelToBox((number - i) + " - " + players.get(i).getCivilization().name().toLowerCase(Locale.ROOT)
                    + ": " +  players.get(i).getGameScore(), box);
        int avg = 0;
        for(Player player1 : players)
            avg += player1.getGameScore();
        addLabelToBox("----------------------", box);
        addLabelToBox(infoCommands.averageScore.regex + ((double) avg / players.size()), box);

        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        return list;
    }
    public void demographics(MouseEvent mouseEvent) {
        audioClip.play();
        showDemographic(commandHandler.getPlayer());
    }

    public void military(MouseEvent mouseEvent) {
        audioClip.play();
        showMilitary(commandHandler.getPlayer());
    }
    private void addButtonToBox(String text, VBox box) {
        Button button = new Button();
        button.setText(text);
        button.setStyle("-fx-border-color: white;" +
                "-fx-border-width: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color: #003675;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 20");
        button.setOnMouseMoved(mouseEvent -> button.setStyle("-fx-border-color: white;" +
                "-fx-border-width: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color: #00499f;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 20"));
        button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-border-color: white;" +
                "-fx-border-width: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-background-color: #003675;" +
                "-fx-text-fill: white;" +
                "-fx-font-size: 20"));
        box.getChildren().add(button);
    }
    private void saveGamePanel() {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 300, 300, false);
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setSpacing(8);
        for (int i = 0; i < 4; i++) {
            Button button = makeButtonLarge("save " + (i + 1));
            box.getChildren().add(button);
        }

        list.getChildren().add(box);
        setCoordinates(list, 100, 45);
        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        pane.getChildren().add(list);
        for (int i = 0; i < pane.getChildren().size() - 1; i++)
            pane.getChildren().get(i).setDisable(true);
    }
    public void options(MouseEvent mouseEvent) {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 300, 300, true);
        VBox box = new VBox();
        list.getChildren().add(box);
        box.setAlignment(Pos.CENTER);
        box.setSpacing(8);

        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(String.valueOf(new URL(getClass()
                    .getResource("photos/gameIcons/panelsIcons/Speaker.png").toExternalForm()))));
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        list.getChildren().add(imageView);
        setCoordinates(list, 250, 10);
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent12 -> {
            if(gameDemo.isPlaying())
                gameDemo.stop();
            else
                gameDemo.play();
        });
        addButtonToBox("resume", box);
        (box.getChildren().get(box.getChildren().size() - 1)).setOnMousePressed(
                mouseEvent1 -> {
                    pane.getChildren().remove(list);
                    for(Node node : pane.getChildren())
                        node.setDisable(false);
                    pane.requestFocus();
                });
        addButtonToBox("save game", box);
        (box.getChildren().get(box.getChildren().size() - 1)).setOnMousePressed(
                mouseEvent1 -> saveGamePanel());
        addButtonToBox("exit", box);
        (box.getChildren().get(box.getChildren().size() - 1)).setOnMousePressed(
                mouseEvent1 -> Platform.exit());
        setCoordinatesBox(list, box, 95, 60);
        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        list.getChildren().get(0).setOnMouseMoved(mouseEvent1 ->
                ((Node) mouseEvent1.getSource()).getScene().setCursor(Cursor.CROSSHAIR));
        list.getChildren().get(0).setOnMouseExited(mouseEvent1 ->
                ((Node) mouseEvent1.getSource()).getScene().setCursor(Cursor.DEFAULT));
        autoSaveButtonStyle(list);
        pane.getChildren().add(list);
        for(int i = 0; i < pane.getChildren().size() - 1; i++)
            pane.getChildren().get(i).setDisable(true);
        setCoordinates(pane, 490, 210);

    }

    private void autoSaveButtonStyle(Pane list) {
        Rectangle rectangle = new Rectangle();
        rectangle.setHeight(40);
        rectangle.setWidth(100);
        Label label = new Label();
        label.setStyle("-fx-text-fill: white");
        label.setText("auto save");
        if(isAutoSaveOn)
            rectangle.setStyle("-fx-fill: green");
        else
            rectangle.setStyle("-fx-fill: red");

        rectangle.setOnMouseClicked(mouseEvent -> {
            if(isAutoSaveOn)
                rectangle.setStyle("-fx-fill: red");
            else
                rectangle.setStyle("-fx-fill: green");
            isAutoSaveOn = !isAutoSaveOn;
        });
        list.getChildren().add(rectangle);
        setCoordinates(list, 10, 250);
        list.getChildren().add(label);
        setCoordinates(list, 17, 258);
    }
    public void technologyTree() {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 1300, 350, false);
        list.setLayoutX(-10);
        list.setLayoutY(150);
        ArrayList<Technology> technologies = new ArrayList<>();
        technologies.add(Technology.AGRICULTURE);
        addLabelTechnologyTree(technologies, list, 4);
        int state = 1;
        while (true) {
            ArrayList<Technology> tmp = new ArrayList<>();
            for (Technology technology : Technology.values())
                if (technologies.containsAll(technology.requiredTechnologies) &&
                        !technologies.contains(technology))
                    tmp.add(technology);
            addLabelTechnologyTree(tmp, list, ((7 - tmp.size()) / 2) + 1);
            technologies.addAll(tmp);
            if(tmp.size() == 0)
                break;
            setCoordinates(list ,state * 300 + 10, 10);
            state++;
        }
        addLabelToPane("technology tree", list);
        setCoordinates(list, 15, 225);
        addLabelToPane("red: gained technologies", list);
        setCoordinates(list, 15, 250);
        addLabelToPane("green: researching technology", list);
        setCoordinates(list, 15, 275);
        addLabelToPane("blue: others", list);
        setCoordinates(list, 15, 300);
        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 20, 10);
        list.setOnScroll((ScrollEvent event) -> {
            double xScale = 30;
            double deltaX = event.getDeltaX();
            if (deltaX < 0)
                xScale *= -1;
            if((list.getChildren().get(list.getChildren().size() - 7).getLayoutX() > 1150 && xScale < 0) || list.getChildren().get(1).getLayoutX() < 25 && xScale > 0) {
                for(int i = 1; i < list.getChildren().size() - 1; i++)
                    list.getChildren().get(i).setLayoutX(list.getChildren().get(i).getLayoutX() + xScale);
            }
        });
        //swap
        swap(((VBox) list.getChildren().get(3)), 1, 5);
        swap(((VBox) list.getChildren().get(3)), 2, 4);
        swap(((VBox) list.getChildren().get(3)), 3, 5);
        swap(((VBox) list.getChildren().get(3)), 4, 5);
        swap(((VBox) list.getChildren().get(4)), 2, 5);
        swap(((VBox) list.getChildren().get(5)), 2, 3);
        swap(((VBox) list.getChildren().get(5)), 3, 4);
        swap(((VBox) list.getChildren().get(5)), 4, 5);
        swap(((VBox) list.getChildren().get(5)), 5, 6);
        swap(((VBox) list.getChildren().get(6)), 2, 5);
        swap(((VBox) list.getChildren().get(6)), 3, 4);
        swap(((VBox) list.getChildren().get(6)), 4, 5);
        swap(((VBox) list.getChildren().get(7)), 2, 4);
        swap(((VBox) list.getChildren().get(7)), 3, 5);
        swap(((VBox) list.getChildren().get(7)), 4, 5);
        swap(((VBox) list.getChildren().get(8)), 2, 5);
        swap(((VBox) list.getChildren().get(9)), 3, 4);
        swap(((VBox) list.getChildren().get(9)), 2, 3);
        swap(((VBox) list.getChildren().get(11)), 3, 5);

        //arrows
        arrowTechnologyTree(list, 1, 150, 140, 310, 80);
        arrowTechnologyTree(list, 1, 150, 135, 310, 135);
        arrowTechnologyTree(list, 1, 150, 160, 310, 200);
        arrowTechnologyTree(list, 1, 150, 170, 310, 260);

        arrowTechnologyTree(list, 2, 460, 110, 600, 50);
        arrowTechnologyTree(list, 2, 460, 90, 600, 90);
        arrowTechnologyTree(list, 2, 390, 200, 600, 140);
        arrowTechnologyTree(list, 2, 390, 185, 600, 185);
        arrowTechnologyTree(list, 2, 420, 235, 600, 235);
        arrowTechnologyTree(list, 2, 420, 235, 600, 305);

        arrowTechnologyTree(list, 3, 700, 85, 900, 85);
        arrowTechnologyTree(list, 3, 700, 85, 900, 160);
        arrowTechnologyTree(list, 3, 700, 135, 900, 210);
        arrowTechnologyTree(list, 3, 700, 185, 900, 260);
        arrowTechnologyTree(list, 3, 700, 270, 900, 270);

        arrowTechnologyTree(list, 4, 1000, 85, 1230, 85);
        arrowTechnologyTree(list, 4, 1000, 85, 1200, 160);
        arrowTechnologyTree(list, 4, 1000, 185, 1230, 185);
        arrowTechnologyTree(list, 4, 1050, 250, 1200, 130);
        arrowTechnologyTree(list, 4, 950, 300, 1200, 230);
        arrowTechnologyTree(list, 4, 1000, 270, 1200, 270);

        arrowTechnologyTree(list, 5, 1300, 160, 1500, 80);
        arrowTechnologyTree(list, 5, 1300, 135, 1540, 135);
        arrowTechnologyTree(list, 5, 1380, 200, 1500, 80);
        arrowTechnologyTree(list, 5, 1300, 65, 1510, 230);
        arrowTechnologyTree(list, 5, 1300, 230, 1540, 230);
        arrowTechnologyTree(list, 5, 1300, 300, 1510, 175);
        arrowTechnologyTree(list, 5, 1300, 165, 1510, 330);

        arrowTechnologyTree(list, 6, 1600, 85, 1800, 85);
        arrowTechnologyTree(list, 6, 1570, 330, 1800, 50); //bad shape
        arrowTechnologyTree(list, 6, 1600, 85, 1800, 155);
        arrowTechnologyTree(list, 6, 1600, 135, 1840, 135);
        arrowTechnologyTree(list, 6, 1600, 175, 1820, 175);
        arrowTechnologyTree(list, 6, 1600, 245, 1800, 185);
        arrowTechnologyTree(list, 6, 1600, 220, 1800, 220);

        arrowTechnologyTree(list, 7, 1900, 85, 2130, 85);
        arrowTechnologyTree(list, 7, 1900, 85, 2100, 150);
        arrowTechnologyTree(list, 7, 1900, 175, 2130, 175);
        arrowTechnologyTree(list, 7, 1900, 135, 2100, 185);
        arrowTechnologyTree(list, 7, 1900, 220, 2110, 220);
        arrowTechnologyTree(list, 7, 1900, 220, 2110, 300);

        arrowTechnologyTree(list, 8, 2230, 85, 2410, 85);
        arrowTechnologyTree(list, 8, 2230, 135, 2410, 135);
        arrowTechnologyTree(list, 8, 2230, 175, 2410, 175);
        arrowTechnologyTree(list, 8, 2230, 225, 2410, 185);
        arrowTechnologyTree(list, 8, 2230, 225, 2410, 225);
        arrowTechnologyTree(list, 8, 2230, 275, 2410, 240);

        arrowTechnologyTree(list, 9, 2570, 175, 2710, 120);
        arrowTechnologyTree(list, 9, 2570, 175, 2710, 175);

        arrowTechnologyTree(list, 10, 2875, 175, 3000, 120);
        arrowTechnologyTree(list, 10, 2875, 165, 3000, 165);
        arrowTechnologyTree(list, 10, 2875, 175, 3000, 220);

        arrowTechnologyTree(list, 11, 3200, 120, 3310, 120);
        arrowTechnologyTree(list, 11, 3100, 220, 3310, 160);
        arrowTechnologyTree(list, 11, 3100, 220, 3310, 220);
         pane.getChildren().add(list);
    }
    private void addLabelTechnologyTree(ArrayList<Technology> technologies, Pane list, int space) {
        VBox box = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        box.setSpacing(12);
        for (int i = 0; i < space; i++)
            addLabelToBox("", box);
        for(Technology technology : technologies) {
            Label label = new Label();
            label.setText(String.valueOf(technology));
            if(commandHandler.getPlayer().getResearchingTechnology() == technology) {
                label.setStyle("-fx-background-color: #00ff3c;" +
                        "-fx-text-fill: #560000;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 7");
            }
            else if(commandHandler.getPlayer().getTechnologies().contains(technology)) {
                label.setStyle("-fx-background-color: #ff0032;" +
                        "-fx-text-fill: #560000;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 7");
            }
            else {
                label.setStyle("-fx-background-color: #00eaff;" +
                        "-fx-text-fill: #000062;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 7");
            }
            box.getChildren().add(label);
        }
        list.getChildren().add(box);
    }
    private void arrowTechnologyTree(Pane list, int index, double x1, double y1, double x2, double y2) {
        //make image view & set width and height
        ImageView imageView = new ImageView();
        try {
            if(y2 > y1) {
                imageView.setImage(new Image(String.valueOf(new URL(getClass()
                        .getResource("photos/gameIcons/panelsIcons/treeArrowDown.png").toExternalForm()))));
                imageView.setFitHeight(y2 - y1);
                imageView.setY(y1);
            }
            else if(y1 == y2) {
                imageView.setImage(new Image(String.valueOf(new URL(getClass()
                        .getResource("photos/gameIcons/panelsIcons/treeArrowStraight.png").toExternalForm()))));
                imageView.setFitHeight(20);
                imageView.setY(y1);
            }
            else {
                imageView.setImage(new Image(String.valueOf(new URL(getClass()
                        .getResource("photos/gameIcons/panelsIcons/treeArrowUp.png").toExternalForm()))));
                imageView.setFitHeight(y1 - y2);
                imageView.setY(y2);
            }
            imageView.setX(x1);
            imageView.setFitWidth(x2 - x1);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        list.getChildren().add(index, imageView);
    }
    private void swap(VBox box, int a, int b) {
        Label labelA = (Label) box.getChildren().get(a);
        Label labelB = (Label) box.getChildren().get(b);
        box.getChildren().remove(b);
        box.getChildren().remove(a);
        box.getChildren().add(a, labelB);
        box.getChildren().add(b, labelA);
    }

    private void showDiplomacy() {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 1040, 500, false);
        list.setLayoutX(100);
        list.setLayoutY(110);
        VBox civilizations = new VBox(), score = new VBox(), names = new VBox(), rulers = new VBox(), relationState = new VBox(),
                trade = new VBox(), chat = new VBox(), declareWar = new VBox(), makePeace = new VBox();
        list.getChildren().addAll(civilizations, score, names, rulers, relationState, trade, chat, declareWar, makePeace);
        for(int i = 0; i < 9; i++) {
            ((VBox) list.getChildren().get(list.getChildren().size() - 1 - i)).setSpacing(5);
            ((VBox) list.getChildren().get(list.getChildren().size() - 1 - i)).setAlignment(Pos.CENTER);
        }

        ArrayList<Player> players = new ArrayList<>();
        // here
        for (Player player : commandHandler.getPlayers())
            if(!player.getCivilization().equals(commandHandler.getPlayer().getCivilization()))
                players.add(player);
        //information
        if(players.size() != 0) {
            addLabelToBox("civilization", civilizations);
            addLabelToBox("score", score);
            addLabelToBox("leader name" , names);
            addLabelToBox("ruler name" , rulers);
            addLabelToBox("declare war" , declareWar);
            addLabelToBox("peace" , makePeace);
            addLabelToBox("relation", relationState);
            addLabelToBox("trade", trade);
            addLabelToBox("chat", chat);
        }
        for (Player player : players) {
            addLabelToBox(player.getCivilization().name(), civilizations);
            addLabelToBox(String.valueOf(player.getGameScore()), score);
            addLabelToBox(player.getCivilization().leaderName, names);
            addLabelToBox(player.getUsername(), rulers);

            declareWar.getChildren().add(makeButton("war"));
            declareWar.getChildren().get(declareWar.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                player.getRelationStates().replace(commandHandler.getPlayer().getCivilization(), RelationState.ENEMY);
                commandHandler.getPlayer().getRelationStates().replace(player.getCivilization(), RelationState.ENEMY);
            });
            makePeace.getChildren().add(makeButton("peace"));
            makePeace.getChildren().get(declareWar.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> player.getTradeRequests().add(new TradeRequest(commandHandler.getPlayer() ,"peace", "peace")));
            addLabelToBox(player.getRelationStates().
                    get(commandHandler.getPlayer().getCivilization()).name(), relationState);
            trade.getChildren().add(makeButton("trade"));
            trade.getChildren().get(trade.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                pane.getChildren().add(tradePanel(player));
                for (int i = 0; i < pane.getChildren().size() - 1; i++)
                    pane.getChildren().get(i).setDisable(true);
            });
            chat.getChildren().add(makeButton("chat"));
            chat.getChildren().get(chat.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                ChatMenu chatMenu = new ChatMenu();
                //ChatMenu.sender = registerController.getUserByUsername(this.player.getUsername());
                ChatMenu.receiver = player;
                try {
                    chatMenu.start(new Stage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        //coordinates
        setCoordinatesBox(list, civilizations, 25, 60);
        setCoordinatesBox(list, score, 135, 60);
        setCoordinatesBox(list, names, 230, 60);
        setCoordinatesBox(list, rulers, 400, 60);
        setCoordinatesBox(list, relationState, 535, 60);
        setCoordinatesBox(list, declareWar, 670, 60);
        setCoordinatesBox(list, makePeace, 780, 60);
        setCoordinatesBox(list, trade, 870, 60);
        setCoordinatesBox(list, chat, 940, 60);

        //requests
        VBox requests = new VBox();
        list.getChildren().add(requests);
        setCoordinates(list, 15, 250);
        requests.setSpacing(5);
        requests.setAlignment(Pos.TOP_LEFT);
        for (TradeRequest request : commandHandler.getPlayer().getTradeRequests()) {
            Label label = new Label();
            if (request.getOfferToSell().equals("peace")) {
                label.setText(request.getSender().getUsername() + " want's to make peace:)");
            }
            else {
                label.setText(request.getSender().getUsername() + ": \n    you'll get: " + request.getOfferToSell() +
                        ": \n    you'll sell: " + request.getWantToBuy());
            }
            labelStyle(label);
            requests.getChildren().add(label);
            requests.getChildren().get(requests.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                Pane yesOrNo = new Pane();
                panelsPaneStyle(yesOrNo, 200, 100, false);
                pane.getChildren().add(yesOrNo);
                setCoordinates(pane, 540, 310);
                Button yes = new Button();
                yesOrNo.getChildren().add(yes);
                setCoordinates(yesOrNo, 25, 30);
                yes.setText("yes");
                yes.setStyle("-fx-background-color: green;" +
                        "-fx-font-size: 17;" +
                        "-fx-text-fill: white;" +
                        "-fx-pref-width: 50;" +
                        "-fx-pref-height: 30");
                yes.setOnMouseClicked(mouseEvent12 -> {
                    tradeAccepted.play();
                    pane.getChildren().remove(yesOrNo);
                    requests.getChildren().remove(label);
                    commandHandler.getPlayer().getTradeRequests().remove(request);
                    list.getChildren().remove(requests);
                    list.getChildren().add(requests);
                    setCoordinates(list, 15, 250);
                    commandHandler.getPlayer().checkRequests(request);
                    list.getChildren().remove(relationState);
                    list.getChildren().add(relationState);
                    setCoordinatesBox(list, relationState, 535, 60);
                    if (commandHandler.getPlayer().getTradeRequests().size() == 0)
                        pane.getChildren().remove(diplomacyAlert);
                    setInformationStyles();
                    pane.requestFocus();
                });
                Button no = new Button();
                yesOrNo.getChildren().add(no);
                setCoordinates(yesOrNo, 125, 30);
                no.setText("no");
                no.setStyle("-fx-background-color: red;" +
                        "-fx-font-size: 17;" +
                        "-fx-text-fill: white;" +
                        "-fx-pref-width: 50;" +
                        "-fx-pref-height: 30");
                no.setOnMouseClicked(mouseEvent1 -> {
                    pane.getChildren().remove(yesOrNo);
                    requests.getChildren().remove(label);
                    commandHandler.getPlayer().getTradeRequests().remove(request);
                    list.getChildren().remove(requests);
                    list.getChildren().add(requests);
                    setCoordinates(list, 15, 250);
                    if (commandHandler.getPlayer().getTradeRequests().size() == 0)
                        pane.getChildren().remove(diplomacyAlert);
                });
            });

        }
        list.getChildren().add(exitButtonStyle());
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        pane.getChildren().add(list);
    }
    private void updateCoordinates(Rectangle rectangle1, Rectangle rectangle2,
                                   double length, Pane list) {

        selectedCoordinates(rectangle1, length);
        nonSelectedCoordinates(rectangle2, length, list);
    }
    private void selectedCoordinates(Rectangle rectangle, double length) {
        rectangle.setWidth(length + 110);
    }
    private void nonSelectedCoordinates(Rectangle rectangle, double length, Pane list) {
        list.getChildren().get(list.getChildren().indexOf(rectangle)).
                setLayoutX(length + 125);
        rectangle.setWidth(108 - length);
    }
    private int getGoldOffer(String type, Player player) {
        Pane list = new Pane();
        panelsPaneStyle(list, 300, 75, false);
        list.setLayoutX(800);
        list.setLayoutY(500);
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(String.valueOf(new URL(getClass()
                    .getResource("photos/gameIcons/Gold.png").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(35);
        imageView.setFitHeight(35);

        Rectangle selectedAmount = new Rectangle();
        selectedAmount.setWidth(200);
        selectedAmount.setHeight(10);
        Rectangle nonSelectedAmount = new Rectangle();
        nonSelectedAmount.setWidth(200);
        nonSelectedAmount.setHeight(10);

        selectedAmount.setStyle("-fx-arc-width: 60;" +
                "-fx-arc-height: 60;" +
                "-fx-fill: #d0a708");
        nonSelectedAmount.setStyle("-fx-arc-width: 60;" +
                "-fx-arc-height: 60;" +
                "-fx-fill: #ffffff");


        list.getChildren().add(selectedAmount);
        setCoordinates(list, 50, 30);
        list.getChildren().add(nonSelectedAmount);
        setCoordinates(list, 50, 30);
        list.getChildren().add(imageView);
        setCoordinates(list, 125, 20);
        AtomicReference<Double> number = new AtomicReference<>((double) 52.0);
        addLabelToPane(String.format("%.0f", number.get()), list);
        setCoordinates(list, 265, 25);
        updateCoordinates(selectedAmount, nonSelectedAmount, 5, list);

        imageView.addEventHandler(MouseEvent.MOUSE_DRAGGED, event -> {
            if (event.getX() > -91 && event.getX() < 108) {
                imageView.setX(event.getX());
                updateCoordinates(selectedAmount, nonSelectedAmount, event.getX(), list);
                number.set((100 * (event.getX() + 91)) / 199.0);
                list.getChildren().remove(list.getChildren().size() - 1);
                addLabelToPane(String.format("%.0f", number.get()), list);
                setCoordinates(list, 265, 25);
            }
        });

        list.getChildren().add(list.getChildren().size() - 1, checkmarkButtonStyle(type, player, number));
        pane.getChildren().add(list);
        return 0;
    }
    private Pane resourcePanel(String type, Player player) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 350, false);

        list.setLayoutX(500);
        list.setLayoutY(250);
        ArrayList<Resource> resources;
        if (type.equals("buy"))
            resources = player.getResources();
        else
            resources = commandHandler.getPlayer().getResources();
        int flag = 0;
        while (flag < resources.size()){
            VBox names = new VBox();
            names.setSpacing(7);
            names.setAlignment(Pos.CENTER);

            for (int i = 0; i < 7; i++)
                if (i + flag < resources.size()) {
                    addLabelToBox(resources.get(i + flag).getRESOURCE_TYPE().name(), names);
                    int finalI = i;
                    int finalFlag = flag;
                    names.getChildren().get(names.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                        selectedResource = resources.get(finalI + finalFlag);
                        getGoldOffer(type, player);
                    });
                }

            list.getChildren().add(names);
            setCoordinates(list, 15 + (flag / 7.0) * 100, 50);
            flag += 7;

        }

        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        return list;
    }
    private Pane tradePanel(Player player) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 350, false);
        addLabelToPane("you: " + commandHandler.getPlayer().getUsername(), list);
        setCoordinates(list, 50, 10);
        addLabelToPane("receiver: " + player.getUsername(), list);
        setCoordinates(list, 350, 10);
        addLabelToPane("what do you want?", list);
        setCoordinates(list, 170, 65);
        addLabelToPane("buy resource", list);
        list.getChildren().get(list.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
            pane.getChildren().add(resourcePanel("buy", player));
            for (int i = 0; i < pane.getChildren().size() - 1; i++)
                pane.getChildren().get(i).setDisable(true);
        });
        setCoordinates(list, 100, 100);
        addLabelToPane("sell resource", list);
        list.getChildren().get(list.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
            pane.getChildren().add(resourcePanel("sell", player));
            for (int i = 0; i < pane.getChildren().size() - 1; i++)
                pane.getChildren().get(i).setDisable(true);
        });
        setCoordinates(list, 300, 100);
        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        return list;
    }
    private Button makeButtonLarge(String text) {
        Button button = new Button();
        button.setText(text);
        button.setPrefWidth(100);
        button.setPrefHeight(40);
        button.setStyle("-fx-background-color: #ffcc00;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: #000000;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 20;");
        button.setOnMouseMoved(mouseEvent -> button.setStyle("-fx-background-color: #c8ff00;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: #000000;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 20;"));
        button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-background-color: #ffcc00;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: #000000;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 20;"));
        return button;
    }
    private Button makeButton(String text) {
        Button button = new Button();
        button.setText(text);
        button.setPrefWidth(45);
        button.setPrefHeight(15);
        button.setStyle("-fx-background-color: #ffcc00;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: #000000;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 10;");
        button.setOnMouseMoved(mouseEvent -> button.setStyle("-fx-background-color: #c8ff00;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: #000000;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 10;"));
        button.setOnMouseExited(mouseEvent -> button.setStyle("-fx-background-color: #ffcc00;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: #000000;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;" +
                "-fx-font-size: 10;"));
        return button;
    }
    public void showDiplomacy(MouseEvent mouseEvent) {
        showDiplomacy();
    }
}
