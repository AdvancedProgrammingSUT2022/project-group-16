package Controllers;

import Models.Game.Position;
import Models.Terrain.BorderType;
import Models.Terrain.Tile;
import Models.Terrain.TileFeature;
import Models.Terrain.TileType;

import java.util.ArrayList;

public class GameController
{
	private static GameController instance = null;
	private final ArrayList<Position> grid = new ArrayList<>();
	private final int MAX_GRID_LENGTH = 30;
	private final ArrayList<Tile> map = new ArrayList<>();
	private final int MAX_MAP_SIZE = 20;
	
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
		for(int i = 0; i < MAX_MAP_SIZE; i++)
			for(int j = 0; j < MAX_MAP_SIZE; j++)
			{
				BorderType[] borders = {BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE};
				map.add(new Tile(getPositionByXY(i, j), TileType.GRASSLAND, TileFeature.NONE, borders, null));
			}
	}
}















