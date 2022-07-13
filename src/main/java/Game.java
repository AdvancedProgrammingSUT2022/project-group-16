import Controllers.GameController;
import Models.City.City;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.Hex;
import Models.Terrain.Position;
import Models.Terrain.Tile;
import enums.gameCommands.infoCommands;
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
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.ArrayList;
import java.util.Scanner;

public class Game extends Application {
    private final Hex[][] hexagons = new Hex[10][10];
    private final GameController gameController = GameController.getInstance();
    ArrayList<Hex> playerTurnTiles = new ArrayList<>();
    @FXML
    private Pane pane;
    @Override

    public void start(Stage stage) throws Exception {
        stage.setScene(new Scene(FXMLLoader.load(new
                URL(getClass().getResource("fxml/game.fxml").toExternalForm()))));
        stage.show();
    }

    public void initialize() {
        setInformationStyles();
        int x = 200;
        for(int i = 0; i < 10; i++){
            int y = (i % 2 == 0 ? 50 : 80);
            for(int j = 0; j < 10 ; j++){
                hexagons[i][j] = new Hex(new Position(x, y), pane);
                y += 60;
            }
            x += 80;
        }
        gameController.initGame();
        generateMapForPlayer(gameController.getPlayerTurn());

        //TODO: do not remove this part :))))
//        new City(gameController.getMap().get(55), gameController.getPlayerTurn());
//        new City(gameController.getMap().get(45), gameController.getPlayerTurn());
//        new City(gameController.getMap().get(78), gameController.getPlayerTurn());
//        gameController.getPlayerTurn().getTechnologies().add(Technology.MILITARY_SCIENCE);
//        gameController.getPlayerTurn().getTechnologies().add(Technology.BRONZE_WORKING);
//        gameController.getPlayerTurn().setResearchingTechnology(Technology.THE_WHEEL);
//        gameController.getPlayerTurn().setCup(100);

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
        if(gameController.getPlayerTurn().getCities().size() == 0) {
            Label label = new Label();
            label.setText("nothing...");
            labelStyle(label);
            box.getChildren().add(label);
        }
        else
            for (City city : gameController.getPlayerTurn().getCities()) {
                Label label = new Label();
                label.setText(city.getName() + " - " + city.getProductionYield());
                labelStyle(label);
                box.getChildren().add(label);
            }
        return box;
    }

