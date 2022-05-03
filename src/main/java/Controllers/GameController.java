package Controllers;

import Models.City.City;
import Models.Game.Position;
import Models.Player.*;
import Models.Menu.Menu;
import Models.Player.Player;
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
import enums.gameCommands.mapCommands;
import enums.gameCommands.selectCommands;
import enums.gameEnum;
import enums.mainCommands;

import java.util.*;
import java.util.regex.Matcher;

public class GameController
{
	private static GameController instance = null;
	private final ArrayList<Position> grid = new ArrayList<>();
	public final int MAX_GRID_LENGTH = 30;
	private final ArrayList<Tile> map = new ArrayList<>();
	public final int MAX_MAP_SIZE = 10;
	private final ArrayList<Player> players = new ArrayList<>();
	private Player playerTurn;
	
	public static GameController getInstance()
	{
		if(instance == null)
			instance = new GameController();
		return instance;
	}
	public void addPlayer(Player player)
	{
		this.players.add(player);
	}
	public void changeTurn()
	{
		// TODO: do everything needed to change turns
		playerTurn = players.get((players.indexOf(playerTurn) + 1) % players.size());
	}
	public void initGame(ArrayList<Player> players)
	{
		// TODO: sync with gameMenu
		initGrid();
		initMap();
		this.players.addAll(players);
		// TODO: set tileStates for each player
		playerTurn = players.get(0);
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
	public Position getPosition(int x, int y)
	{
		if(x < 0 || y < 0 || x >= MAX_GRID_LENGTH || y >= MAX_GRID_LENGTH)
			return null;
		
		for(Position position : grid)
			if(position.X == x && position.Y == y)
				return position;
		
		return null;
	}
	private void initMap()
	{
		// TODO: select from a list of maps
		
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
				map.add(new Tile(getPosition(i, j), TileType.values()[tileTypeRandom.nextInt(TileType.values().length)],
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

	public static int getNum(Scanner scanner, int min, int max)
	{
		int number = 0;
		String tmpNumber = scanner.nextLine();
		if(GameController.isValid(tmpNumber))
			number = Integer.parseInt(tmpNumber);
		if(number > max || number < min)
			number = 0;
		return number;
	}

	public static boolean isValid(String n)
	{
		for(int i = 0; i < n.length(); i++)
		{
			if(n.charAt(i) > 57 || n.charAt(i) < 48)
				return false;
		}
		return true;
	}

	private static boolean validPos(int x, int y)
	{
		return x < getInstance().MAX_MAP_SIZE && y < getInstance().MAX_MAP_SIZE && x >= 0 && y >= 0;
	}

	//DOC commands
	public String showResearch()
	{
		// TODO: calculate the remaining turns of the research
		// TODO: find everything that unlocks after the research
		return "Reseach info:\n"+"Researching technology: " + playerTurn.getResearchingTechnology().toString() + "\n" +
			"Remaining turns: " + " " + "\n"+
			"everything which will be unlocked" + " ";
	}
	public String showUnits()
	{
		// TODO: player should be able to active some units
		// TODO: show units in the right order
		StringBuilder allUnitsString = new StringBuilder("Units info:\n");
		ArrayList<Unit> units = playerTurn.getUnits();
		for(int i = 0; i < units.size(); i++)
			allUnitsString.append(units.get(i).toString() + ", ");
		
		return allUnitsString.toString();
	}
	public String showCities()
	{
		// TODO: player should be able to go to some cities and see their info
		StringBuilder allCitiesString = new StringBuilder("Cities info:\n");
		for(City city : playerTurn.getCities())
			allCitiesString.append(city.getName() + ", ");
		
		return allCitiesString.toString();
	}
	public String showDiplomacy()
	{
		// TODO: player should be able to negotiate with other players
		return "Diplomacy info:\n"+ "Score of " + playerTurn.getCivilization() + ": " + playerTurn.getScore();
	}
	public String showVictory()
	{
		// TODO: show victory info
		return "Victory info:\n";
	}
	public String showDemographics()
	{
		// TODO: show demographics info
		
		return "Demographics info:\n";
	}
	public String showNotifications()
	{
		StringBuilder allNotificationsString = new StringBuilder("Notifications info:\n");
		Stack<Notification> notifications = playerTurn.getNotifications();
		for(int i = notifications.size() - 1; i >= 0; i--)
			allNotificationsString.append(notifications.get(i).toString()).append(", ");
		
		return allNotificationsString.toString();
	}
	public String showMilitary()
	{
		// TODO ??
		return showUnits();
	}
	public String showEconomics()
	{
		//TODO
		return null;
	}
	public String showDiplomatic()
	{
		//TODO
		return null;
	}
	public String showDeals()
	{
		//TODO
		return null;
	}
	public String selectUnitCombat()
	{

	}
	public static void selectUnitNonCombat()
	{

	}
	public static City selectCity(String command, Player player)
	{
		for(int i = 0; i < command.length(); i++)
		{
			Matcher matcher1 = selectCommands.compareRegex(command, selectCommands.newPos);
			Matcher matcher2 = selectCommands.compareRegex(command, selectCommands.shortNewPos);
			Matcher matcher3 = selectCommands.compareRegex(command, selectCommands.newName);
			Matcher matcher4 = selectCommands.compareRegex(command, selectCommands.shortNewName);

			if(matcher1 != null)
			{
				int x = Integer.parseInt(matcher1.group("x"));
				int y = Integer.parseInt(matcher1.group("y"));
				for(int j = 0; j < player.getCities().size(); j++)
					if(player.getCities().get(j).getCapitalTile().getPosition().X == x &&
							player.getCities().get(j).getCapitalTile().getPosition().Y == y)
						return player.getCities().get(j);

			}
			if(matcher2 != null)
			{
				int x = Integer.parseInt(matcher2.group("x"));
				int y = Integer.parseInt(matcher2.group("y"));
				System.out.println(x + " " +  y);
				for(int j = 0; j < player.getCities().size(); j++)
					if(player.getCities().get(j).getCapitalTile().getPosition().X == x &&
							player.getCities().get(j).getCapitalTile().getPosition().Y == y)
						return player.getCities().get(j);
			}
			if(matcher3 != null)
			{
				String cityName = matcher3.group("name");
				for(int j = 0; j < player.getCities().size(); j++)
					if(player.getCities().get(j).getName().equals(cityName))
						return player.getCities().get(j);

			}
			if(matcher4 != null)
			{
				String cityName = matcher4.group("name");
				for(int j = 0; j < player.getCities().size(); j++)
					if(player.getCities().get(j).getName().equals(cityName))
						return player.getCities().get(j);
			}
		}
		return null;
	}
	public static void moveUnit()
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
	public static void attack()
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

	public static void mapShow()
	{

	}
	public static void mapMoveRight(String command)
	{
		int number = getMoves(command);
		//TODO
	}
	public static void mapMoveLeft(String command)
	{
		int number = getMoves(command);
		//TODO
	}
	public static void mapMoveUp(String command)
	{
		int number = getMoves(command);
		//TODO
	}
	public static void mapMoveDown(String command)
	{
		int number = getMoves(command);
		//TODO
	}

	private static int getMoves(String command)
	{
		int number = 0;
		Matcher matcher;
		if ((matcher = mapCommands.compareRegex(command, mapCommands.newNumber)) != null)
			number = Integer.parseInt(matcher.group("c"));
		else if ((matcher = mapCommands.compareRegex(command, mapCommands.shortNewNumber)) != null)
			number = Integer.parseInt(matcher.group("c"));
		return number;
	}
}
