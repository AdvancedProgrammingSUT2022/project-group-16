package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Game.Position;
import Models.Player.Player;
import Models.Resources.BonusResource;
import Models.Resources.Resource;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.MidRange;
import Models.Units.CombatUnits.MidRangeType;
import Models.Units.NonCombatUnits.Worker;

import java.util.ArrayList;
import java.util.Random;

public class GameController
{
	private static GameController instance = null;
	private final ArrayList<Position> grid = new ArrayList<>();
	public final int MAX_GRID_LENGTH = 30;
	private final ArrayList<Tile> map = new ArrayList<>();
	public final int MAX_MAP_SIZE = 10;
	private ArrayList<Player> players;
	
	
	private GameController()
	{
		initGrid();
		initMap(); // TODO: customization for the arbitrary map
	}
	
	public static GameController getInstance()
	{
		if(instance == null)
			instance = new GameController();
		return instance;
	}
	
	public ArrayList<Tile> getMap()
	{
		return map;
	}
	
	private void initGrid()
	{
		for(int i = 0; i < MAX_GRID_LENGTH; i++)
			for(int j = 0; j < MAX_GRID_LENGTH; j++)
				grid.add(new Position(i, j));
	}
	public Position getPositionByXY(int x, int y)
	{
		if(x < 0 || y < 0 || x >= MAX_GRID_LENGTH || y >= MAX_GRID_LENGTH)
			return null;
		
		for(Position position : grid)
			if(position.X == x && position.Y == y)
				return position;
		
		return null;
	}
	public Position getPositionByQRS(int q, int r)
	{
		return getPositionByXY(r + (q - (q & 1)) / 2, q);
	}
	private void initMap()
	{
		// create sample maps
		Random borderRandom = new Random();
		Random tileTypeRandom = new Random();
		Random tileFeatureRandom = new Random();
		Random improvementRandom = new Random();
		Random resourceRandom = new Random();
		Random CUnitRandom = new Random();
		
		for(int i = 0; i < MAX_MAP_SIZE; i++)
			for(int j = 0; j < MAX_MAP_SIZE; j++)
			{
				BorderType[] borders = new BorderType[6];
				for(int k = 0; k < 6; k++)
					borders[k] = BorderType.values()[borderRandom.nextInt(2)];
				// TODO: bug with the resource and unit. fix it!!!
				map.add(new Tile(getPositionByXY(i, j), TileType.values()[tileTypeRandom.nextInt(TileType.values().length)],
						TileFeature.values()[tileFeatureRandom.nextInt(TileFeature.values().length)], borders,
						new BonusResource(ResourceType.values()[resourceRandom.nextInt(ResourceType.values().length)]),
						Improvement.values()[improvementRandom.nextInt(Improvement.values().length)],
						new MidRange(MidRangeType.values()[CUnitRandom.nextInt(MidRangeType.values().length)]),
						new Worker()));
			}
	}
	public Tile getTileByXY(int x, int y)
	{
		for(Tile tile : map)
			if(tile.getPosition().X == x && tile.getPosition().Y == y)
				return tile;
		return null;
	}
	public String getMapString() // TODO:
	{
		return null;
	}
	// TODO: create overloaded printMap which takes a map as an argument
	
}















