package Models.Terrain;

import Controllers.GameController;
import Models.City.City;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Player.TileState;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.MidRange;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.NonCombatUnits.Worker;
import Models.Units.Unit;
import enums.gameCommands.infoCommands;
import enums.gameCommands.selectCommands;
import enums.gameCommands.unitCommands;
import enums.gameEnum;
import javafx.animation.FadeTransition;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.util.Duration;
import java.util.ArrayList;
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
        pane.setOnMousePressed(mouseEvent -> onMousePressed());
        pane.setOnMouseMoved(mouseEvent -> onMouseMoved());
        pane.setOnMouseReleased(mouseEvent -> onMouseReleased());
        pane.setOnMouseExited(mouseEvent -> onMouseExited());
        pane.setOnMouseClicked(mouseEvent -> onMouseClicked());
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

        ArrayList<CombatUnit> combatUnitsInTile = GameController.getInstance().getTileCUnits(tile);
        int i = 0;
        for(CombatUnit combatUnitInTile : combatUnitsInTile) {
            String url = "/photos/units/";
            if (combatUnitInTile instanceof LongRange) {
                switch (((LongRange) combatUnitInTile).getType()) {
                    case ARCHER -> url += "archer.png";
                    case CHARIOT_A -> url += "chariotarcher.png";
                    case CATAPULT -> url += "catapult.png";
                    case CROSSMAN -> url += "crossbowman.png";
                    case TREBUCHET -> url += "trebuchet.png";
                    case CANON -> url += "cannon.png";
                    case ARTILLERY -> url += "artillery.png";
                }
            }
            else if (combatUnitInTile instanceof MidRange) {
                switch (((MidRange) combatUnitInTile).getType()) {
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
            }
            setImage(url, 30 - i, 0 - i, 60, 60);
            i += 5;
        }

    }

    private void setNCUnits()
    {
        if(tile.getNonCombatUnitInTile() == null || tileState.equals(TileState.FOG_OF_WAR))
            return;

        ArrayList<NonCombatUnit> nonCombatUnits = GameController.getInstance().getTileNCUnits(tile);
        int i = 0;
        for(NonCombatUnit nonCombatUnit : nonCombatUnits) {
            String url = "/photos/units/";
            if (nonCombatUnit instanceof Worker)
                url += "worker.png";
            else if (nonCombatUnit instanceof Settler)
                url += "settler.png";

            setImage(url, 50 -i , 50 - i, 30, 30);
            i += 5;
        }
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
        if(hasCity() != null && !isBannerOpen) {
            isBannerOpen = true;
            cityBanner(hasCity());
        }
        else if(!isBannerOpen && !gameController.getPlayerTurn().getMap().get(tile).equals(TileState.FOG_OF_WAR)){
            isBannerOpen = true;
            emptyTilePanel();
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
        else if (isBannerOpen) {
            parent.getChildren().remove(parent.getChildren().size() - 2);
            isBannerOpen = false;
        }
    }
    private City hasCity() {
        for (City city : gameController.getPlayerTurn().getCities())
            if(city.getCapitalTile().getPosition() == tile.getPosition())
                return city;
        return null;
    }
    private Unit hasCUnit() {
        for (Unit unit : gameController.getPlayerTurn().getUnits())
            if(unit.getTile().getPosition() == tile.getPosition() && unit.getPower() != 0)
                return unit;
        return null;
    }
    private Unit hasNCUnit() {
        for (Unit unit : gameController.getPlayerTurn().getUnits())
            if(unit.getTile().getPosition().X == tile.getPosition().X &&
                    unit.getTile().getPosition().Y == tile.getPosition().Y &&
                    unit.getPower() == 0)
                return unit;
        return null;
    }
    private void onMouseClicked()
    {
        if(isCityPanelOpen) {
            parent.getChildren().remove(parent.getChildren().size() - 1);
            isCityPanelOpen = false;
            if(hasCUnit() != null) {
                isCUnitPanelOpen = true;
                combatUnitPanel(hasCUnit());
            }
            else if(hasNCUnit() != null) {
                isNCUnitPanelOpen = true;
                nonCombatUnitPanel(hasNCUnit());
            }
        }
        else if(isCUnitPanelOpen) {
            parent.getChildren().remove(parent.getChildren().size() - 1);
            isCUnitPanelOpen = false;
            if(hasNCUnit() != null) {
                isNCUnitPanelOpen = true;
                nonCombatUnitPanel(hasNCUnit());
            }
        }
        else if(isNCUnitPanelOpen) {
            parent.getChildren().remove(parent.getChildren().size() - 1);
            isNCUnitPanelOpen = false;
            gameController.getPlayerTurn().setSelectedUnit(null);
        }
        else {
            if(hasCity() != null) {
                isCityPanelOpen = true;
                cityPanel(hasCity());
            }
            else if (hasCUnit() != null) {
                isCUnitPanelOpen = true;
                combatUnitPanel(hasCUnit());
            }
            else if (hasNCUnit() != null) {
                isNCUnitPanelOpen = true;
                nonCombatUnitPanel(hasNCUnit());
            }
        }
    }
    private void emptyTilePanel() {
        Pane list = new Pane();
        fade(list).play();
        panelsPaneStyle(list, position.X + 40, position.Y - 65, 200, 50);
        addLabelToPane(list, 10, -6, null, tile.getPosition().X + "," + tile.getPosition().Y);
        if (tile.getResource() != null)
            addLabelToPane(list, 10, 10, null, "resource: " + tile.getResource().getRESOURCE_TYPE().symbol +
                    " " +
                    tile.getResource().getRESOURCE_TYPE().toString());
        else
            addLabelToPane(list, 10, 10, null, "resource: nothing");
        if((isCityPanelOpen || isCUnitPanelOpen || isNCUnitPanelOpen))
            parent.getChildren().add(parent.getChildren().size() - 1, list);
        else
            parent.getChildren().add(list);
    }
    private void removeAllPanels() {
        isCUnitPanelOpen = false;
        isNCUnitPanelOpen = false;
        isCityPanelOpen = false;
    }
    private void combatUnitPanel(Unit unit) {
        Pane list = new Pane(), actions = new Pane();
        panelsPaneStyle2(list);
        actions.setPrefHeight(150);
        VBox box = new VBox(), photos = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        photos.setAlignment(Pos.CENTER);
        photos.setSpacing(12);
        box.setSpacing(6);

        //information
        addLabelToBox(unitCommands.unitType.regex + unit.toString(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitMP.regex + unit.getMovementPoints(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitHealth.regex + unit.getHealth(), box);
        addPhotoToBox(photos, "photos/gameIcons/health.png");
        addLabelToBox(unitCommands.unitState.regex + unit.getUnitState().symbol, box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitPosition.regex + unit.getTile().getPosition().X + ","
                + unit.getTile().getPosition().Y, box);
        addPhotoToBox(photos, "photos/gameIcons/target.png");
        list.getChildren().add(box);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(75);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(photos);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(50);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(2);

        //actions
        gameController.getPlayerTurn().setSelectedUnit(unit);
        addPhotoToPane(actions, 10, 10, "photos/gameIcons/unitActions/Shield.png", "fortify");
        actions.getChildren().get(0).setOnMousePressed(mouseEvent -> {
            String result = gameController.fortify();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.fortifyActivated.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 60, 10, "photos/gameIcons/unitActions/wakeUp.png", "wake up");
        actions.getChildren().get(1).setOnMousePressed(mouseEvent -> {
            String result = gameController.wake();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.wokeUp.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 110, 10, "photos/gameIcons/unitActions/Fire.png", "pillage");
        actions.getChildren().get(2).setOnMousePressed(mouseEvent -> {
            String result = gameController.pillage();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.destroyImprovement.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 160, 10, "photos/gameIcons/unitActions/remove.png", "remove");
        actions.getChildren().get(3).setOnMousePressed(mouseEvent -> {
            String result = gameController.delete();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.endsWith(unitCommands.gold.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 10, 60, "photos/gameIcons/unitActions/Move.png", "move");
        actions.getChildren().get(4).setOnMousePressed(mouseEvent -> {
            TextField textField = new TextField();
            textField.setPromptText("enter coordinates...");
            actions.getChildren().add(textField);
            actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(300);
            actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(200);
            textField.setOnKeyPressed(keyEvent -> {
                String keyName = keyEvent.getCode().getName();
                if(keyName.equals("Enter")) {
                    Matcher matcher;
                    if (textField.getText() == null)
                        matcher = null;
                    else
                        matcher = unitCommands.compareRegex(textField.getText(), unitCommands.moveTo);
                    String result = gameController.moveUnit(matcher);
                    if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                        list.getChildren().remove(list.getChildren().size() - 1);
                    addLabelToPane(list, 300, 200, null, result);
                    if (result.equals(unitCommands.moveSuccessfull.regex)) {
                        removeAllPanels();
                        list.getChildren().remove(textField);
                        parent.getChildren().remove(list);
                    }
                    if (textField.getText().equals("-")) {
                        actions.getChildren().remove(textField);
                        if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                            list.getChildren().remove(list.getChildren().size() - 1);
                    }
                    textField.setText(null);
                }
            });
        });
        addPhotoToPane(actions, 10, 110, "photos/gameIcons/unitActions/Sleep.png", "sleep");
        actions.getChildren().get(5).setOnMousePressed(mouseEvent -> {
            String result = gameController.sleep();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.slept.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 60, 60, "photos/gameIcons/unitActions/garrison.png", "garrison");
        actions.getChildren().get(6).setOnMousePressed(mouseEvent -> {
            String result = gameController.garrison();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.garissonSet.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 60, 110, "photos/gameIcons/unitActions/Fight.png", "fight");
        actions.getChildren().get(7).setOnMousePressed(mouseEvent -> {
                    TextField textField = new TextField();
                    textField.setPromptText("enter coordinates...");
                    actions.getChildren().add(textField);
                    actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(300);
                    actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(200);
                    textField.setOnKeyPressed(keyEvent -> {
                        String keyName = keyEvent.getCode().getName();
                        if (keyName.equals("Enter")) {
                            Matcher matcher;
                            if (textField.getText() == null)
                                matcher = null;
                            else
                                matcher = unitCommands.compareRegex(textField.getText(), unitCommands.attack);
                            String result = gameController.attackCity(matcher);
                            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                list.getChildren().remove(list.getChildren().size() - 1);
                            if (result != null)
                                addLabelToPane(list, 300, 200, null, result);
                            if(result.equals(unitCommands.successfullAttack.regex) || result.equals(null))
                            {
                                removeAllPanels();
                                list.getChildren().remove(textField);
                                parent.getChildren().remove(list);
                            }
                            if (textField.getText().equals("-")) {
                                actions.getChildren().remove(textField);
                                if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                    list.getChildren().remove(list.getChildren().size() - 1);
                            }
                            textField.setText(null);
                        }
                    });
                });
        addPhotoToPane(actions, 110, 60, "photos/gameIcons/unitActions/readyToFight.png", "ready");
        actions.getChildren().get(8).setOnMousePressed(mouseEvent -> {
            TextField textField = new TextField();
            textField.setPromptText("enter coordinates...");
            actions.getChildren().add(textField);
            actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(300);
            actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(200);
            textField.setOnKeyPressed(keyEvent -> {
                        String keyName = keyEvent.getCode().getName();
                        if (keyName.equals("Enter")) {
                            Matcher matcher;
                            if (textField.getText() == null)
                                matcher = null;
                            else
                                matcher = unitCommands.compareRegex(textField.getText(), unitCommands.setup);
                            String result = gameController.setup(matcher);
                            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                list.getChildren().remove(list.getChildren().size() - 1);
                            addLabelToPane(list, 300, 200, null, result);
                            if (result.equals(unitCommands.setupSuccessful.regex)) {
                                removeAllPanels();
                                parent.getChildren().remove(list);
                            }
                            if (textField.getText().equals("-")) {
                                actions.getChildren().remove(textField);
                                if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                    list.getChildren().remove(list.getChildren().size() - 1);
                            }
                            textField.setText(null);
                        }
                    });
        });
        addPhotoToPane(actions, 110, 110, "photos/gameIcons/unitActions/alert.png", "alert");
        actions.getChildren().get(9).setOnMousePressed(mouseEvent -> {
            String result = gameController.alert();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.alerted.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 160, 60, "photos/gameIcons/unitActions/fortifyTilHeal.png", "fortify til heal");
        actions.getChildren().get(10).setOnMousePressed(mouseEvent -> {
            String result = gameController.fortifyTilHeal();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.fortifyHealActivated.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 160, 110, "photos/gameIcons/unitActions/Close.png", "close");
        actions.getChildren().get(11).setOnMousePressed(mouseEvent -> {
            String result = gameController.cancel();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.cancelCommand.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        list.getChildren().add(actions);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(300);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(13);
        parent.getChildren().add(list);
    }
    private void nonCombatUnitPanel(Unit unit) {
        Pane list = new Pane(), actions = new Pane();
        panelsPaneStyle2(list);
        actions.setPrefHeight(150);
        VBox box = new VBox(), photos = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        photos.setAlignment(Pos.CENTER);
        photos.setSpacing(12);
        box.setSpacing(6);

        //information
        addLabelToBox(unitCommands.unitType.regex + unit.toString(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitMP.regex + unit.getMovementPoints(), box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitHealth.regex + unit.getHealth(), box);
        addPhotoToBox(photos, "photos/gameIcons/health.png");
        addLabelToBox(unitCommands.unitState.regex + unit.getUnitState().symbol, box);
        addPhotoToBox(photos, null);
        addLabelToBox(unitCommands.unitPosition.regex + unit.getTile().getPosition().X + ","
                + unit.getTile().getPosition().Y, box);
        addPhotoToBox(photos, "photos/gameIcons/target.png");
        list.getChildren().add(box);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(75);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(photos);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(50);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(2);

        //actions
        gameController.getPlayerTurn().setSelectedUnit(unit);
        addPhotoToPane(actions, 10, 10, "photos/gameIcons/unitActions/wakeUp.png", "wake up");
        actions.getChildren().get(0).setOnMousePressed(mouseEvent -> {
            String result = gameController.wake();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.wokeUp.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 60, 10, "photos/gameIcons/unitActions/remove.png", "remove");
        actions.getChildren().get(1).setOnMousePressed(mouseEvent -> {
            String result = gameController.delete();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.endsWith(unitCommands.gold.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 110, 10, "photos/gameIcons/unitActions/Move.png", "move");
        actions.getChildren().get(2).setOnMousePressed(mouseEvent -> {
            TextField textField = new TextField();
            textField.setPromptText("enter coordinates...");
            actions.getChildren().add(textField);
            actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(300);
            actions.getChildren().get(actions.getChildren().size() - 1).setLayoutX(200);
            textField.setOnKeyPressed(keyEvent -> {
                String keyName = keyEvent.getCode().getName();
                if(keyName.equals("Enter")) {
                    Matcher matcher;
                    if (textField.getText() == null)
                        matcher = null;
                    else
                        matcher = unitCommands.compareRegex(textField.getText(), unitCommands.moveTo);
                    String result = gameController.moveUnit(matcher);
                    if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                        list.getChildren().remove(list.getChildren().size() - 1);
                    addLabelToPane(list, 300, 200, null, result);
                    if (result.equals(unitCommands.moveSuccessfull.regex)) {
                        removeAllPanels();
                        list.getChildren().remove(textField);
                        parent.getChildren().remove(list);
                    }
                    if (textField.getText().equals("-")) {
                        actions.getChildren().remove(textField);
                        if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                            list.getChildren().remove(list.getChildren().size() - 1);
                    }
                    textField.setText(null);
                }
            });
        });
        addPhotoToPane(actions, 10, 60, "photos/gameIcons/unitActions/Sleep.png", "sleep");
        actions.getChildren().get(3).setOnMousePressed(mouseEvent -> {
            String result = gameController.sleep();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.slept.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        addPhotoToPane(actions, 60, 60, "photos/gameIcons/unitActions/Close.png", "close");
        actions.getChildren().get(4).setOnMousePressed(mouseEvent -> {
            String result = gameController.cancel();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.cancelCommand.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
            }
        });
        if(unit.getClass().equals(Settler.class)) {
            addPhotoToPane(actions, 110, 60, "photos/gameIcons/unitActions/city.png", "found city");
            actions.getChildren().get(5).setOnMousePressed(mouseEvent -> {
                String result = gameController.found();
                if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                    list.getChildren().remove(list.getChildren().size() - 1);
                addLabelToPane(list, 300, 200, null, result);
                if (result.equals(unitCommands.cityBuilt.regex)) {

                    removeAllPanels();
                    parent.getChildren().remove(list);
                }
            });
        }
        else {
            addPhotoToPane(actions, 110, 60, "photos/gameIcons/unitActions/build.png", "build");
            actions.getChildren().get(5).setOnMousePressed(mouseEvent -> {
                buildPanel();
            });
        }

        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        list.getChildren().add(actions);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(300);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(13);
        parent.getChildren().add(list);
    }
    private void onBuildClicked(Pane list, String result, String matchResult) {
        if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
            list.getChildren().remove(list.getChildren().size() - 1);
        addLabelToPane(list, 300, 200, null, result);
        if (result.endsWith(matchResult)) {
            removeAllPanels();
            parent.getChildren().remove(list);
            for (int i = 0; i < parent.getChildren().size(); i++)
                parent.getChildren().get(i).setDisable(false);
        }
    }
    private void buildPanel() {
        Pane list = new Pane(), actions = new Pane();
        panelsPaneStyle(list, 320, 50, 170, 300);

        addPhotoToPane(list, 20, 50, "photos/gameIcons/workers/Road.png", "road");
        list.getChildren().get(0).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.road(), unitCommands.roadBuilt.regex));
        addPhotoToPane(list, 20, 100, "photos/gameIcons/workers/Railroad.png", "railroad");
        list.getChildren().get(1).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.railRoad(), unitCommands.railRoadBuilt.regex));
        addPhotoToPane(list, 20, 150, "photos/gameIcons/workers/Farm.png", "farm");
        list.getChildren().get(2).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.farm(), unitCommands.farmBuild.regex));
        addPhotoToPane(list, 20, 200, "photos/gameIcons/workers/Mine.png", "mine");
        list.getChildren().get(3).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.mine(), unitCommands.mineBuild.regex));
        addPhotoToPane(list, 20, 250, "photos/gameIcons/workers/TradingPost.png", "trading post");
        list.getChildren().get(4).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.tradingPost(), unitCommands.tradingPostBuild.regex));
        addPhotoToPane(list, 70, 50, "photos/gameIcons/workers/LumberMill.png", "lumber mill");
        list.getChildren().get(5).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.lumberMill(), unitCommands.lumberMillBuild.regex));
        addPhotoToPane(list, 70, 100, "photos/gameIcons/workers/factory.png", "factory");
        list.getChildren().get(6).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.factory(), unitCommands.factoryBuild.regex));
        addPhotoToPane(list, 70, 150, "photos/gameIcons/workers/Camp.png", "camp");
        list.getChildren().get(7).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.camp(), unitCommands.campBuild.regex));
        addPhotoToPane(list, 70, 200, "photos/gameIcons/workers/Pasture.png", "Pasture");
        list.getChildren().get(8).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.pasture(), unitCommands.pastureBuild.regex));
        addPhotoToPane(list, 70, 250, "photos/gameIcons/workers/Plantation.png", "Plantation");
        list.getChildren().get(9).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.plantation(), unitCommands.plantationBuild.regex));
        addPhotoToPane(list, 120, 50, "photos/gameIcons/workers/Quarry.png", "quarry");
        list.getChildren().get(10).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.quarry(), unitCommands.quarryBuild.regex));
        addPhotoToPane(list, 120, 100, "photos/gameIcons/workers/routeRemove.png", "route remove");
        list.getChildren().get(11).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.removeRoute(), unitCommands.roadRemoved.regex));
        addPhotoToPane(list, 120, 150, "photos/gameIcons/workers/forestRemove.png", "forest remove");
        list.getChildren().get(12).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.removeJungle(), unitCommands.jungleRemoved.regex));
        addPhotoToPane(list, 120, 200, "photos/gameIcons/workers/repair.png", "repair");
        list.getChildren().get(13).setOnMousePressed(mouseEvent -> onBuildClicked(list, gameController.repair(), unitCommands.repairedSuccessful.regex));


        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        list.getChildren().add(actions);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(300);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(13);
        parent.getChildren().add(list);
        for (int i = 0; i < parent.getChildren().size() - 1; i++)
            parent.getChildren().get(i).setDisable(true);
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

        //lock and unlock citizens to tile
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

        //purchase tile
        addLabelToPane(list, 400, 44, null, "purchase tile");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            purchaseTilePanel(city);
            list.setDisable(true);
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private void purchaseTilePanel(City city) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 300, 450, 170);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 130, 25);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        addLabelToBox("you can purchase tile here", box);
        addLabelToBox("your gold: " + city.getRulerPlayer().getGold(), box);
        addLabelToBox("enter position of tile here: ", box);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                Matcher matcher = selectCommands.compareRegex(textField.getText(), selectCommands.buyTile);
                gameController.getPlayerTurn().setSelectedCity(city);
                String result = gameController.buyTile(matcher);
                if(box.getChildren().size() == 4)
                    addLabelToBox(result, box);
                else {
                    box.getChildren().remove(box.getChildren().size() - 1);
                    addLabelToBox(result, box);
                }
                if(result.equals(gameEnum.buyTile.regex)) {
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
            gameController.getPlayerTurn().setSelectedUnit(null);
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
    private void addPhotoToPane(Pane box, int x, int y, String url, String information) {
        if (url == null)
            addLabelToPane(box, x, y, null, "");
        else {
            ImageView imageView = new ImageView();
            imageView.setImage(new Image(url));
            imageView.setFitWidth(25);
            imageView.setFitHeight(25);
            box.getChildren().add(imageView);
            box.getChildren().get(box.getChildren().size() - 1).setLayoutX(x);
            box.getChildren().get(box.getChildren().size() - 1).setLayoutY(y);
            imageView.setOnMouseMoved(mouseEvent -> {
                if(box.getChildren().get(box.getChildren().size() - 1).getClass() != Label.class) {
                    addLabelToPane(box, x + 22, y, null, information);
                    fade(box.getChildren().get(box.getChildren().size() - 1)).play();
                }
            });
            imageView.setOnMouseExited(mouseEvent -> box.getChildren().remove(box.getChildren().size() - 1));
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

