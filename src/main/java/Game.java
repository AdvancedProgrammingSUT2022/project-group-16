import Controllers.GameController;
import Models.City.City;
import Models.City.CityState;
import Models.Player.Notification;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.Hex;
import Models.Terrain.Position;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.MidRange;
import Models.Units.CombatUnits.MidRangeType;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.Unit;
import Models.Units.UnitState;
import enums.cheatCode;
import enums.gameCommands.infoCommands;
import enums.gameEnum;
import enums.mainCommands;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;

public class Game extends Application {
    private Hex[][] hexagons;
    private final GameController gameController = GameController.getInstance();
    ArrayList<Hex> playerTurnTiles = new ArrayList<>();
    private boolean needUpdateScience = false;
    private boolean needUpdateProduction = true;
    public AudioClip audioClip = new AudioClip(Game.class.getResource("audio/gameAudios/click.mp3").toExternalForm());
    private final AudioClip gameDemo = new AudioClip(Game.class.getResource("audio/2.mp3").toExternalForm());
    private boolean isCPressed = false;
    private boolean isShiftPressed = false;
    @FXML
    public Pane pane;
    @Override

    public void start(Stage stage) throws Exception {
//        gameDemo.play();
        Pane root = FXMLLoader.load(new URL(getClass().getResource("fxml/game.fxml").toExternalForm()));
        stage.setScene(new Scene(root));
        root.requestFocus();
        stage.show();
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
                    gameController.increaseGold(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseTurns)) != null) //Almost done
                    gameController.increaseTurns(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.gainFood)) != null)
                    gameController.increaseFood(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.gainTechnology)) != null)
                    gameController.addTechnology(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseHappiness)) != null)
                    gameController.increaseHappiness(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.killEnemyUnit)) != null)
                    gameController.killEnemyUnit(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.moveUnit)) != null)
                    gameController.moveUnit(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseHealth)) != null)
                    gameController.increaseHealth(matcher);
                else if((matcher = cheatCode.compareRegex(command, cheatCode.increaseScore)) != null)
                    gameController.increaseScore(matcher);
                else if(cheatCode.compareRegex(command, cheatCode.winGame) != null) {
                    gameController.removeAllPlayers();
                    gameController.winGame();
                    Platform.exit();
                }
                else if(cheatCode.compareRegex(command, cheatCode.gainBonusResource) != null)
                    gameController.gainBonusResourceCheat();
                else if(cheatCode.compareRegex(command, cheatCode.gainStrategicResource) != null)
                    gameController.gainStrategicResourceCheat();
                else if(cheatCode.compareRegex(command, cheatCode.gainLuxuryResource) != null)
                    gameController.gainLuxuryResourceCheat();
                else if(command.equals("a")) {
                    ((Settler) gameController.getPlayerTurn().getUnits().get(1)).createCity();
                    gameController.getPlayerTurn().getCities().get(0).addPopulation(4);
                }
                pane.getChildren().remove(textField);
                setInformationStyles();
                pane.requestFocus();
            }
        });
    }
    public void initialize() {
        gameController.initGame();
        Hex.setPane(pane);
        int x = 100;
        hexagons = new Hex[GameController.getInstance().MAP_SIZE][GameController.getInstance().MAP_SIZE];
        for(int i = 0; i < gameController.MAP_SIZE; i++)
        {
            int y = (i % 2 == 0 ? 45 : 90);
            for(int j = 0; j < gameController.MAP_SIZE ; j++){
                hexagons[j][i] = new Hex(new Position(x, y), gameController);
                y += 100;
            }
            x += 90;
        }
        generateMapForPlayer(gameController.getPlayerTurn());

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

        setInformationStyles();
        pane.getChildren().get(11).setOnMousePressed(mouseEvent -> {
            showTechnologies();
            audioClip.play();
        });

        //cheatCode shortcut
        //TODO: do not remove this part :))))
//        ((Settler) gameController.getPlayerTurn().getUnits().get(1)).createCity();
//        gameController.getPlayerTurn().addTechnology(Technology.AGRICULTURE);
//        gameController.getPlayerTurn().addTechnology(Technology.ARCHERY);
//        gameController.getPlayerTurn().addTechnology(Technology.POTTERY);
//        gameController.getPlayerTurn().setCup(10);
//        new City(gameController.getMap().get(55), gameController.getPlayerTurn());
//        new City(gameController.getMap().get(45), gameController.getPlayerTurn());
//        new City(gameController.getMap().get(78), gameController.getPlayerTurn());
//        gameController.getPlayerTurn().getCities().get(0).addPopulation(4);
//        gameController.getPlayerTurn().getCities().get(1).addPopulation(7);
//        gameController.getPlayerTurn().getCities().get(2).addPopulation(3);
//        new MidRange(gameController.getPlayerTurn(), MidRangeType.CAVALRY, gameController.getMap().get(44));
//        new MidRange(gameController.getPlayerTurn(), MidRangeType.HORSEMAN, gameController.getMap().get(23));
//        new MidRange(gameController.getPlayerTurn(), MidRangeType.LSWORDSMAN, gameController.getMap().get(11));
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be dutchman");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be in zendegi");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "dorood bar lotfian");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be ap");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be seyyed");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be SNP");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be ap");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "dorood bar group 16");
//        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "bazam lanat be ap");
//        gameController.getPlayerTurn().getTechnologies().add(Technology.MILITARY_SCIENCE);
//        gameController.getPlayerTurn().getTechnologies().add(Technology.BRONZE_WORKING);
//        gameController.getPlayerTurn().setResearchingTechnology(Technology.THE_WHEEL);
//        gameController.getPlayerTurn().setCup(100);

    }
    //TODO when the turn changes delete playerTurnTiles from pane
    public void generateMapForPlayer(Player player){
        for (Tile tile : player.getMap().keySet()) {
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileState(player.getMap().get(tile));
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTile(tile);
            playerTurnTiles.add(hexagons[tile.getPosition().X][tile.getPosition().Y]);
        }
        playerTurnTiles.forEach(Hex::addHex);
    }
    public void changeTurn(MouseEvent mouseEvent) {
        for(int i= 0; i < GameController.getInstance().MAP_SIZE; i++){
            for(int j = 0; j < GameController.getInstance().MAP_SIZE; j++){
                hexagons[i][j].removeHex();
            }
        }
        playerTurnTiles.clear();
        gameController.checkChangeTurn(); //TODO: fix bugs
        generateMapForPlayer(gameController.getPlayerTurn());
        setInformationStyles();
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
        label.setText(gameController.showResearch());
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
        if(gameController.getPlayerTurn().getCities().size() == 0) {
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
            for (City city : gameController.getPlayerTurn().getCities()) {
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
                ((Label) information.getChildren().get(0)).setText(gameController.showResearch());
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
        setHoverForInformationTitles((ImageView) pane.getChildren().get(6), informationVbox(String.valueOf(gameController.getPlayerTurn().getGold()), 12));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(7), productionInformationStyle());
        setHoverForInformationTitles((ImageView) pane.getChildren().get(8), informationVbox(String.valueOf(gameController.getPlayerTurn().getFood()), 14));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(9), informationVbox(String.valueOf(gameController.getPlayerTurn().getPopulation()), 15));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(10), informationVbox(String.valueOf(gameController.getPlayerTurn().getHappiness()), 16));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(11), scienceInformationStyle());
        setHoverForInformationTitles((ImageView) pane.getChildren().get(13), panelsVbox("cities", 20));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(15), panelsVbox("units", 75));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(17), panelsVbox("military", 130));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(19), panelsVbox("demographics", 185));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(21), panelsVbox("notifications", 240));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(23), panelsVbox("economics", 295));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(26), informationVbox("menu", 24));
        setHoverForInformationTitles((ImageView) pane.getChildren().get(28), informationVbox("Technology Tree", 18));
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showGainedTechnologies(VBox box)
    {
        addLabelToBox(infoCommands.gained.regex, box);
        ArrayList<Technology> tmp = gameController.getPlayerTurn().getTechnologies();
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
        Pane list = new Pane();
        panelsPaneStyle(list, 350, 800,false);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 15, 15);
        addLabelToBox(infoCommands.numberOfCup.regex + gameController.getPlayerTurn().getCup(), box);
        showGainedTechnologies(box);
        addLabelToBox(infoCommands.chooseTechnology.regex, box);
        int max = 0, flag = -1;
        Player tmp = gameController.getPlayerTurn();
        ArrayList<Technology> candidateTechs = new ArrayList<>();
        for(int i = 0; i < Technology.values().length; i++)
            if(tmp.getTechnologies().containsAll(Technology.values()[i].requiredTechnologies) &&
                    !tmp.getTechnologies().contains(Technology.values()[i]))
            {
                if(tmp.getResearchingTechnology() != null &&
                        Technology.values()[i].equals(tmp.getResearchingTechnology()))
                {
                    addLabelToBox((max + 1) + ": " + Technology.values()[i].toString() +
                            infoCommands.currResearch.regex, box);
                    flag = max + 1;
                }
                else
                {
                    addLabelToBox((max + 1) + ": " + Technology.values()[i].toString() + infoCommands.requiredTurns.regex +
                            (Technology.values()[i].cost / 10 - tmp.getResearchingTechCounter()[i]), box);
                    if(gameController.requiredTechForBuilding(Technology.values()[i]) != null)
                        addLabelToBox(infoCommands.willGain.regex + gameController.
                                requiredTechForBuilding(Technology.values()[i]).name(), box);
                    if(gameController.requiredTechForImprovement(Technology.values()[i]) != null)
                        addLabelToBox(infoCommands.willGain.regex + gameController.
                                requiredTechForImprovement(Technology.values()[i]).name(), box);
                }
                candidateTechs.add(Technology.values()[i]);
                max++;
            }
        addLabelToBox((max + 1) + infoCommands.backToGame.regex, box);
        pane.getChildren().add(list);
        setCoordinates(pane, 465, -1);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        int finalMax = max;
        int finalFlag = flag;
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                if(isValidNumber(textField.getText())) {
                    int number = Integer.parseInt(textField.getText());
                    if((number > finalMax + 1 || number == 0)&& (box.getChildren().get(box.getChildren().size() - 1).getClass() == TextField.class))
                        addLabelToBox(mainCommands.pickBetween.regex + "1 and " + (finalMax + 1), box);
                    else if((number > finalMax + 1 || number == 0)&& (box.getChildren().get(box.getChildren().size() - 1).getClass() == Label.class &&
                            !((Label) box.getChildren().get(box.getChildren().size() - 1)).getText().split(" ")[0].equals("please"))) {
                        box.getChildren().remove(box.getChildren().size() - 1);
                        addLabelToBox(mainCommands.pickBetween.regex + "1 and " + (finalMax + 1), box);
                    }
                    else if(number <= finalMax + 1)
                    {
                        if(box.getChildren().indexOf(textField) != box.getChildren().size() - 1)
                            box.getChildren().remove(box.getChildren().size() - 1);
                        int flg = -1;
                        if(number != finalMax + 1)
                            for(int i = 0; i < Technology.values().length; i++)
                                if(Technology.values()[i] == candidateTechs.get(number - 1)) flg = i;
                        if(number == finalFlag) {
                            addLabelToBox(infoCommands.alreadyResearching.regex, box);
                            updateBox(list);
                        }
                        else if(number != finalMax + 1 && tmp.getCup() >= candidateTechs.get(number - 1).cost / 10 - tmp.getResearchingTechCounter()[flg])
                        {
                            tmp.setResearchingTechnology(candidateTechs.get(number - 1));
                            addLabelToBox(infoCommands.choose.regex + candidateTechs.get(number - 1).name() + infoCommands.successful.regex, box);
                            tmp.reduceCup();
                            pane.getChildren().remove(box);
                            needUpdateScience = true;
                            showTechnologies();
                        }
                        else if(number != finalMax + 1) {
                            addLabelToBox(infoCommands.enoughCup.regex + candidateTechs.get(number - 1).name(), box);
                            updateBox(list);
                        }
                        else {
                            pane.getChildren().remove(list);
                            pane.requestFocus();
                        }
                    }
                }
                textField.setText(null);
            }
        });
        box.setOnScroll((ScrollEvent event) -> {
            double yScale = 30;
            double deltaY = event.getDeltaY();
            if (deltaY < 0)
                yScale *= -1;
            if((box.getLayoutY() + box.getHeight() > 650 && yScale < 0) || box.getLayoutY() < 40 && yScale > 0)
                box.setLayoutY(box.getLayoutY() + yScale);
        });
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
        for(City city : gameController.getPlayerTurn().getSeizedCities())
            if(city.getState() == CityState.ATTACHED)
                tmp.add(city);
        box.getChildren().add(printCities(gameController.getPlayerTurn()));
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
                if (finalI <= gameController.getPlayerTurn().getCities().size()) {
                    gameController.getPlayerTurn().setSelectedCity(gameController.getPlayerTurn().getCities().get(finalI - 1));
                    showCity();
                    gameController.getPlayerTurn().setSelectedCity(null);
                }
                else {
                    gameController.getPlayerTurn().setSelectedCity(tmp.get(finalI - gameController.getPlayerTurn().getCities().size() - 1));
                    showCity();
                }
            });
        }
        list.getChildren().add(box);
        pane.getChildren().add(list);
    }
    public void showEconomics()
    {
        audioClip.play();
        Pane list = new Pane();
        panelsPaneStyle(list, 1040, 500, false);
        list.setLayoutX(100);
        list.setLayoutY(110);
        ArrayList<City> n = gameController.getPlayerTurn().getCities();
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
        for(City city : gameController.getPlayerTurn().getSeizedCities())
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
        ArrayList<Notification> tmp = gameController.getPlayerTurn().getNotifications();
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
            if(gameController.getPlayerTurn().getNotifications().size() - listNumber > 4) {
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
        ArrayList<Unit> tmp = gameController.getPlayerTurn().getUnits();
        VBox names = new VBox(), coordinates = new VBox(), unitState = new VBox();
        names.setSpacing(5);
        coordinates.setSpacing(5);
        unitState.setSpacing(5);
        box.getChildren().addAll(names, coordinates, unitState);
        names.setAlignment(Pos.CENTER);
        unitState.setAlignment(Pos.CENTER);
        int max = gameController.getPlayerTurn().getUnits().size();
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
            Unit unit = gameController.getPlayerTurn().getUnits().get(i);
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
                            showMilitary(gameController.getPlayerTurn());
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
        imageView.setStyle("-fx-background-radius: 8;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
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
        Player player = gameController.getPlayerTurn();
        City tmp = player.getSelectedCity();
        int flg = -1;
        for(int i = 0; i < Technology.values().length; i++)
            if(Technology.values()[i] == player.getResearchingTechnology()) flg = i;
        addLabelToBox(infoCommands.cityName.regex + tmp.getName(), box);
        addLabelToBox(gameEnum.foodYield.regex + tmp.getFoodYield(), box);
        addLabelToBox(gameEnum.production.regex + tmp.getProductionYield(), box);
        addLabelToBox(gameEnum.goldYield.regex + tmp.getGoldYield(), box);
        addLabelToBox(gameEnum.cupYield.regex + tmp.getCupYield(), box);
        addLabelToBox(infoCommands.size.regex + tmp.getTerritory().size(), box);
        addLabelToBox(gameEnum.population.regex + tmp.getCitizens().size(), box);
        addLabelToBox(gameEnum.power.regex + tmp.getCombatStrength(), box);
        if(flg > -1) {
            addLabelToBox(infoCommands.currentResearching.regex + gameController.
                    getPlayerTurn().getResearchingTechnology().name(), box);
            addLabelToBox(infoCommands.remainingTurns.regex + (player.getResearchingTechnology().
                    cost - player.getResearchingTechCounter()[flg]), box);
        }
        else {
            addLabelToBox(infoCommands.currentResearching.regex + infoCommands.nothing.regex, box);
            addLabelToBox(infoCommands.remainingTurns.regex + "-", box);
        }
        addLabelToBox(gameEnum.employedCitizens.regex + (tmp.employedCitizens()), box);
        addLabelToBox(gameEnum.unEmployedCitizens.regex + (gameController.getPlayerTurn().
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
        for(Player player : gameController.getPlayers())
            if(player != gameController.getPlayerTurn()) {
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
        Pane list = new Pane();
        panelsPaneStyle(list, 300, 250, false);
        VBox box = new VBox();
        list.getChildren().add(box);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        setCoordinatesBox(list, box, 75, 10);
        addLabelToBox(infoCommands.scoreBoard.regex, box);
        gameController.getPlayers().sort((o1, o2) -> {
            if (o1.getScore() == o2.getScore())
                return 0;
            return o1.getScore() < o2.getScore() ? -1 : 1;
        });
        int number = gameController.getPlayers().size();
        for(int i = number - 1; i >= 0; i--)
            addLabelToBox((number - i) + " - " + gameController.getPlayers().get(i).getCivilization().name().toLowerCase(Locale.ROOT)
                    + ": " +  gameController.getPlayers().get(i).getScore(), box);
        int avg = 0;
        for(Player player1 : gameController.getPlayers())
            avg += player1.getScore();
        addLabelToBox("----------------------", box);
        addLabelToBox(infoCommands.averageScore.regex + ((double) avg / gameController.getPlayers().size()), box);

        list.getChildren().add(exitButtonStyle());
        setCoordinates(list, 10, 10);
        return list;
    }
    public void demographics(MouseEvent mouseEvent) {
        audioClip.play();
        showDemographic(gameController.getPlayerTurn());
    }

    public void military(MouseEvent mouseEvent) {
        audioClip.play();
        showMilitary(gameController.getPlayerTurn());
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
                mouseEvent1 -> {
                    pane.getChildren().remove(list);
                    for(Node node : pane.getChildren())
                        node.setDisable(false);
                    pane.requestFocus();
                });
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
        pane.getChildren().add(list);
        for(int i = 0; i < pane.getChildren().size() - 1; i++)
            pane.getChildren().get(i).setDisable(true);
        setCoordinates(pane, 490, 210);

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
            if(gameController.getPlayerTurn().getResearchingTechnology() == technology) {
                label.setStyle("-fx-background-color: #00ff3c;" +
                        "-fx-text-fill: #560000;" +
                        "-fx-border-width: 4;" +
                        "-fx-border-color: #ffffff;" +
                        "-fx-border-radius: 5;" +
                        "-fx-background-radius: 7");
            }
            else if(gameController.getPlayerTurn().getTechnologies().contains(technology)) {
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
}
