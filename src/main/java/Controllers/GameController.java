package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Game.Position;
import Models.Menu.Menu;
import Models.Resources.BonusResource;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import Models.Units.CombatUnits.MidRange;
import Models.Units.CombatUnits.MidRangeType;
import Models.Units.NonCombatUnits.Worker;
import enums.gameEnum;
import enums.mainCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.regex.Matcher;

public class GameController
{
	private static GameController instance = null;
	private final ArrayList<Position> grid = new ArrayList<>();
	public final int MAX_GRID_LENGTH = 30;
	private final ArrayList<Tile> map = new ArrayList<>();
	public final int MAX_MAP_SIZE = 10;

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
	public String getMapString()
	{
//		return MapPrinter.getMapString(map, MAX_MAP_SIZE, MAX_MAP_SIZE);
		System.out.println(MapPrinter.getMapString(map, MAX_MAP_SIZE, MAX_MAP_SIZE));

		System.out.println();
		return null;
	}
	// TODO: create overloaded printMap which takes a map as an argument
	///////////////////////////
	//menu methods
	public static boolean numberOfPlayers(HashMap<String,String> players)
	{
		for(int i = 0; i < players.size(); i++)
		{
			if(!players.containsKey(String.valueOf(i + 1)))
				return false;
		}
		return true;
	}

	public static boolean doesUsernameExist(String username)
	{
		for(int i = 0; i < Menu.allUsers.size(); i++)
		{
			if(Menu.allUsers.get(i).getUsername().equals(username))
				return true;
		}
		return false;
	}

	public static boolean existingPlayers(HashMap<String,String> players)
	{
		int index = 0;
		for (Object key : players.keySet())
		{
			Object value = players.get(key);
			index++;
			if(!doesUsernameExist(value.toString()))
				return false;
		}
		return true;
	}//check the input usernames with arrayList

	public static String startNewGame(String command, HashMap<String, String> players)
	{
		int flag = 0;
		for(int i = 0; i < command.length(); i++)
		{
			Matcher matcher = gameEnum.compareRegex(command.substring(i, command.length()), gameEnum.newPlayer);
			if (matcher != null && !matcher.group("username").equals(Menu.loggedInUser.getUsername()))
			{
				players.put(matcher.group("number"), matcher.group("username"));
				flag = 1;
			}
			else if(matcher != null && matcher.group("username").equals(Menu.loggedInUser.getUsername()))
			{
				return gameEnum.loggedInPlayerInCandidates.regex;
			}
		}
		if(flag == 0)
			return mainCommands.invalidCommand.regex;
		else if(!numberOfPlayers(players))
			return gameEnum.numberOfPlayers.regex;
		else if(!existingPlayers(players))
			return gameEnum.playerExist.regex;
		else
			return gameEnum.successfulStartGame.regex;
	}
}















