package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Game.Position;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Menu.Menu;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.BonusResource;
import Models.Resources.Resource;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.MidRange;
import Models.Units.CombatUnits.MidRangeType;
import Models.Units.NonCombatUnits.Worker;
import Models.Units.Unit;
import Models.User;
import Views.gameMenuView;
import enums.cheatCode;
import enums.gameEnum;
import enums.mainCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;
import java.util.Scanner;
import java.util.regex.Matcher;

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
						null,
						null));
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
		// TODO:
		return null;
	}
	// TODO: create overloaded printMap which takes a map as an argument
	///////////////////////////
	//menu methods
	private static boolean numberOfPlayers(HashMap<String,String> players)
	{
		for(int i = 0; i < players.size(); i++)
		{
			if(!players.containsKey(String.valueOf(i + 1)))
				return false;
		}
		return true;
	}

	private static boolean doesUsernameExist(String username)
	{
		for(int i = 0; i < Menu.allUsers.size(); i++)
		{
			if(Menu.allUsers.get(i).getUsername().equals(username))
				return true;
		}
		return false;
	}

	private static boolean existingPlayers(HashMap<String,String> players)
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
			Matcher matcher1 = gameEnum.compareRegex(command.substring(i), gameEnum.newPlayer);
			Matcher matcher2 = gameEnum.compareRegex(command.substring(i), gameEnum.shortNewPlayer);

			if (matcher1 != null && !matcher1.group("username").equals(Menu.loggedInUser.getUsername()))
			{
				players.put(matcher1.group("number"), matcher1.group("username"));
				flag = 1;
			}
			else if (matcher2 != null && !matcher2.group("username").equals(Menu.loggedInUser.getUsername()))
			{
				players.put(matcher2.group("number"), matcher2.group("username"));
				flag = 1;
			}
			else if(matcher1 != null && matcher1.group("username").equals(Menu.loggedInUser.getUsername()))
				return gameEnum.loggedInPlayerInCandidates.regex;
			else if(matcher2 != null && matcher2.group("username").equals(Menu.loggedInUser.getUsername()))
				return gameEnum.loggedInPlayerInCandidates.regex;
		}
		if(flag == 0)
			return mainCommands.invalidCommand.regex;
		else if(!numberOfPlayers(players))
			return gameEnum.numberOfPlayers.regex;
		else if(!existingPlayers(players))
			return gameEnum.playerExist.regex;
		else if(players.size()  > 3)
			return gameEnum.lessThanFour.regex;
		else
			return gameEnum.successfulStartGame.regex;
	}

	//cheat codes
	public static String increaseGold(Matcher matcher, Player player)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		player.setGold(player.getGold() + amount);
		return cheatCode.successful.regex;
	}
	public static String increaseFood(Matcher matcher, Player player)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		player.setFood(player.getFood() + amount);
		return cheatCode.successful.regex;
	}
	public static String increaseTurns(Matcher matcher, Player player)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		return cheatCode.successful.regex;
	}
	public static String addTechnology(Matcher matcher, Player player)
	{
		player.getTechnologies().add(Technology.valueOf(matcher.group("name")));
		player.setTechnologies(player.getTechnologies());
		return cheatCode.successful.regex;
	}
	public static String winBattle(Matcher matcher, Player player)
	{
		int x = Integer.parseInt(matcher.group("positionX"));
		int y = Integer.parseInt(matcher.group("positionY"));
		//TODO:win battle
		return cheatCode.successful.regex;
	}
	public static String moveUnit(Matcher matcher, Player player)
	{
		int x = Integer.parseInt(matcher.group("positionX"));
		int y = Integer.parseInt(matcher.group("positionY"));
		int newX = Integer.parseInt(matcher.group("newPositionX"));
		int newY = Integer.parseInt(matcher.group("newPositionY"));
		return cheatCode.successful.regex;
	}

	public static String enterMenu(Scanner scanner, Matcher matcher)
	{
		String menuName = matcher.group("menuName");

		if((matcher = mainCommands.compareRegex(menuName, mainCommands.profileName)) != null)
			return mainCommands.navigationError.regex;
		else if((matcher = mainCommands.compareRegex(menuName, mainCommands.loginMenu)) != null)
			return mainCommands.navigationError.regex;
		else if((matcher = mainCommands.compareRegex(menuName, mainCommands.mainMenu)) != null)
			return "1";
		return mainCommands.invalidCommand.regex;
	}

	public static User[] convertMapToArr(HashMap<String, String> players)
	{
		User[] newArr = new User[players.size() + 1];
		newArr[0] = Menu.loggedInUser;

		for(int i = 1; i < players.size() + 1; i++)
			newArr[i] = RegisterController.getUserByUsername(players.entrySet().toArray()[i - 1].toString().substring(2));
		return newArr;
	}

	public static Civilization findCivilByNumber(int number)
	{
		if(number == 1) return Civilization.AMERICAN;
		if(number == 2) return Civilization.ARABIAN;
		if(number == 3) return Civilization.ASSYRIAN;
		if(number == 4) return Civilization.CHINESE;
		if(number == 5) return Civilization.GERMAN;
		if(number == 6) return Civilization.GREEK;
		if(number == 7) return Civilization.MAYAN;
		if(number == 8) return Civilization.PERSIAN;
		if(number == 9) return Civilization.OTTOMAN;
		if(number == 10) return Civilization.RUSSIAN;
		return null;
	}

	//DOC commands
	public static String showResearch(Player player)
	{
		if(player.getResearchingTechnology() == null)
			return mainCommands.nothing.regex;
		return player.getResearchingTechnology().toString();
	}

	public static String pickCivilization(ArrayList<Player> players, User[] users, Scanner scanner, GameController gameController, int num, int i)
	{
		if(num > 10 || num < 1)
			return gameEnum.between1And10.regex;
		else if(inArr(players, findCivilByNumber(num)))
			return gameEnum.alreadyPicked.regex;
		else
			return gameEnum.chooseCivilization.regex + Civilization.values()[num - 1];

	}

	public static boolean inArr(ArrayList<Player> player, Civilization n)
	{
		for(int i = 0; i < player.size(); i++)
		{
			if(player.get(i).getCivilization() == n)
				return true;
		}
		return false;
	}

	public static void selectUnitCombat(Position position)
	{

	}
	public static void selectUnitNonCombat(Position position)
	{

	}
	public static void selectCityName(String name)
	{

	}
	public static void selectCityPosition(Position position)
	{

	}
	public static void moveUnit(Position position)
	{

	}
	public static void sleep()
	{

	}
	public static void alert()
	{

	}
	public static void fortify()
	{

	}
	public static void fortifyTilHeal()
	{

	}
	public static void garrison()
	{

	}
	public static void setup()
	{

	}
	public static void attack(Position position)
	{

	}
	public static void found()
	{

	}
	public static void cancel()
	{

	}
	public static void wake()
	{

	}
	public static void delete()
	{

	}
	public static void road()
	{

	}
	public static void railRoad()
	{

	}
	public static void farm()
	{

	}
	public static void mine()
	{

	}
	public static void tradingPost()
	{

	}
	public static void lumberMill()
	{

	}
	public static void pasture()
	{

	}
	public static void camp()
	{

	}
	public static void plantation()
	{

	}
	public static void quarry()
	{

	}
	public static void removeJungle()
	{

	}
	public static void removeRoute()
	{

	}
	public static void repair()
	{

	}
	public static void mapShowPosition(Position position)
	{

	}
	public static void mapShowCityName(String name)
	{

	}
	public static void mapMoveRight(int moves)
	{

	}
	public static void mapMoveLeft(int moves)
	{

	}
	public static void mapMoveUp(int moves)
	{

	}
	public static void mapMoveDown(int moves)
	{

	}
}
