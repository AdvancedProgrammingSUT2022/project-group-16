import Controllers.CommandHandler;
import Models.City.Building;
import Models.City.BuildingType;
import Models.City.City;
import Models.Player.*;
import Models.Resources.LuxuryResource;
import Models.Resources.Resource;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import Models.Units.CombatUnits.*;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.NonCombatUnits.Worker;
import Models.Units.Unit;
import enums.gameCommands.infoCommands;
import enums.gameCommands.selectCommands;
import enums.gameCommands.unitCommands;
import enums.gameEnum;
import enums.mainCommands;
import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class Hex extends Application {
    private static Pane parent;
    private final Pane pane;
    private Position position;
    private Tile tile;
    private TileState tileState;
    private ArrayList<ImageView> hexElements = new ArrayList<>();
    private ImageView tileImageView;
    private CommandHandler commandHandler = CommandHandler.getInstance();
    private Player player;
    private boolean isBannerOpen = false;
    private boolean isCityPanelOpen = false;
    private boolean isCUnitPanelOpen = false;
    private boolean isNCUnitPanelOpen = false;
    private Unit selectedUnit = null;
    private BuildingType selectedBuildingType = null;
    private Game game;
    private final AudioClip attackAudio = new AudioClip(Hex.class.getResource("audio/gameAudios/cannon.mp3").toExternalForm());
    private final AudioClip declareWarAudio = new AudioClip(Hex.class.getResource("audio/gameAudios/declareWar.mp3").toExternalForm());
    private final AudioClip addEnemy = new AudioClip(Hex.class.getResource("audio/gameAudios/addEnemy.mp3").toExternalForm());
    private final AudioClip constructionAudio = new AudioClip(Hex.class.getResource("audio/gameAudios/construction.mp3").toExternalForm());

    public Hex(Position position, Player player, Game game){
        this.player = player;
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
        this.game = game;

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
        setBuildings();
        setImprovements();
        setCUnits();
        setNCUnits();
        setEnemyUnitsBorder();
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

        Player playerTurn = player;


        for (Player player : commandHandler.getPlayers())
            for (Integer cityBorderIndex : player.getCityBorderIndexes(tile))
            {
                String url;
                if(player.getCivilization().equals(playerTurn.getCivilization()))
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
            case BANANA -> url += "Bananas";
            case CATTLE -> url += "Cattle";
            case DEER -> url += "Deer";
            case SHEEP -> url += "Sheep";
            case WHEAT -> url += "Wheat";
            case COAL -> url += "Coal";
            case HORSES -> url += "Horses";
            case IRON -> url += "Iron";
            case COTTON -> url += "Cotton";
            case DYES -> url += "Dyes";
            case FURS -> url += "Furs";
            case GEMS -> url += "Gems";
            case GOLD -> url += "Gold";
            case INCENSE -> url += "Incense";
            case IVORY -> url += "Ivory";
            case MARBLE -> url += "Marble";
            case SILK -> url += "Silk";
            case SILVER -> url += "Silver";
            case SUGAR -> url += "Sugar";
        }
        url += ".png";
        setImage(url, 0, 50, 30, 30);
    }

    private void setCUnits()
    {
        if(tile.getCombatUnitInTile() == null || tileState.equals(TileState.FOG_OF_WAR))
            return;

        String url = "/photos/units/";
        if(tile.getCombatUnitInTile() instanceof LongRange)
            switch (((LongRange) tile.getCombatUnitInTile()).getType())
            {
                case ARCHER -> url += "archer";
                case CHARIOT_A -> url += "chariotarcher";
                case CATAPULT -> url += "catapult";
                case CROSSMAN -> url += "crossbowman";
                case TREBUCHET -> url += "trebuchet";
                case CANON -> url += "cannon";
                case ARTILLERY -> url += "artillery";
            }
        else if(tile.getCombatUnitInTile() instanceof  MidRange)
            switch (((MidRange) tile.getCombatUnitInTile()).getType())
            {
                case SCOUT -> url += "scout";
                case SPEARMAN -> url += "spearman";
                case WARRIOR -> url += "warrior";
                case HORSEMAN -> url += "horseman";
                case SWORDSMAN -> url += "swordsman";
                case KNIGHT -> url += "knight";
                case LSWORDSMAN -> url += "longswordsman";
                case PIKE_MAN -> url += "pikeman";
                case CAVALRY -> url += "cavalry";
                case LANCER -> url += "lancer";
                case MUSKET_MAN -> url += "musketman";
                case RIFLEMAN -> url += "rifleman";
                case ANTI_TANK -> url += "antitankgun";
                case INFANTRY -> url += "infantry";
                case PANZER -> url += "panzer";
                case TANK -> url += "tank";
            }
        url += ".png";
        setImage(url, 40, 0, 60, 60);
    }

    private void setNCUnits()
    {
        if(tile.getNonCombatUnitInTile() == null || tileState.equals(TileState.FOG_OF_WAR))
            return;

        String url = "/photos/units/";
        if (tile.getNonCombatUnitInTile() instanceof Worker)
            url += "worker.png";
        else if (tile.getNonCombatUnitInTile() instanceof Settler)
            url += "settler.png";

        setImage(url, 60 , 60, 30, 30);
    }
    private void setEnemyUnitsBorder()
    {
        if(tileState.equals(TileState.FOG_OF_WAR))
            return;

        ArrayList<Tile> adjacentTiles = player.getAdjacentTiles(tile, 1);
        for (Tile adjacentTile : adjacentTiles)
        {
            if(adjacentTile.getCombatUnitInTile() == null || player.getUnits().contains(adjacentTile.getCombatUnitInTile()))
                continue;
            if(adjacentTile.getPosition().Q == tile.getPosition().Q && adjacentTile.getPosition().R == tile.getPosition().R - 1)
            {
                // south neighbor
                String url = "/photos/Tiles/enemyBorder";
                setImage(url + "3.png", -8,  -8, 106, 106);
                setImage(url + "2.png", -8,  -8, 106, 106);
                setImage(url + "4.png", -8,  -8, 106, 106);
            }
            else if(adjacentTile.getPosition().Q == tile.getPosition().Q && adjacentTile.getPosition().R == tile.getPosition().R + 1)
            {
                // north neighbor
                String url = "/photos/Tiles/enemyBorder";
                setImage(url + "0.png", -8,  -8, 106, 106);
                setImage(url + "1.png", -8,  -8, 106, 106);
                setImage(url + "5.png", -8,  -8, 106, 106);
            }
            else if(adjacentTile.getPosition().Q == tile.getPosition().Q - 1 && adjacentTile.getPosition().R == tile.getPosition().R)
            {
                // south-east neighbor
                String url = "/photos/Tiles/enemyBorder";
                setImage(url + "4.png", -8,  -8, 106, 106);
                setImage(url + "3.png", -8,  -8, 106, 106);
                setImage(url + "5.png", -8,  -8, 106, 106);
            }
            else if(adjacentTile.getPosition().Q == tile.getPosition().Q + 1 && adjacentTile.getPosition().R == tile.getPosition().R)
            {
                // north-west neighbor
                String url = "/photos/Tiles/enemyBorder";
                setImage(url + "1.png", -8,  -8, 106, 106);
                setImage(url + "0.png", -8,  -8, 106, 106);
                setImage(url + "2.png", -8,  -8, 106, 106);
            }
            else if(adjacentTile.getPosition().Q == tile.getPosition().Q + 1 && adjacentTile.getPosition().S == tile.getPosition().S)
            {
                // south-west neighbor
                String url = "/photos/Tiles/enemyBorder";
                setImage(url + "2.png", -8,  -8, 106, 106);
                setImage(url + "1.png", -8,  -8, 106, 106);
                setImage(url + "3.png", -8,  -8, 106, 106);
            }
            else if(adjacentTile.getPosition().Q == tile.getPosition().Q - 1 && adjacentTile.getPosition().S == tile.getPosition().S)
            {
                // north-east neighbor
                String url = "/photos/Tiles/enemyBorder";
                setImage(url + "5.png", -8,  -8, 106, 106);
                setImage(url + "0.png", -8,  -8, 106, 106);
                setImage(url + "4.png", -8,  -8, 106, 106);
            }
        }
    }

    private void setBuildings()
    {
        if(tileState.equals(TileState.FOG_OF_WAR))
            return;

        for (City city : player.getCities())
            for (Building building : city.getBuildings())
                if(building.getTile().getPosition().equals(tile.getPosition()))
                {
                    String url = "/photos/Buildings/";

                    switch (building.getBuildingType())
                    {
                       case BARRACKS -> url += "barracks";
                       case GRANARY -> url += "granary";
                       case LIBRARY -> url += "library";
                       case MONUMENT -> url += "monument";
                       case WALLS -> url += "walls";
                       case WATER_MILL -> url += "watermill";
                       case ARMORY -> url += "armory";
                       case BURIAL_TOMB -> url += "burialtomb";
                       case CIRCUS -> url += "circus";
                       case COLOSSEUM -> url += "colosseum";
                       case COURTHOUSE -> url += "courthouse";
                       case STABLE -> url += "stable";
                       case TEMPLE -> url += "temple";
                       case CASTLE -> url += "castle";
                       case FORGE -> url += "forge";
                       case GARDEN -> url += "garden";
                       case MARKET -> url += "market";
                       case MINT -> url += "mint";
                       case MONASTERY -> url += "monastery";
                       case UNIVERSITY -> url += "university";
                       case WORKSHOP -> url += "workshop";
                       case BANK -> url += "bank";
                       case MILITARY_ACADEMY -> url += "militaryacademy";
                       case OPERA_HOUSE -> url += "operahouse";
                       case MUSEUM -> url += "museum";
                       case PUBLIC_SCHOOL -> url += "publicschool";
                       case SATRAPS_COURT -> url += "satrapscourt";
                       case THEATER -> url += "theatre";
                       case WINDMILL -> url += "windmill";
                       case ARSENAL -> url += "arsenal";
                       case BROADCAST_TOWER -> url += "broadcasttower";
                       case FACTORY -> url += "factory";
                       case HOSPITAL -> url += "hospital";
                       case MILITARY_BASE -> url += "militarybase";
                       case STOCK_EXCHANGE -> url += "stockexchange";
                       case PALACE -> url += "palace";
                       default -> {throw new RuntimeException("Unknown building type");}
                   }
                    url += ".png";

                    setImage(url, 30, 50, 40, 40);
                    return;
                }
    }

    private void setImprovements()
    {
        if(tileState.equals(TileState.FOG_OF_WAR) || tile.getImprovement() == null || tile.getImprovement().equals(Improvement.NONE))
            return;

        String url = "/photos/Improvements/";
        switch (tile.getImprovement())
        {
            case CAMP -> url += "Camp";
            case FARM -> url += "Farm";
            case MINE -> url += "Mine";
            case LUMBER_MILL -> url += "LumberMill";
            case TRADING_POST -> url += "TradingPost";
            case PASTURE -> url += "Pasture";
            case PLANTATION -> url += "Plantation";
            case QUARRY -> url += "Quarry";
            case FACTORY -> url += "Manufactory";
            default -> {throw new RuntimeException("Unknown improvement type");}
        }
        url += ".png";
        setImage(url, 0, 0, 20, 20);
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
        setImage(url,  10, 10, 30, 30 );
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

        if(player.getMap().get(this.tile).equals(TileState.REVEALED)){
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
        else if(!isBannerOpen && !player.getMap().get(tile).equals(TileState.FOG_OF_WAR)){
            isBannerOpen = true;
            emptyTilePanel();
        }
    }
    private void cityBanner(City city) {
        Pane list = new Pane();
        fade(list).play();
        panelsPaneStyle(list, position.X + 40, position.Y - 80, 200, 50);
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
        for (City city : player.getCities())
            if(city.getCapitalTile().getPosition() == tile.getPosition())
                return city;
        return null;
    }
    private Unit hasCUnit() {
        for (Unit unit : player.getUnits())
            if(unit.getTile().getPosition() == tile.getPosition() && unit.getPower() != 0)
                return unit;
        return null;
    }
    private Unit hasNCUnit() {
        for (Unit unit : player.getUnits())
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
            commandHandler.selectCUnit("", "");
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
        panelsPaneStyle(list, position.X + 40, position.Y - 140, 400, 105);
        addLabelToPane(list, 10, 10, "photos/gameIcons/Food.png", String.valueOf(tile.getTileFeature().food));
        addLabelToPane(list, 50, 10, "photos/gameIcons/Production.png", String.valueOf(tile.getTileFeature().production));
        addLabelToPane(list, 90, 10, "photos/gameIcons/Gold.png", String.valueOf(tile.getTileFeature().gold));
        addLabelToPane(list, 130, 10, "photos/gameIcons/Resistance.png", String.valueOf(tile.getTileFeature().combatModifier));
        addLabelToPane(list, 170, 10, "photos/gameIcons/target.png", String.valueOf(tile.getTileFeature().movementCost));

        addLabelToPane(list, 210, -6, null, tile.getPosition().X + "," + tile.getPosition().Y);
        if (tile.getResource() != null) {
            addLabelToPane(list, 210, 65, null, "resource: " + tile.getResource().getRESOURCE_TYPE().symbol +
                    " " +
                    tile.getResource().getRESOURCE_TYPE().toString());
            addLabelToPane(list, 10, 65, "photos/gameIcons/Food.png", String.valueOf(tile.getResource().getRESOURCE_TYPE().food));
            addLabelToPane(list, 50, 65, "photos/gameIcons/Production.png", String.valueOf(tile.getResource().getRESOURCE_TYPE().production));
            addLabelToPane(list, 90, 65, "photos/gameIcons/Gold.png", String.valueOf(tile.getResource().getRESOURCE_TYPE().gold));
        }
        else {
            addLabelToPane(list, 210, 65, null, "resource: nothing");
            addLabelToPane(list, 10, 65, "photos/gameIcons/Food.png", "-");
            addLabelToPane(list, 50, 65, "photos/gameIcons/Production.png", "-");
            addLabelToPane(list, 90, 65, "photos/gameIcons/Gold.png", "-");
        }
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
        // here
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
        addLabelToBox("unit XP: " + String.valueOf(unit.getXP()), box);
        addPhotoToBox(photos, "photos/gameIcons/Science.png");
        list.getChildren().add(box);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(75);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        list.getChildren().add(photos);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(50);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(2);

        //actions
        commandHandler.selectCUnit(String.valueOf(unit.getTile().getPosition().X), String.valueOf(unit.getTile().getPosition().Y));
        addPhotoToPane(actions, 10, 10, "photos/gameIcons/unitActions/Shield.png", "fortify");
        actions.getChildren().get(0).setOnMousePressed(mouseEvent -> {
            // here
            String result = commandHandler.fortify();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.fortifyActivated.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 60, 10, "photos/gameIcons/unitActions/wakeUp.png", "wake up");
        actions.getChildren().get(1).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.wake();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.wokeUp.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 110, 10, "photos/gameIcons/unitActions/Fire.png", "pillage");
        actions.getChildren().get(2).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.pillage();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.destroyImprovement.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 160, 10, "photos/gameIcons/unitActions/remove.png", "remove");
        actions.getChildren().get(3).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.delete();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.endsWith(unitCommands.gold.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
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
                    String result = commandHandler.moveUnit(textField.getText());
                    if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                        list.getChildren().remove(list.getChildren().size() - 1);
                    addLabelToPane(list, 300, 200, null, result);
                    if (result.equals(unitCommands.moveSuccessfull.regex)) {
                        attackAudio.play();
                        removeAllPanels();
                        list.getChildren().remove(textField);
                        parent.getChildren().remove(list);
                        game.updateScreen();
                    }
                    else if (result.equals(gameEnum.MP.regex)) {
                        attackAudio.play();
                        removeAllPanels();
                        list.getChildren().remove(textField);
                        parent.getChildren().remove(list);
                        game.updateScreen();
                    }
                    else if (result.equals(gameEnum.notYourTile.regex)) {
                        declareWarAudio.play();
                        declareWarPanel(matcher);
                    }
                    if (textField.getText().equals("-")) {
                        actions.getChildren().remove(textField);
                        if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                            list.getChildren().remove(list.getChildren().size() - 1);
                    }
                    textField.setText(null);

                    game.updateScreen();
                }
            });
        });
        addPhotoToPane(actions, 10, 110, "photos/gameIcons/unitActions/Sleep.png", "sleep");
        actions.getChildren().get(5).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.sleep();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.slept.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 60, 60, "photos/gameIcons/unitActions/garrison.png", "garrison");
        actions.getChildren().get(6).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.garrison();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.garissonSet.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
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
                            String result = commandHandler.attackCity(matcher);
                            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                list.getChildren().remove(list.getChildren().size() - 1);
                            if (result != null)
                                addLabelToPane(list, 300, 200, null, result);
                            if(result.equals(unitCommands.successfullAttack.regex) || result.equals(null))
                            {
                                removeAllPanels();
                                list.getChildren().remove(textField);
                                parent.getChildren().remove(list);
                                game.updateScreen();
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
                            String result = commandHandler.setup(matcher);
                            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                list.getChildren().remove(list.getChildren().size() - 1);
                            addLabelToPane(list, 300, 200, null, result);
                            if (result.equals(unitCommands.setupSuccessful.regex)) {
                                removeAllPanels();
                                parent.getChildren().remove(list);
                                game.updateScreen();
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
            String result = commandHandler.alert();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.alerted.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 160, 60, "photos/gameIcons/unitActions/fortifyTilHeal.png", "fortify til heal");
        actions.getChildren().get(10).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.fortifyTilHeal();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.fortifyHealActivated.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 160, 110, "photos/gameIcons/unitActions/Close.png", "close");
        actions.getChildren().get(11).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.cancel();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.cancelCommand.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
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
        // here
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
        commandHandler.selectNCUNit(String.valueOf(unit.getTile().getPosition().X), String.valueOf(unit.getTile().getPosition().Y));
        addPhotoToPane(actions, 10, 10, "photos/gameIcons/unitActions/wakeUp.png", "wake up");
        actions.getChildren().get(0).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.wake();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.wokeUp.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();

            }
        });
        addPhotoToPane(actions, 60, 10, "photos/gameIcons/unitActions/remove.png", "remove");
        actions.getChildren().get(1).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.delete();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.endsWith(unitCommands.gold.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
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
                    String result = commandHandler.moveUnit(textField.getText());
                    if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                        list.getChildren().remove(list.getChildren().size() - 1);
                    addLabelToPane(list, 300, 200, null, result);
                    if (result.equals(unitCommands.moveSuccessfull.regex)) {
                        removeAllPanels();
                        list.getChildren().remove(textField);
                        parent.getChildren().remove(list);
                        game.updateScreen();
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
            String result = commandHandler.sleep();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(gameEnum.slept.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        addPhotoToPane(actions, 60, 60, "photos/gameIcons/unitActions/Close.png", "close");
        actions.getChildren().get(4).setOnMousePressed(mouseEvent -> {
            String result = commandHandler.cancel();
            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                list.getChildren().remove(list.getChildren().size() - 1);
            addLabelToPane(list, 300, 200, null, result);
            if (result.equals(unitCommands.cancelCommand.regex)) {
                removeAllPanels();
                parent.getChildren().remove(list);
                game.updateScreen();
            }
        });
        if(unit.getClass().equals(Settler.class)) {
            addPhotoToPane(actions, 110, 60, "photos/gameIcons/unitActions/city.png", "found city");
            actions.getChildren().get(5).setOnMousePressed(mouseEvent -> {
                constructionAudio.play();
                String result = commandHandler.found();
                if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                    list.getChildren().remove(list.getChildren().size() - 1);
                addLabelToPane(list, 300, 200, null, result);
                if (result.equals(unitCommands.cityBuilt.regex)) {
                    removeAllPanels();
                    parent.getChildren().remove(list);
                    game.updateScreen();
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
            game.updateScreen();
        }
    }
    private void buildPanel() {
        // here
        Pane list = new Pane(), actions = new Pane();
        panelsPaneStyle(list, 320, 50, 170, 300);

        constructionAudio.play();
        addPhotoToPane(list, 20, 50, "photos/gameIcons/workers/Road.png", "road");
        list.getChildren().get(0).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.road(), unitCommands.roadBuilt.regex));
        addPhotoToPane(list, 20, 100, "photos/gameIcons/workers/Railroad.png", "railroad");
        list.getChildren().get(1).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.railRoad(), unitCommands.railRoadBuilt.regex));
        addPhotoToPane(list, 20, 150, "photos/gameIcons/workers/Farm.png", "farm");
        list.getChildren().get(2).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.farm(), unitCommands.farmBuild.regex));
        addPhotoToPane(list, 20, 200, "photos/gameIcons/workers/Mine.png", "mine");
        list.getChildren().get(3).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.mine(), unitCommands.mineBuild.regex));
        addPhotoToPane(list, 20, 250, "photos/gameIcons/workers/TradingPost.png", "trading post");
        list.getChildren().get(4).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.tradingPost(), unitCommands.tradingPostBuild.regex));
        addPhotoToPane(list, 70, 50, "photos/gameIcons/workers/LumberMill.png", "lumber mill");
        list.getChildren().get(5).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.lumberMill(), unitCommands.lumberMillBuild.regex));
        addPhotoToPane(list, 70, 100, "photos/gameIcons/workers/factory.png", "factory");
        list.getChildren().get(6).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.factory(), unitCommands.factoryBuild.regex));
        addPhotoToPane(list, 70, 150, "photos/gameIcons/workers/Camp.png", "camp");
        list.getChildren().get(7).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.camp(), unitCommands.campBuild.regex));
        addPhotoToPane(list, 70, 200, "photos/gameIcons/workers/Pasture.png", "Pasture");
        list.getChildren().get(8).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.pasture(), unitCommands.pastureBuild.regex));
        addPhotoToPane(list, 70, 250, "photos/gameIcons/workers/Plantation.png", "Plantation");
        list.getChildren().get(9).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.plantation(), unitCommands.plantationBuild.regex));
        addPhotoToPane(list, 120, 50, "photos/gameIcons/workers/Quarry.png", "quarry");
        list.getChildren().get(10).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.quarry(), unitCommands.quarryBuild.regex));
        addPhotoToPane(list, 120, 100, "photos/gameIcons/workers/routeRemove.png", "route remove");
        list.getChildren().get(11).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.removeRoute(), unitCommands.roadRemoved.regex));
        addPhotoToPane(list, 120, 150, "photos/gameIcons/workers/forestRemove.png", "Feature remove");
        list.getChildren().get(12).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.removeFeature(), unitCommands.jungleRemoved.regex));
        addPhotoToPane(list, 120, 200, "photos/gameIcons/workers/repair.png", "repair");
        list.getChildren().get(13).setOnMousePressed(mouseEvent -> onBuildClicked(list, commandHandler.repair(), unitCommands.repairedSuccessful.regex));


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
        commandHandler.selectCity(String.valueOf(city.getCapitalTile().getPosition().X), String.valueOf(city.getCapitalTile().getPosition().Y));
        Pane list = new Pane();
        panelsPaneStyle2(list);
        VBox box = new VBox(), photos = new VBox();
        box.setAlignment(Pos.TOP_LEFT);
        photos.setAlignment(Pos.CENTER);
        photos.setSpacing(14);
        box.setSpacing(6);
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
            addLabelToBox(infoCommands.currentResearching.regex + player
                    .getResearchingTechnology().name(), box);
            addLabelToBox(infoCommands.remainingTurns.regex + ((player.getResearchingTechnology().
                    cost - player.getResearchingTechCounter()[flg]) / 10), box);
        }
        else {
            addLabelToBox(infoCommands.currentResearching.regex + infoCommands.nothing.regex, box);
            addLabelToBox(infoCommands.remainingTurns.regex + "-", box);
        }
        addLabelToBox(gameEnum.employedCitizens.regex + (city.employedCitizens()), box);
        addLabelToBox(gameEnum.unEmployedCitizens.regex + (city.getPopulation() - city.employedCitizens()), box);
        if(city.getCurrentConstruction() != null) {
            if (player.getSelectedCity().getCurrentConstruction() instanceof Building)
                addLabelToBox(gameEnum.currentConstruction.regex + ((Building) city.getCurrentConstruction()).getBuildingType().name(), box);
            else
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
        //purchase unit
        addLabelToPane(list, 400, 64, null, "purchase unit");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            purchaseUnitPanel(city);
            list.setDisable(true);
        });
        //purchase building
        addLabelToPane(list, 400, 84, null, "purchase building");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            purchaseBuildingPanel(city);
            list.setDisable(true);
        });
        //build building
        addLabelToPane(list, 400, 104, null, "build building");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            buildBuildingPanel(city);
            list.setDisable(true);
        });
        //build unit
        addLabelToPane(list, 400, 124, null, "build unit");
        list.getChildren().get(list.getChildren().size() - 1).setOnMousePressed(mouseEvent -> {
            buildUnitPanel(city);
            list.setDisable(true);
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private void purchaseUnitPanel(City city) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 300, 450, 230);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 130, 25);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        addLabelToBox("you can purchase unit here", box);
        addLabelToBox("your gold: " + city.getRulerPlayer().getGold(), box);
        addLabelToBox("enter name of unit here: ", box);
        addLabelToBox("note: enter all letters capital:)", box);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                commandHandler.selectCity(String.valueOf(city.getCapitalTile().getPosition().X), String.valueOf(city.getCapitalTile().getPosition().Y));
                // here
                String result = commandHandler.buyUnit(textField.getText());
                if(box.getChildren().size() == 5)
                    addLabelToBox(result, box);
                else {
                    box.getChildren().remove(box.getChildren().size() - 1);
                    addLabelToBox(result, box);
                }
                if(result.equals(gameEnum.unitBought.regex)) {
                    parent.getChildren().remove(list);
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    game.updateScreen();
                }
                textField.setText(null);
            }
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }
    private void declareWarPanel(Matcher matcher) {
        // here

        Pane yesOrNo = new Pane();
        panelsPaneStyle(yesOrNo, 490, 323, 300, 75);
        parent.getChildren().add(yesOrNo);
        addLabelToPane(yesOrNo, 100, -10, null, "declare war?");
        Button yes = new Button();
        yesOrNo.getChildren().add(yes);
        yesOrNo.getChildren().get(yesOrNo.getChildren().size() - 1).setLayoutX(70);
        yesOrNo.getChildren().get(yesOrNo.getChildren().size() - 1).setLayoutY(30);
        yes.setText("yes");
        yes.setStyle("-fx-background-color: green;" +
                "-fx-font-size: 17;" +
                "-fx-text-fill: white;" +
                "-fx-pref-width: 50;" +
                "-fx-pref-height: 30");
        yes.setOnMouseClicked(mouseEvent12 -> {
            addEnemy.play();
            parent.getChildren().remove(yesOrNo);
            parent.getChildren().remove(parent.getChildren().size() - 1);
            // here
            Tile destination = player.getTileByXY(Integer.parseInt(matcher.group("x")),
                    Integer.parseInt(matcher.group("y")));
            Player enemyPlayer = destination.GetTileRuler();
            Civilization enemy = enemyPlayer.getCivilization();
            player.getRelationStates().replace(enemy, RelationState.ENEMY);
            enemyPlayer.getRelationStates().replace(player.getCivilization(), RelationState.ENEMY);
            player.getSelectedUnit().move(destination);
            game.updateScreen();
        });
        Button no = new Button();
        yesOrNo.getChildren().add(no);
        yesOrNo.getChildren().get(yesOrNo.getChildren().size() - 1).setLayoutX(170);
        yesOrNo.getChildren().get(yesOrNo.getChildren().size() - 1).setLayoutY(30);
        no.setText("no");
        no.setStyle("-fx-background-color: red;" +
                "-fx-font-size: 17;" +
                "-fx-text-fill: white;" +
                "-fx-pref-width: 50;" +
                "-fx-pref-height: 30");
        no.setOnMouseClicked(mouseEvent1 -> {
            parent.getChildren().remove(yesOrNo);
            parent.getChildren().remove(parent.getChildren().size() - 1);
        });
    }
    private void purchaseBuildingPanel(City city) {
        Pane list = new Pane();
        panelsPaneStyle(list, 500, 300, 450, 230);
        VBox box = new VBox();
        list.getChildren().add(box);
        setCoordinatesBox(list, box, 130, 25);
        box.setSpacing(5);
        box.setAlignment(Pos.CENTER);
        addLabelToBox("you can purchase buildings here", box);
        addLabelToBox("your gold: " + city.getRulerPlayer().getGold(), box);
        addLabelToBox("enter name of building here: ", box);
        addLabelToBox("note: enter all letters capital:)", box);
        TextField textField = new TextField();
        box.getChildren().add(textField);
        textField.setOnKeyPressed(keyEvent -> {
            String keyName = keyEvent.getCode().getName();
            if(keyName.equals("Enter")) {
                commandHandler.selectCity(String.valueOf(city.getCapitalTile().getPosition().X), String.valueOf(city.getCapitalTile().getPosition().Y));
                // here
                String result = commandHandler.buyBuilding(textField.getText());
                if(box.getChildren().size() == 5)
                    addLabelToBox(result, box);
                else {
                    box.getChildren().remove(box.getChildren().size() - 1);
                    addLabelToBox(result, box);
                }
                if(result.equals(mainCommands.buildingBuilt.regex)) {
                    parent.getChildren().remove(list);
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    game.updateScreen();
                }
                textField.setText(null);
            }
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
                commandHandler.selectCity(String.valueOf(city.getCapitalTile().getPosition().X), String.valueOf(city.getCapitalTile().getPosition().Y));
                // here
                String result = commandHandler.buyTile(textField.getText());
                if(box.getChildren().size() == 4)
                    addLabelToBox(result, box);
                else {
                    box.getChildren().remove(box.getChildren().size() - 1);
                    addLabelToBox(result, box);
                }
                if(result.equals(gameEnum.buyTile.regex)) {
                    parent.getChildren().remove(list);
                    parent.getChildren().remove(parent.getChildren().size() - 1);
                    game.updateScreen();
                }
                textField.setText(null);
            }
        });
        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(15);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(15);
        parent.getChildren().add(list);
    }

    private boolean hasTechnology(City city, BuildingType buildingType) {
        return buildingType.requiredTechnology == null ||
                city.getRulerPlayer().getTechnologies().contains(buildingType.requiredTechnology);
    }
    private boolean hasTechnology(MidRangeType midRangeType) {
        return midRangeType.requiredTech == null ||
                player.getTechnologies().contains(midRangeType.requiredTech);
    }
    private boolean hasTechnology(LongRangeType longRangeType) {
        return longRangeType.requiredTech == null ||
                player.getTechnologies().contains(longRangeType.requiredTech);
    }
    private boolean hasResource(ResourceType resourceType) {
        for (Resource resource : player.getResources())
            if (resource.getRESOURCE_TYPE().equals(resourceType))
                return true;
        return false;
    }
    private boolean hasResource(MidRangeType midRangeType) {
        return midRangeType.requiredSource == null || hasResource(midRangeType.requiredSource);
    }
    private boolean hasResource(LongRangeType longRangeType) {
        return longRangeType.requiredSource == null || hasResource(longRangeType.requiredSource);
    }

    private boolean hasBuilding(City city, BuildingType buildingType) {
        if (buildingType.requiredBuilding == null)
            return true;
        for (Building building : city.getBuildings())
            if (building.getBuildingType().equals(buildingType))
                return true;
        return false;
    }
    private void buildBuildingPanel(City city) {
        // here
        Pane list = new Pane();
        panelsPaneStyle(list, 390, 165, 500, 500);
        addLabelToPane(list, 200, -5, null, "your gold: " + player.getGold());

        ArrayList<BuildingType> buildingTypes = new ArrayList<>();
        for (BuildingType buildingType : BuildingType.values())
            if (hasTechnology(city, buildingType) && hasBuilding(city, buildingType))
                buildingTypes.add(buildingType);

        int flag = 0;
        while (flag < buildingTypes.size()) {
            VBox names = new VBox();
            names.setSpacing(5);
            names.setAlignment(Pos.TOP_LEFT);

            for (int i = 0; i < 15; i++)
                if (i + flag < buildingTypes.size()) {
                    {
                        if (city.getCurrentConstruction() != null &&
                                city.getCurrentConstruction() instanceof Building &&
                                ((Building) city.getCurrentConstruction()).
                                        getBuildingType().equals(buildingTypes.get(i + flag)))
                            addLabelToBox(buildingTypes.get(i + flag).name() +
                                    "    cost: " + buildingTypes.get(i + flag).cost + "    (current construction)", names);
                        else
                            addLabelToBox(buildingTypes.get(i + flag).name() +
                                    "    cost: " + buildingTypes.get(i + flag).cost, names);
                    }
                    if (city.getCurrentConstruction() == null) {
                        int finalI = i;
                        int finalFlag = flag;
                        names.getChildren().get(names.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                            // here
                            String result = commandHandler.buildBuilding(buildingTypes.get(finalI + finalFlag));
                            if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                                list.getChildren().remove(list.getChildren().size() - 1);
                            addLabelToPane(list, 300, 200, null, result);
                            if (result.equals(gameEnum.successfulBuild.regex)) {
                                removeAllPanels();
                                parent.getChildren().remove(list);
                                game.updateScreen();
                            }
                        });
                    }
                }

            list.getChildren().add(names);
            setCoordinatesBox(list, names,15 + (flag / 15.0) * 100, 50);
            flag += 15;

        }

        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(10);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        parent.getChildren().add(list);
        for (int i = 0; i < parent.getChildren().size() - 1; i++)
            parent.getChildren().get(i).setDisable(true);
    }

    private void buildUnitPanel(City city) {
        // here
        Pane list = new Pane();
        panelsPaneStyle(list, 390, 165, 500, 500);
        addLabelToPane(list, 200, -5, null, "your gold: " + player.getGold());

        ArrayList<MidRangeType> midRangeTypes = new ArrayList<>();
        ArrayList<LongRangeType> longRangeTypes = new ArrayList<>();
        for (MidRangeType midRangeType : MidRangeType.values())
            if (hasTechnology(midRangeType) && hasResource(midRangeType))
                midRangeTypes.add(midRangeType);
        for (LongRangeType longRangeType : LongRangeType.values())
            if (hasTechnology(longRangeType) && hasResource(longRangeType))
                longRangeTypes.add(longRangeType);

        int flag = 0;
        while (flag < midRangeTypes.size() + longRangeTypes.size() + 2) {
            VBox names = new VBox();
            names.setSpacing(6);
            names.setAlignment(Pos.TOP_LEFT);

            for (int i = 0; i < 15; i++) {
                if (i + flag < midRangeTypes.size()) {
                    if (city.getCurrentConstruction() != null &&
                            city.getCurrentConstruction() instanceof Unit &&
                            ((Unit) city.getCurrentConstruction()).getClass().equals(MidRange.class) &&
                            ((MidRange) city.getCurrentConstruction()).getType().equals(midRangeTypes.get(i + flag)))
                        addLabelToBox(midRangeTypes.get(i + flag).name() +
                                "    cost: " + midRangeTypes.get(i + flag).cost + "    (current construction)", names);
                    else
                        addLabelToBox(midRangeTypes.get(i + flag).name() +
                                "    cost: " + midRangeTypes.get(i + flag).cost, names);
                }
                else if (i + flag < midRangeTypes.size() + longRangeTypes.size()) {
                    if (city.getCurrentConstruction() != null &&
                            city.getCurrentConstruction() instanceof Unit &&
                            ((Unit) city.getCurrentConstruction()).getClass().equals(LongRange.class) &&
                            ((LongRange) city.getCurrentConstruction()).getType().equals(longRangeTypes.get(i + flag - midRangeTypes.size())))
                        addLabelToBox(longRangeTypes.get(i + flag - midRangeTypes.size()).name() +
                                "    cost: " + longRangeTypes.get(i + flag - midRangeTypes.size()).cost + "    (current construction)", names);
                    else
                        addLabelToBox(longRangeTypes.get(i + flag - midRangeTypes.size()).name() +
                                "    cost: " + longRangeTypes.get(i + flag - midRangeTypes.size()).cost, names);
                }
                else if (i + flag < midRangeTypes.size() + longRangeTypes.size() + 1) {
                    if (city.getCurrentConstruction() != null &&
                            city.getCurrentConstruction() instanceof Unit &&
                            ((Unit) city.getCurrentConstruction()).getClass().equals(Settler.class))
                        addLabelToBox("SETTLER    cost: 10    (current construction)", names);
                    else
                        addLabelToBox("SETTLER    cost: 10", names);
                }
                else if (i + flag < midRangeTypes.size() + longRangeTypes.size() + 2) {
                    if (city.getCurrentConstruction() != null &&
                            city.getCurrentConstruction() instanceof Unit &&
                            ((Unit) city.getCurrentConstruction()).getClass().equals(Worker.class))
                        addLabelToBox("WORKER    cost: 10    (current construction)", names);
                    else
                        addLabelToBox("WORKER    cost: 10", names);
                }
                if (city.getCurrentConstruction() == null) {
                    int finalI = i;
                    int finalFlag = flag;
                    names.getChildren().get(names.getChildren().size() - 1).setOnMouseClicked(mouseEvent -> {
                        String result;
                        // here
                        if (finalI + finalFlag < midRangeTypes.size())
                            result = commandHandler.buildUnit(midRangeTypes.get(finalI + finalFlag).name());
                        else if (finalI + finalFlag < midRangeTypes.size() + longRangeTypes.size())
                            result = commandHandler.buildUnit(longRangeTypes.get(finalI + finalFlag - midRangeTypes.size()).name());
                        else if (finalI + finalFlag < midRangeTypes.size() + longRangeTypes.size() + 1)
                            result = commandHandler.buildUnit("SETTLER");
                        else
                            result = commandHandler.buildUnit("WORKER");

                        if(list.getChildren().get(list.getChildren().size() - 1).getClass() == Label.class)
                            list.getChildren().remove(list.getChildren().size() - 1);
                        addLabelToPane(list, 300, 200, null, result);
                        if (result.equals(gameEnum.successfulBuild.regex)) {
                            removeAllPanels();
                            parent.getChildren().remove(list);
                            game.updateScreen();
                        }
                    });
                }
            }

            list.getChildren().add(names);
            setCoordinatesBox(list, names,15 + (flag / 15.0) * 300, 50);
            flag += 15;

        }

        list.getChildren().add(exitButtonStyle(list));
        list.getChildren().get(list.getChildren().size() - 1).setLayoutX(10);
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(10);
        parent.getChildren().add(list);
        for (int i = 0; i < parent.getChildren().size() - 1; i++)
            parent.getChildren().get(i).setDisable(true);
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
                commandHandler.selectCity(String.valueOf(city.getCapitalTile().getPosition().X), String.valueOf(city.getCapitalTile().getPosition().Y));
                // here
                String result = commandHandler.unLockCitizenToTile(textField.getText());
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
        // here
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
                commandHandler.selectCity(String.valueOf(city.getCapitalTile().getPosition().X), String.valueOf(city.getCapitalTile().getPosition().Y));
                // here
                String result = commandHandler.lockCitizenToTile(textField.getText());
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
            commandHandler.selectCUnit("", "");
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
        list.getChildren().get(list.getChildren().size() - 1).setLayoutY(y + 15);
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

    @Override
    public void start(Stage stage) throws Exception {

    }
}

