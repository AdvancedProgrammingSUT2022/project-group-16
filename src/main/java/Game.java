import Controllers.GameController;
import Models.City.City;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Terrain.Hex;
import Models.Terrain.Position;
import Models.Terrain.Tile;
import enums.gameCommands.infoCommands;
import enums.mainCommands;
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
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
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
    }
    private void VboxStyle(VBox box) {
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #572e2e;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-pref-width: 100");
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
            for(Node node : pane.getChildren())
                System.out.println(node);
            System.out.println("............");
            if(pane.getChildren().get(pane.getChildren().size() - 1).getClass() != VBox.class) {
                if(pane.getChildren().indexOf(tmp) == 6)
                    pane.getChildren().add(informationVbox(String.valueOf(gameController.getPlayerTurn().getGold()), 12));
                else if(pane.getChildren().indexOf(tmp) == 7)
                    pane.getChildren().add(productionInformationStyle());
                else if(pane.getChildren().indexOf(tmp) == 8)
                    pane.getChildren().add(informationVbox(String.valueOf(gameController.getPlayerTurn().getFood()), 14));
                else if(pane.getChildren().indexOf(tmp) == 9)
                    pane.getChildren().add(informationVbox(String.valueOf(gameController.getPlayerTurn().getPopulation()), 15));
                else if(pane.getChildren().indexOf(tmp) == 10)
                    pane.getChildren().add(informationVbox(String.valueOf(gameController.getPlayerTurn().getHappiness()), 16));
                else if(pane.getChildren().indexOf(tmp) == 11)
                    pane.getChildren().add(scienceInformationStyle());
                tmp.setFitWidth(43);
                tmp.setFitHeight(43);
                tmp.setX(13.5);
                tmp.setY(y - 1.5);
            }
        });
        tmp.setOnMouseExited(mouseEvent -> {
            pane.getChildren().remove(pane.getChildren().size() - 1);
            tmp.setFitWidth(40);
            tmp.setFitHeight(40);
            tmp.setX(15);
            tmp.setY(y);
        });
        pane.getChildren().get(11).setOnMousePressed(mouseEvent -> {
            showTechnologies();
        });
    }

    private void setInformationStyles() {
        for (int i = 6; i < 12; i++)
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
        box.setStyle("-fx-background-radius: 8;" +
                "-fx-background-color: #572e2e;" +
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
        pane.getChildren().add(pane.getChildren().size() - 2, box);
//        for (int i = 0; i < pane.getChildren().size() - 1; i++)
//            pane.getChildren().get(i).setDisable(true);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        int finalMax = max;
        int finalFlag = flag;
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            int number;
            if(keyName.equals("Enter")) {
                number = Integer.parseInt(textField.getText());
                box.getChildren().remove(textField);
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
        });
    }
    private void updateBox(VBox box) {
        pane.getChildren().remove(box);
        pane.getChildren().add(box);
    }

}
