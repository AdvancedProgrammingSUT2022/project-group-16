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
import Models.Units.Unit;
import Models.Units.UnitState;
import enums.gameCommands.infoCommands;
import enums.gameEnum;
import enums.mainCommands;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

public class Game extends Application {
    private Hex[][] hexagons;
    private final GameController gameController = GameController.getInstance();
    ArrayList<Hex> playerTurnTiles = new ArrayList<>();
    private boolean needUpdateScience = false;
    private boolean needUpdateProduction = true;
    public AudioClip audioClip = new AudioClip(Main.class.getResource("audio/gameAudios/click.mp3").toExternalForm());
    @FXML
    private Pane pane;
    @Override

    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/game.fxml").toExternalForm()))));
        stage.show();
    }

    public void initialize() {
        gameController.initGame();
        Hex.setPane(pane);
        int x = 200;
        hexagons = new Hex[GameController.getInstance().MAP_SIZE][GameController.getInstance().MAP_SIZE];
        for(int i = 0; i < gameController.MAP_SIZE; i++){
            int y = (i % 2 == 0 ? 50 : 80);
            for(int j = 0; j < gameController.MAP_SIZE ; j++){
                hexagons[i][j] = new Hex(new Position(x, y));
                y += 60;
            }
            x += 80;
        }
        generateMapForPlayer(gameController.getPlayerTurn());

        setInformationStyles();
        pane.getChildren().get(11).setOnMousePressed(mouseEvent -> {
            showTechnologies();
            audioClip.play();
        });

        //TODO: do not remove this part :))))
        new City(gameController.getMap().get(55), gameController.getPlayerTurn());
        new City(gameController.getMap().get(45), gameController.getPlayerTurn());
        new City(gameController.getMap().get(78), gameController.getPlayerTurn());
        new MidRange(gameController.getPlayerTurn(), MidRangeType.CAVALRY, gameController.getMap().get(44));
        new MidRange(gameController.getPlayerTurn(), MidRangeType.HORSEMAN, gameController.getMap().get(23));
        new MidRange(gameController.getPlayerTurn(), MidRangeType.LSWORDSMAN, gameController.getMap().get(11));
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be dutchman");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be in zendegi");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "dorood bar lotfian");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be ap");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be seyyed");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be SNP");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "lanat be ap");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "dorood bar group 16");
        new Notification(gameController.getPlayerTurn(), gameController.getTurnCounter(), "bazam lanat be ap");
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
        for(int i= 0; i < 10; i++){
            for(int j = 0; j < 10; j++){
                hexagons[i][j].removeHex();
            }
        }
        playerTurnTiles.clear();
        gameController.checkChangeTurn(); //TODO: fix bugs
        generateMapForPlayer(gameController.getPlayerTurn());
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
    private VBox informationVbox(String information, int index) {
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
    private void updateBox(VBox box) {
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
        VBox box = new VBox();
        panelsVboxStyle(box);
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
        pane.getChildren().add(box);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        int finalMax = max;
        int finalFlag = flag;
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                if(isValidNumber(textField.getText())) {
                    int number = Integer.parseInt(textField.getText());
                    if(number > finalMax + 1 && (box.getChildren().get(box.getChildren().size() - 1).getClass() == TextField.class))
                        addLabelToBox(mainCommands.pickBetween.regex + "1 and " + (finalMax + 1), box);
                    else if(number > finalMax + 1 && (box.getChildren().get(box.getChildren().size() - 1).getClass() == Label.class &&
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
                            updateBox(box);
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
                            updateBox(box);
                        }
                        else {
                            pane.getChildren().remove(box);
                        }
                    }
                }
                textField.setText(null);
            }
        });
    }
    private void panelsVboxStyle(VBox box) {
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(340);
        box.setLayoutY(180);
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: rgb(68,30,30);" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 600");
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
        Pane list = new Pane();
        panelsPaneStyle(list, 450, 500);
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
        Pane list = new Pane();
        panelsPaneStyle(list, 1040, 500);
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
        Pane list = new Pane();
        panelsPaneStyle(list, 400, 500);
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
        Pane box = new Pane();
        panelsPaneStyle(box, 600, 500);
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
            pane.getChildren().get(pane.getChildren().size() - 1).setDisable(false);
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
        panelsPaneStyle(box, 600, 500);
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
    private void panelsPaneStyle(Pane list, double width, double height) {
        list.setLayoutX(340);
        list.setLayoutY(180);
        ImageView imageView = new ImageView();
        try {
            imageView.setImage(new Image(String.valueOf(new URL(getClass()
                    .getResource("photos/backgrounds/icons/frontGamePage.jpg").toExternalForm()))));
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
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
        panelsPaneStyle(list, 1000, 500);
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
        panelsPaneStyle(list, 300, 250);
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
}
