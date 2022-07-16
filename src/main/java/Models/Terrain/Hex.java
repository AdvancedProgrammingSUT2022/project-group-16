package Models.Terrain;

import Controllers.GameController;
import Models.Player.Player;
import Models.Player.TileState;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.MidRange;
import Models.Units.NonCombatUnits.Settler;
import Models.Units.NonCombatUnits.Worker;
import Models.Units.Unit;
import javafx.event.EventHandler;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.effect.Lighting;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;

import java.util.ArrayList;

public class Hex {
    private static Pane parent;
    private final Pane pane;
    private final Position position;
    private Tile tile;
    private TileState tileState;
    private ArrayList<ImageView> hexElements = new ArrayList<>();
    private ImageView tileImageView;


    public Hex(Position position){
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
        pane.setOnMouseReleased(new EventHandler<MouseEvent>()
        {
            @Override
            public void handle(MouseEvent mouseEvent)
            {
                onMouseReleased();
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

    private void onMouseReleased()
    {
        if(tileState.equals(TileState.FOG_OF_WAR))
            return;
        if(tileState.equals(TileState.VISIBLE))
            tileImageView.setEffect(null);
        else if (tileState.equals(TileState.REVEALED))
        {
            ColorAdjust colorAdjust = new ColorAdjust();
            colorAdjust.setBrightness(-.6);
            tileImageView.setEffect(colorAdjust);
        }



    }

    private void onMouseClicked()
    {

    }
}