    private void setHoverForInformationTitles(ImageView tmp) {
        double y = tmp.getY();
        tmp.setOnMouseMoved(mouseEvent -> {
            if(pane.getChildren().get(pane.getChildren().size() - 1).getClass() != VBox.class) {
                if(pane.getChildren().indexOf(tmp) == 6) {
                    VBox vBox = informationVbox(String.valueOf(gameController.getPlayerTurn().getGold()), 12);
                    fade(vBox, 0, 1).play();
                    pane.getChildren().add(vBox);
                }
                else if(pane.getChildren().indexOf(tmp) == 7) {
                    VBox vBox = productionInformationStyle();
                    fade(vBox, 0, 1).play();
                    pane.getChildren().add(vBox);
                }
                else if(pane.getChildren().indexOf(tmp) == 8) {
                    VBox vBox = informationVbox(String.valueOf(gameController.getPlayerTurn().getFood()), 14);
                    fade(vBox, 0, 1).play();
                    pane.getChildren().add(vBox);
                }
                else if(pane.getChildren().indexOf(tmp) == 9) {
                    VBox vBox = informationVbox(String.valueOf(gameController.getPlayerTurn().getPopulation()), 15);
                    fade(vBox, 0, 1).play();
                    pane.getChildren().add(vBox);
                }
                else if(pane.getChildren().indexOf(tmp) == 10) {
                    VBox vBox = informationVbox(String.valueOf(gameController.getPlayerTurn().getHappiness()), 16);
                    fade(vBox, 0, 1).play();
                    pane.getChildren().add(vBox);
                }
                else if(pane.getChildren().indexOf(tmp) == 11) {
                    VBox vBox = scienceInformationStyle();
                    fade(vBox, 0, 1).play();
                    pane.getChildren().add(vBox);
                }
                tmp.setFitWidth(43);
                tmp.setFitHeight(43);
                tmp.setX(13.5);
                tmp.setY(y - 1.5);
            }
        });
        tmp.setOnMouseExited(mouseEvent -> {
            if(pane.getChildren().get(pane.getChildren().size() - 2).getClass() == VBox.class) {
                VBox vBox = ((VBox) pane.getChildren().get(pane.getChildren().size() - 2));
                fade(vBox, 1, 0).play();
                pane.getChildren().remove(vBox);
            }
            else {
                VBox vBox = ((VBox) pane.getChildren().get(pane.getChildren().size() - 1));
                fade(vBox, 1, 0).play();
                pane.getChildren().remove(vBox);
            }
            tmp.setFitWidth(40);
            tmp.setFitHeight(40);
            tmp.setX(15);
            tmp.setY(y);
        });
        pane.getChildren().get(11).setOnMousePressed(mouseEvent -> {
            showTechnologies();
        });
    }
    private FadeTransition fade(Node node, double from, double to) {
        FadeTransition ft = new FadeTransition();
        ft.setNode(node);
        ft.setDuration(new Duration(200));
        ft.setFromValue(from);
        ft.setToValue(to);
        return ft;
    }
    private void setInformationStyles() {
        for (int i = 6; i < 12; i++)
            if(pane.getChildren().size() < 50)
                setHoverForInformationTitles((ImageView) pane.getChildren().get(i));
    }
    //TODO when the turn changes delete playerTurnTiles from pane
    public void generateMapForPlayer(Player player){
        for (Tile tile : player.getMap().keySet()) {
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileState(player.getMap().get(tile));
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileType(tile.getTileType());
            hexagons[tile.getPosition().X][tile.getPosition().Y].setTileFeature(tile.getTileFeature());
            playerTurnTiles.add(hexagons[tile.getPosition().X][tile.getPosition().Y]);
        }
        playerTurnTiles.forEach(Hex::addHex);
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
    private void showTechnologies()
    {
        VBox box = new VBox();
        box.setAlignment(Pos.CENTER);
        box.setLayoutX(340);
        box.setLayoutY(180);
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: rgba(0,0,0,0.74);" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 600");
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
                            updateBox(box);
                            tmp.reduceCup();
                        }
                        else if(number != finalMax + 1) {
                            addLabelToBox(infoCommands.enoughCup.regex + candidateTechs.get(number - 1).name(), box);
                            updateBox(box);
                        }
                        else
                            pane.getChildren().remove(box);
                    }
                }
                textField.setText(null);
            }
        });
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
    private void labelInformationFadesSet(Label label, double y) {
        if(pane.getChildren().get(pane.getChildren().size() - 1).getClass() != VBox.class) {
            VBox vBox = panelsVbox(label.getText(), y);
            fade(vBox, 0, 1).play();
            pane.getChildren().add(vBox);
        }
    }
    public void removeLabel(MouseEvent mouseEvent) {
        if(pane.getChildren().get(pane.getChildren().size() - 2).getClass() == VBox.class) {
            VBox vBox = ((VBox) pane.getChildren().get(pane.getChildren().size() - 2));
            fade(vBox, 1, 0).play();
            pane.getChildren().remove(vBox);
        }
        else {
            VBox vBox = ((VBox) pane.getChildren().get(pane.getChildren().size() - 1));
            fade(vBox, 1, 0).play();
            pane.getChildren().remove(vBox);
        }
    }
    public void cityLabel(MouseEvent mouseEvent) {
        Label label = new Label();
        labelStyle(label);
        label.setText("cities");
        labelInformationFadesSet(label, 20);
    }

    public void unitLabel(MouseEvent mouseEvent) {
        Label label = new Label();
        labelStyle(label);
        label.setText("units");
        labelInformationFadesSet(label, 75);
    }

    public void militaryLabel(MouseEvent mouseEvent) {
        Label label = new Label();
        labelStyle(label);
        label.setText("military");
        labelInformationFadesSet(label, 130);
    }

    public void demographicLabel(MouseEvent mouseEvent) {
        Label label = new Label();
        labelStyle(label);
        label.setText("demographics");
        labelInformationFadesSet(label, 185);
    }

    public void notificationLabel(MouseEvent mouseEvent) {
        Label label = new Label();
        labelStyle(label);
        label.setText("notifications");
        labelInformationFadesSet(label, 240);
    }

    public void economicLabel(MouseEvent mouseEvent) {
        Label label = new Label();
        labelStyle(label);
        label.setText("economic");
        labelInformationFadesSet(label, 295);
    }
}
