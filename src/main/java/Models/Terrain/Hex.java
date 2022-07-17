package Models.Terrain;

import Controllers.GameController;
import Models.City.City;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Player.TileState;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.MidRange;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.NonCombatUnits.Worker;
import Models.Units.Unit;
import enums.gameCommands.infoCommands;
import enums.gameCommands.unitCommands;
import enums.gameEnum;
import javafx.animation.FadeTransition;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.regex.Matcher;

public class Hex{
    private static Pane parent;
    private final Pane pane;
    private Position position;
    private Tile tile;
    private TileState tileState;
    private ArrayList<ImageView> hexElements = new ArrayList<>();
    private ImageView tileImageView;
    private GameController gameController;
    private boolean isBannerOpen = false;
    private boolean isCityPanelOpen = false;
    private boolean isCUnitPanelOpen = false;
    private boolean isNCUnitPanelOpen = false;

    public Hex(Position position, GameController gameController){
        this.gameController = gameController;
        this.position = position;
        this.pane = new Pane();
        pane.setOnMousePressed(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                onMousePressed();
            }
        });
        pane.setOnMouseMoved(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                onMouseMoved();
            }
        });
        pane.setOnMouseReleased(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                onMouseReleased();
            }
        });
        pane.setOnMouseExited(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                onMouseExited();
            }
        });
        pane.setOnMouseClicked(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                onMouseClicked();
            }
        });
        pane.setPrefWidth(90);
        pane.setPrefHeight(90);
        pane.setLayoutX(position.X);
        pane.setLayoutY(position.Y);
    }

    public static void setPane(Pane pane){
        parent = pane;
    }
    public void setTile(Tile tile){
        this.tile = tile;
        setBackground();
        setFeatureBackground();
        setRiverBorders();
        setCitiesBorder();
        setResources();
        setCUnits();
        setNCUnits();
    }


    private ImageView setImage(String url, int x, int y ,int width, int height){
        Image image = new Image(this.getClass().getResource(url).toExternalForm());
        ImageView imageView = new ImageView();
        imageView.setX(x);
        imageView.setY(y);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setImage(image);
        hexElements.add(imageView);
        return imageView;
    }

    private void setRiverBorders()
    {
        if(this.tileState.equals(TileState.FOG_OF_WAR)) return;
        for (int i = 0; i < 6; i++)
        {
            if (this.tile.getBorders()[i].equals(BorderType.NONE))
                continue;

            String url = "/photos/Tiles/river" + i + ".png";
            setImage(url, -8, -8, 106, 106);
        }
    }

	private void setCitiesBorder()
	{
        if(tileState.equals(TileState.FOG_OF_WAR))
            return;

        Player playerTurn = GameController.getInstance().getPlayerTurn();


        for (Player player : GameController.getInstance().getPlayers())
            for (Integer cityBorderIndex : player.getCityBorderIndexes(tile))
            {
                String url;
                if(player == playerTurn)
                    url = "/photos/Tiles/cityBorder_us_" + cityBorderIndex + ".png";
                else
                    url = "/photos/Tiles/cityBorder_them_" + cityBorderIndex + ".png";

                setImage(url, -8,  -8, 106, 106);
            }
	}

    private void setResources()
    {
        if(tile.getResource() == null || this.tileState.equals(TileState.FOG_OF_WAR))
            return;

        String url = "/photos/resources/";
        switch (this.tile.getResource().getRESOURCE_TYPE())
        {
            case BANANA -> url += "Bananas.png";
            case CATTLE -> url += "Cattle.png";
            case DEER -> url += "Deer.png";
            case SHEEP -> url += "Sheep.png";
            case WHEAT -> url += "Wheat.png";
            case COAL -> url += "Coal.png";
            case HORSES -> url += "Horses.png";
            case IRON -> url += "Iron.png";
            case COTTON -> url += "Cotton.png";
            case DYES -> url += "Dyes.png";
            case FURS -> url += "Furs.png";
            case GEMS -> url += "Gems.png";
            case GOLD -> url += "Gold.png";
            case INCENSE -> url += "Incense.png";
            case IVORY -> url += "Ivory.png";
            case MARBLE -> url += "Marble.png";
            case SILK -> url += "Silk.png";
            case SILVER -> url += "Silver.png";
            case SUGAR -> url += "Sugar.png";
        }
        setImage(url, 15, 45, 30, 30);
    }

    private void setCUnits()
    {
        if(tile.getCombatUnitInTile() == null || tileState.equals(TileState.FOG_OF_WAR))
            return;

        String url = "/photos/units/";

        Unit combatUnitInTile = tile.getCombatUnitInTile();
        if(combatUnitInTile instanceof LongRange)
            switch (((LongRange) combatUnitInTile).getType())
            {
                case ARCHER -> url += "archer.png";
                case CHARIOT_A -> url += "chariotarcher.png";
                case CATAPULT -> url += "catapult.png";
                case CROSSMAN -> url += "crossbowman.png";
                case TREBUCHET -> url += "trebuchet.png";
                case CANON -> url += "cannon.png";
                case ARTILLERY -> url += "artillery.png";
            }
        else if(combatUnitInTile instanceof MidRange)
			switch (((MidRange) combatUnitInTile).getType())
			{
				case SCOUT -> url += "scout.png";
				case SPEARMAN -> url += "spearman.png";
				case WARRIOR -> url += "warrior.png";
				case HORSEMAN -> url += "horseman.png";
                case SWORDSMAN -> url += "swordsman.png";
                case KNIGHT -> url += "knight.png";
                case LSWORDSMAN -> url += "longswordsman.png";
                case PIKE_MAN -> url += "pikeman.png";
                case CAVALRY -> url += "cavalry.png";
                case LANCER -> url += "lancer.png";
                case MUSKET_MAN -> url += "musketman.png";
                case RIFLEMAN -> url += "rifleman.png";
                case ANTI_TANK -> url += "antitankgun";
                case INFANTRY -> url += "infantry";
                case PANZER -> url += "panzer.png";
                case TANK -> url += "tank.png";
			}

        setImage(url, 30, 0, 60, 60);
    }

    private void setNCUnits()
    {
        if(tile.getNonCombatUnitInTile() == null || tileState.equals(TileState.FOG_OF_WAR))
            return;

        String url = "/photos/units/";
        if(tile.getNonCombatUnitInTile() instanceof Worker)
            url += "worker.png";
        else if(tile.getNonCombatUnitInTile() instanceof Settler)
            url += "settler.png";

        setImage(url, 50, 50, 40, 40);
    }

    private void setFeatureBackground() {
        if(tile.getTileFeature() == null || tile.getTileFeature().equals(TileFeature.NONE) || this.tileState.equals(TileState.FOG_OF_WAR)) return;
        String url = "/photos/Tiles/Hexagon.png";
        switch (tile.getTileFeature()){
            case FLOOD_PLAIN -> url = "/photos/features/FloodPlains.png";
            case FOREST -> url = "/photos/features/Forest.png";
            case JUNGLE -> url = "/photos/features/Jungle.png";
            case MARSH -> url = "/photos/features/Marsh.png";
            case OASIS -> url = "/photos/features/Oasis.png";
            case ICE -> url = "/photos/features/Ice.png";
        }
        setImage(url,  15, 5, 30, 30 );
    }

    public void setTileState(TileState tileState) {
        this.tileState = tileState;
    }

    private void setBackground() {
        String url = "/photos/features/fog.png";
        if(this.tileState.equals(TileState.FOG_OF_WAR)){
            setImage(url, 0, 0 , 100, 100);
            return;
        }
        switch (this.tile.getTileType()){
            case PLAINS -> url = "/photos/Tiles/Plains.png";
            case GRASSLAND ->  url = "/photos/Tiles/Grassland.png";
            case DESERT -> url = "/photos/Tiles/Desert.png";
            case TUNDRA -> url = "/photos/Tiles/Tundra.png";
            case SNOW ->  url = "/photos/Tiles/Snow.png";
            case HILLS -> url = "/photos/Tiles/Hill.png";
            case OCEAN -> url = "/photos/Tiles/Ocean.png";
            case MOUNTAIN -> url = "/photos/Tiles/Mountain.png";
            case RUIN -> url = "/photos/Tiles/ruin.png";
            default -> url = "/photos/Tiles/Hexagon.png";
        }
        tileImageView = setImage(url, 0, 0, 90,90);

        if(GameController.getInstance().getPlayerTurn().getMap().get(this.tile).equals(TileState.REVEALED)){
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-.6);
            tileImageView.setEffect(colorAdjust);
        }
    }

    public void removeHex(){
        parent.getChildren().remove(pane);
        pane.getChildren().removeAll(hexElements);
        this.hexElements.clear();
        this.tileState  =null;
        this.tile = null;
    }
    public void addHex(){
        pane.getChildren().addAll(hexElements);
        parent.getChildren().add(pane);
    }

    private void onMousePressed()
    {
        if(tileState.equals(TileState.FOG_OF_WAR))
            return;
        tileImageView.setEffect(new Lighting());


    }

    private void onMouseMoved() {
        //city banner
        for (City city : gameController.getPlayerTurn().getCities()) {
            if (city.getCapitalTile().getPosition() == tile.getPosition() && !isBannerOpen) {
                isBannerOpen = true;
                cityBanner(city);
            }
        }
    }
    private void cityBanner(City city) {
        Pane list = new Pane();
        fade(list).play();
        panelsPaneStyle(list, position.X + 40, position.Y - 65, 200, 50);
        addLabelToPane(list, 10, 10, "photos/gameIcons/Gold.png", String.valueOf(city.getGoldYield()));
        addLabelToPane(list, 30, 10, "photos/gameIcons/Food.png", String.valueOf(city.getFoodYield()));
        addLabelToPane(list, 50, 10, "photos/gameIcons/Production.png", String.valueOf(city.getProductionYield()));
        addLabelToPane(list, 70, 10, "photos/gameIcons/Food.png", String.valueOf(city.getFoodYield()));
        addLabelToPane(list, 90, 10, "photos/gameIcons/Population.png", String.valueOf(city.getPopulation()));
        addLabelToPane(list, 110, 10, "photos/gameIcons/Hexagon.png", String.valueOf(city.getTerritory().size()));
        addLabelToPane(list, 130, -6, null, city.getName());
        addLabelToPane(list, 130, 10, null, "PF: " + String.valueOf(city.getCombatStrength()));
        if((isCityPanelOpen || isCUnitPanelOpen || isNCUnitPanelOpen))
            parent.getChildren().add(parent.getChildren().size() - 1, list);
        else
            parent.getChildren().add(list);
    }
    private void onMouseReleased()
    {
        if(tileState.equals(TileState.FOG_OF_WAR))
            return;
        if(tileState.equals(TileState.VISIBLE))
            tileImageView.setEffect(null);
        else if (tileState.equals(TileState.REVEALED)) {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-.6);
            tileImageView.setEffect(colorAdjust);
        }
    }
    private void onMouseExited()
    {
        //remove banner
        if (isBannerOpen && !isCityPanelOpen && !isCUnitPanelOpen && !isNCUnitPanelOpen) {
            parent.getChildren().remove(parent.getChildren().size() - 1);
            isBannerOpen = false;
        }
        else if (isBannerOpen && (isCityPanelOpen || isCUnitPanelOpen || isNCUnitPanelOpen)) {
            parent.getChildren().remove(parent.getChildren().size() - 2);
            isBannerOpen = false;
        }
    }
    private void onMouseClicked()
    {
        if(!isCityPanelOpen && !isCUnitPanelOpen && !isNCUnitPanelOpen) {
            for (City city : gameController.getPlayerTurn().getCities())
                if (city.getCapitalTile().getPosition() == tile.getPosition()) {
                    isCityPanelOpen = true;
                    cityPanel(city);
                }
        }
        else if(isCityPanelOpen && !isCUnitPanelOpen && !isNCUnitPanelOpen) {
            //combat unit panel
            for (Unit unit : gameController.getPlayerTurn().getUnits()) {
                if (unit.getTile().getPosition() == tile.getPosition() && unit.getPower() != 0) {
                    isCityPanelOpen = false;
                    isCUnitPanelOpen = true;
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    unitPanel(unit);
                }
            }
        }
        else if(!isCityPanelOpen && isCUnitPanelOpen && !isNCUnitPanelOpen) {
            //non combat unit panel
            for (Unit unit : gameController.getPlayerTurn().getUnits()) {
                if (unit.getTile().getPosition().X == tile.getPosition().X &&
                        unit.getTile().getPosition().Y == tile.getPosition().Y &&
                        unit.getPower() == 0) {
                    isCUnitPanelOpen = false;
                    isNCUnitPanelOpen = true;
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    unitPanel(unit);
                }
            }
        }
    }
    private void unitPanel(Unit unit) {
        Pane list = new Pane();
        panelsPaneStyle2(list);
        VBox box = new VBox(), photos = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        photos.setAlignment(Pos.CENTER);
        photos.setSpacing(14);
        box.setSpacing(6);
        addLabelToBox(unitCommands.unitType.regex + unit.toString(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitMP.regex + unit.getMovementPoints(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitHealth.regex + unit.getHealth(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitState.regex + unit.getUnitState().symbol, box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitPosition.regex + unit.getTile().getPosition().X + ","
                + unit.getTile().getPosition().Y, box);
        list.getChildren().add(box);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(75);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(photos);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(50);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private void cityPanel(City city)
    {
        Pane list = new Pane();
        panelsPaneStyle2(list);
        VBox box = new VBox(), photos = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        photos.setAlignment(Pos.CENTER);
        photos.setSpacing(14);
        box.setSpacing(6);
        Player player = gameController.getPlayerTurn();
        int flg = -1;
        for(int i = 0; i < Technology.values().length; i++)
            if(Technology.values()[i] == player.getResearchingTechnology()) flg = i;
        addLabelToBox(infoCommands.cityName.regex + city.getName(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(gameEnum.foodYield.regex + city.getFoodYield(), box);
        addPhotoToBox(photos, "photos/gameIcons/Food.png");
        addLabelToBox(gameEnum.production.regex + city.getProductionYield(), box);
        addPhotoToBox(photos, "photos/gameIcons/Production.png");
        addLabelToBox(gameEnum.goldYield.regex + city.getGoldYield(), box);
        addPhotoToBox(photos, "photos/gameIcons/Gold.png");
        addLabelToBox(gameEnum.cupYield.regex + city.getCupYield(), box);
        addPhotoToBox(photos, "photos/gameIcons/Science.png");
        addLabelToBox(infoCommands.size.regex + city.getTerritory().size(), box);
        addPhotoToBox(photos, "photos/gameIcons/Hexagon.png");
        addLabelToBox(gameEnum.population.regex + city.getCitizens().size(), box);
        addPhotoToBox(photos, "photos/gameIcons/Population.png");
        addLabelToBox(gameEnum.power.regex + city.getCombatStrength(), box);
        addPhotoToBox(photos, "photos/gameIcons/Resistance.png");
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
        addLabelToBox(gameEnum.employedCitizens.regex + (city.employedCitizens()), box);
        addLabelToBox(gameEnum.unEmployedCitizens.regex + (gameController.getPlayerTurn().
                getTotalPopulation() - city.employedCitizens()), box);
        if(city.getCurrentConstruction() != null) {
            addLabelToBox(gameEnum.currentConstruction.regex + city.getCurrentConstruction().toString(), box);
            addLabelToBox(infoCommands.remainingTurns.regex + city.getCurrentConstruction().getTurnTillBuild(), box);
        }
        else
            addLabelToBox(gameEnum.currentConstruction.regex + infoCommands.nothing.regex, box);
        list.getChildren().add(box);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(75);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(photos);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(50);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        addLabelToPane(list, 400, 4, null, "unEmployed citizens");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            unEmployedCitizensPanel(city);
            list.setDisable(true);
        });
        addLabelToPane(list, 400, 24, null, "employed citizens");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            employedCitizensPanel(city);
            list.setDisable(true);
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private void employedCitizensPanel(City city) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 300, 600, 300);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 100, 25);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        addLabelToBox("number of employed citizens: " + String.valueOf(city.employedCitizens()), box);
        addLabelToBox("you can choose a position to un lock citizen from tile", box);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                Matcher matcher = gameEnum.compareRegex(textField.getText(), gameEnum.lockCitizenToTile);
                gameController.getPlayerTurn().setSelectedCity(city);
                String result = gameController.unLockCitizenToTile(matcher);
                if(box.getChildren().size() == 3)
                    addLabelToBox(result, box);
                else {
                    box.getChildren().remove(box.getChildren().size() - 1);
                    addLabelToBox(result, box);
                }
                if(result.equals(gameEnum.removeFromWork.regex)) {
                    parent.getChildren().remove(list);
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    cityPanel(city);
                }
                textField.setText(null);
            }
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private void unEmployedCitizensPanel(City city) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 300, 600, 300);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 100, 25);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        addLabelToBox("number of un employed citizens: " + String.valueOf(city.getPopulation()
                - city.employedCitizens()), box);
        addLabelToBox("you can choose a position to lock citizen to tile", box);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                Matcher matcher = gameEnum.compareRegex(textField.getText(), gameEnum.lockCitizenToTile);
                gameController.getPlayerTurn().setSelectedCity(city);
                String result = gameController.lockCitizenToTile(matcher);
                if(box.getChildren().size() == 3)
                    addLabelToBox(result, box);
                else {
                    box.getChildren().remove(box.getChildren().size() - 1);
                    addLabelToBox(result, box);
                }
                if(result.equals(gameEnum.successfulLock.regex)) {
                    parent.getChildren().remove(list);
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    cityPanel(city);
                }
                textField.setText(null);
            }
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private ImageView exitButtonStyle(Pane list) {
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
            isCityPanelOpen = false;
            isCUnitPanelOpen = false;
            isNCUnitPanelOpen = false;
            for (int i = 0; i < parent.getChildren().size() - 1; i++)
                parent.getChildren().get(i).setDisable(false);
            parent.getChildren().remove(list);
            parent.setDisable(false);
            parent.requestFocus();
        });
        exitButton.setImage(new Image("photos/gameIcons/panelsIcons/Close.png"));
        exitButton.setFitHeight(25);
        exitButton.setFitWidth(25);
        parent.requestFocus();
        return exitButton;
    }
    private FadeTransition fade(Node node) {
        FadeTransition ft = new FadeTransition();
        ft.setNode(node);
        ft.setDuration(new Duration(200));
        ft.setFromValue(0);
        ft.setToValue(1);
        return ft;
    }


    //styles
    private void addLabelToPane(Pane list, int x, int y, String url, String information) {
        if (url != null) {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(url));
            imageView.setFitWidth(13);
            imageView.setFitHeight(13);
            list.getChildren().add(imageView);
            list.getChildren().get(list.getChildren().size() - 1).setLayoutX(x);
            list.getChildren().get(list.getChildren().size() - 1).setLayoutY(y);
        }
        Label label = new Label();
        label.setStyle("-fx-text-fill: white;" +
                "-fx-font-size: 18");
        label.setText(information);
        list.getChildren().add(label);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(x);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(y + 12);
    }
    private void addPhotoToBox(VBox box, String url) {
        if (url == null)
            addLabelToBox("", box);
        else {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(url));
            imageView.setFitWidth(15);
            imageView.setFitHeight(15);
            box.getChildren().add(imageView);
        }
    }
    private void panelsPaneStyle(Pane list, int x, int y, int width, int height) {
        list.setLayoutX(x);
        list.setLayoutY(y);
        list.setPrefHeight(height);
        list.setPrefWidth(width);
        list.setStyle("-fx-background-color: #00009a;" +
                "-fx-text-fill: white;" +
                "-fx-border-width: 3;" +
                "-fx-border-color: white;" +
                "-fx-border-radius: 5;" +
                "-fx-background-radius: 8;");
    }
    private void addLabelToBox(String line, VBox box) {
        Label label = new Label();
        label.setText(line);
        label.setStyle("-fx-text-fill: white;" +
                "-fx-font-size: 18;");
        box.getChildren().add(label);
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

    //coordinates
    private void setCoordinatesBox(Pane list, VBox box, double x, double y) {
        list.getChildren().get(list.getChildren().indexOf(box)).setLayoutX(x);
        list.getChildren().get(list.getChildren().indexOf(box)).setLayoutY(y);
    }
}
