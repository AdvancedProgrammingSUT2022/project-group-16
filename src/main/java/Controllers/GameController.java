package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.Game.Position;
import Models.Player.*;
import Models.Menu.Menu;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.BonusResource;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import Models.Units.Unit;
import Models.User;
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
	private static final ArrayList<Player> players = new ArrayList<>();
	private static Player playerTurn;
	private final RegisterController registerController = new RegisterController();
	private int turnCounter = 0;

	public static GameController getInstance()
	{
		if(instance == null)
			instance = new GameController();
		return instance;
	}

	public void addPlayer(Player player)
	{
		players.add(player);
		playerTurn = players.get(0);
	}
	public void deletePlayer(Player player)
	{
		players.remove(player);
	}
	public void changeTurn()
	{
		playerTurn = players.get((players.indexOf(playerTurn) + 1) % players.size());
	}
	public void initGame()
	{
		// TODO: sync with gameMenu
		initGrid();
		initMap();
		// TODO: set tileStates for each player
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
	public Tile getTileByXY(int X, int Y)
	{
		for(Tile tile : map)
			if(tile.getPosition().X == X && tile.getPosition().Y == Y)
				return tile;
		return null;
	}
	public Tile getTileByQRS(int Q, int R, int S)
	{
		for(Tile tile : map)
			if(tile.getPosition().Q == Q && tile.getPosition().R == R && tile.getPosition().S == S)
				return tile;
		return null;
	}
	public ArrayList<Tile> getAdjacentTiles(Tile tile)
	{
		ArrayList<Tile> adjacentTiles = new ArrayList<>();
		int Q = tile.getPosition().Q;
		int R = tile.getPosition().R;
		int S = tile.getPosition().S;
		
		int[][] distances = {{0, 1, -1}, {0, -1, 1}, {1, 0, -1}, {-1, 0, 1}, {1, -1, 0}, {-1, 1, 0}};
		Tile adjacentTile;
		for(int i = 0; i < 6; i++)
		{
			adjacentTile = getTileByQRS(Q + distances[i][0], R + distances[i][1], S + distances[i][2]);
			if(adjacentTile != null)
				adjacentTiles.add(adjacentTile);
		}
		
		return adjacentTiles;
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
	
	public Player getPlayerTurn()
	{
		return playerTurn;
	}
	public int getTurnCounter() {
		return turnCounter;
	}
	public void addToTurnCounter(int amount) {
		turnCounter += amount;
	}
	public ArrayList<Player> getPlayers() {
		return players;
	}
	public void removeAllPlayers()
	{
		if (players.size() > 0)
			players.subList(0, players.size()).clear();
	}

	private boolean existingPlayers(HashMap<String,String> players)
	{
		for (Object key : players.keySet())
		{
			Object value = players.get(key);
			if(!doesUsernameExist(value.toString()))
				return false;
		}
		return true;
	}//check the input usernames with arrayList

	public String startNewGame(String command, HashMap<String, String> players)
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
	public String increaseGold(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setGold(playerTurn.getGold() + amount);
		return cheatCode.successful.regex;
	}
	public String increaseFood(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setFood(playerTurn.getFood() + amount);
		return cheatCode.successful.regex;
	}
	public String increaseTurns(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		turnCounter += amount;
		return cheatCode.successful.regex;
	}
	public String addTechnology(Matcher matcher)
	{
		// TODO: check if we have access to this technology
		playerTurn.addTechnology(Technology.valueOf(matcher.group("name")));
		return cheatCode.successful.regex;
	}
	public String winBattle(Matcher matcher)
	{
		int x = Integer.parseInt(matcher.group("positionX"));
		int y = Integer.parseInt(matcher.group("positionY"));
		//TODO:win battle
		return cheatCode.successful.regex;
	}
	public String moveUnit(Matcher matcher)
	{
		//TODO: move unit
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
	public User[] convertMapToArr(HashMap<String, String> players)
	{
		User[] newArr = new User[players.size() + 1];
		newArr[0] = Menu.loggedInUser;

		for(int i = 1; i < players.size() + 1; i++)
			newArr[i] = registerController.getUserByUsername(players.entrySet().toArray()[i - 1].toString().substring(2));
		return newArr;
	}
	public Civilization findCivilByNumber(int number)
	{
		switch (number % 10)
		{
			case 1 -> {
				return Civilization.AMERICAN;
			}
			case 2 -> {
				return Civilization.ARABIAN;
			}
			case 3 -> {
				return Civilization.ASSYRIAN;
			}
			case 4 -> {
				return Civilization.CHINESE;
			}
			case 5 -> {
				return Civilization.GERMAN;
			}
			case 6 -> {
				return Civilization.GREEK;
			}
			case 7 -> {
				return Civilization.MAYAN;
			}
			case 8 -> {
				return Civilization.PERSIAN;
			}
			case 9 -> {
				return Civilization.OTTOMAN;
			}
			case 0 -> {
				return Civilization.RUSSIAN;
			}
		}
		return null;
	}
	public String pickCivilization(int num)
	{
		if(num > 10 || num < 1)
			return gameEnum.between1And10.regex;
		else if(inArr(findCivilByNumber(num)))
			return gameEnum.alreadyPicked.regex;
		else
			return gameEnum.chooseCivilization.regex + Civilization.values()[num - 1];
	}
	public boolean inArr(Civilization n)
	{
		for (Player value : players) {
			if (value.getCivilization() == n)
				return true;
		}
		return false;
	}
	public int getNum(Scanner scanner, int min, int max)
	{
		int number = 0;
		String tmpNumber = scanner.nextLine();
		if(isValid(tmpNumber))
			number = Integer.parseInt(tmpNumber);
		if(number > max || number < min)
			number = 0;
		return number;
	}
	public boolean isValid(String n)
	{
		for(int i = 0; i < n.length(); i++)
		{
			if(n.charAt(i) > 57 || n.charAt(i) < 48)
				return false;
		}
		return true;
	}

	//DOC commands
	public String showResearch()
	{
		// TODO: calculate the remaining turns of the research
		// TODO: find everything that unlocks after the research
		return "Reseach info:\n"+"Researching technology: " + "playerTurn.getResearchingTechnology().toString()" + "\n" +
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

	public String selectCUnit(String command)
	{
		playerTurn.setSelectedUnit(null);
		for(int i = 0; i < command.length(); i++)
		{
			Matcher matcher1 = selectCommands.compareRegex(command.substring(i), selectCommands.newPos);
			Matcher matcher2 = selectCommands.compareRegex(command.substring(i), selectCommands.shortNewPos);
			int x = 0, y = 0;
			if(matcher1 != null)
			{
				x = Integer.parseInt(matcher1.group("x"));
				y = Integer.parseInt(matcher1.group("y"));
			}
			else if(matcher2 != null)
			{
				x = Integer.parseInt(matcher2.group("x"));
				y = Integer.parseInt(matcher2.group("y"));
			}
			if(matcher1 != null || matcher2 != null)
			{
				if(x >= getInstance().MAX_MAP_SIZE || x < 0 ||
						y >= getInstance().MAX_MAP_SIZE || y < 0)
					return selectCommands.invalidRange.regex + (getInstance().MAX_MAP_SIZE - 1);
				for(int j = 0; j < playerTurn.getUnits().size(); j++)
					if(playerTurn.getUnits().get(j).getTile().getPosition().X == x &&
							playerTurn.getUnits().get(j).getTile().getPosition().Y == y) //TODO: c/n unit
					{
						playerTurn.setSelectedUnit(playerTurn.getUnits().get(j));
						return selectCommands.selected.regex;
					}
				if(playerTurn.getSelectedUnit() == null)
					return selectCommands.coordinatesDoesntExistCUnit.regex+ x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}
	public String selectNUnit(String command)
	{
		playerTurn.setSelectedUnit(null);
		for(int i = 0; i < command.length(); i++)
		{
			Matcher matcher1 = selectCommands.compareRegex(command.substring(i), selectCommands.newPos);
			Matcher matcher2 = selectCommands.compareRegex(command.substring(i), selectCommands.shortNewPos);
			int x = 0, y = 0;
			if(matcher1 != null)
			{
				x = Integer.parseInt(matcher1.group("x"));
				y = Integer.parseInt(matcher1.group("y"));
			}
			else if(matcher2 != null)
			{
				x = Integer.parseInt(matcher2.group("x"));
				y = Integer.parseInt(matcher2.group("y"));
			}
			if(matcher1 != null || matcher2 != null)
			{
				if(x >= getInstance().MAX_MAP_SIZE || x < 0 ||
						y >= getInstance().MAX_MAP_SIZE || y < 0)
					return selectCommands.invalidRange.regex + (getInstance().MAX_MAP_SIZE - 1);
				for(int j = 0; j < playerTurn.getUnits().size(); j++)
					if(playerTurn.getUnits().get(j).getTile().getPosition().X == x &&
							playerTurn.getUnits().get(j).getTile().getPosition().Y == y) //TODO: c/n unit
					{
						playerTurn.setSelectedUnit(playerTurn.getUnits().get(j));
						return selectCommands.selected.regex;
					}
				if(playerTurn.getSelectedUnit() == null)
					return selectCommands.coordinatesDoesntExistNUnit.regex+ x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}

	public String selectCity(String command)
	{
		int x = 0, y = 0;
		playerTurn.setSelectedCity(null);
		for(int i = 0; i < command.length(); i++)
		{
			Matcher matcher1 = selectCommands.compareRegex(command.substring(i), selectCommands.newPos);
			Matcher matcher2 = selectCommands.compareRegex(command.substring(i), selectCommands.shortNewPos);
			Matcher matcher3 = selectCommands.compareRegex(command.substring(i), selectCommands.newName);
			Matcher matcher4 = selectCommands.compareRegex(command.substring(i), selectCommands.shortNewName);
			if(matcher1 != null)
			{
				x = Integer.parseInt(matcher1.group("x"));
				y = Integer.parseInt(matcher1.group("y"));
			}
			else if(matcher2 != null)
			{
				x = Integer.parseInt(matcher2.group("x"));
				y = Integer.parseInt(matcher2.group("y"));
			}
			else if(matcher3 != null)
			{
				String cityName = matcher3.group("name");
				for(int j = 0; j < playerTurn.getCities().size(); j++)
					if(playerTurn.getCities().get(j).getName().equals(cityName))
					{
						playerTurn.setSelectedCity(playerTurn.getCities().get(j));
						return selectCommands.selected.regex;
					}
				if(playerTurn.getSelectedCity() == null)
					return selectCommands.nameDoesntExist.regex + cityName;
			}
			else if(matcher4 != null)
			{
				String cityName = matcher4.group("name");
				for(int j = 0; j < playerTurn.getCities().size(); j++)
					if(playerTurn.getCities().get(j).getName().equals(cityName))
					{
						playerTurn.setSelectedCity(playerTurn.getCities().get(j));
						return selectCommands.selected.regex;
					}
				if(playerTurn.getSelectedCity() == null)
					return selectCommands.nameDoesntExist.regex + cityName;
			}
			if(matcher1 != null || matcher2 != null)
			{
				if(x >= getInstance().MAX_MAP_SIZE || x < 0 ||
						y >= getInstance().MAX_MAP_SIZE || y < 0)
					return selectCommands.invalidRange.regex + (getInstance().MAX_MAP_SIZE - 1);
				for(int j = 0; j < playerTurn.getCities().size(); j++)
					if(playerTurn.getCities().get(j).getCapitalTile().getPosition().X == x &&
							playerTurn.getCities().get(j).getCapitalTile().getPosition().Y == y)
					{
						playerTurn.setSelectedCity(playerTurn.getCities().get(j));
						return selectCommands.selected.regex;
					}
				if(playerTurn.getSelectedCity() == null)
					return selectCommands.coordinatesDoesntExistCity.regex+ x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}
	public void moveUnit()
	{

	}
	public void sleep()
	{

	}
	public void alert()
	{

	}
	public void fortify()
	{

	}
	public void fortifyTilHeal()
	{

	}
	public void garrison()
	{

	}
	public void setup()
	{

	}
	public void attack()
	{

	}
	public void found()
	{

	}
	public void cancel()
	{

	}
	public void wake()
	{

	}
	public void delete()
	{

	}
	public void road()
	{

	}
	public void railRoad()
	{

	}
	public void farm()
	{

	}
	public void mine()
	{

	}
	public void tradingPost()
	{

	}
	public void lumberMill()
	{

	}
	public void pasture()
	{

	}
	public void camp()
	{

	}
	public void plantation()
	{

	}
	public void quarry()
	{

	}
	public void removeJungle()
	{

	}
	public void removeRoute()
	{

	}
	public void repair()
	{

	}

	public String mapShow(String command)
	{
		int x = 0, y = 0;
		playerTurn.setSelectedCity(null);
		for(int i = 0; i < command.length(); i++)
		{
			Matcher matcher1 = mapCommands.compareRegex(command.substring(i), mapCommands.newPos);
			Matcher matcher2 = mapCommands.compareRegex(command.substring(i), mapCommands.shortNewPos);
			Matcher matcher3 = mapCommands.compareRegex(command.substring(i), mapCommands.newName);
			Matcher matcher4 = mapCommands.compareRegex(command.substring(i), mapCommands.shortNewName);
			if(matcher1 != null)
			{
				x = Integer.parseInt(matcher1.group("x"));
				y = Integer.parseInt(matcher1.group("y"));
			}
			else if(matcher2 != null)
			{
				x = Integer.parseInt(matcher2.group("x"));
				y = Integer.parseInt(matcher2.group("y"));
			}
			else if(matcher3 != null)
			{
				String cityName = matcher3.group("name");
				for (Player player : players)
					for (int j = 0; j < player.getCities().size(); j++)
						if (player.getCities().get(j).getName().equals(cityName)) {
							if (playerTurn.getMap().get(player.getCities().get(j).getCapitalTile()).equals(TileState.FOG_OF_WAR))
								return mapCommands.visible.regex;
							MapPrinter.selectedCity = player.getCities().get(j);
							return mapCommands.selected.regex;
						}
				if(MapPrinter.selectedTile == null)
					return selectCommands.nameDoesntExist.regex + cityName;
			}
			else if(matcher4 != null)
			{
				String cityName = matcher4.group("name");
				for (Player player : players)
					for (int j = 0; j < player.getCities().size(); j++)
						if (player.getCities().get(j).getName().equals(cityName)) {
							if (playerTurn.getMap().get(player.getCities().get(j).getCapitalTile()).equals(TileState.FOG_OF_WAR))
								return mapCommands.visible.regex;
							MapPrinter.selectedCity = player.getCities().get(j);
							return mapCommands.selected.regex;
						}
				if(MapPrinter.selectedTile == null)
					return selectCommands.nameDoesntExist.regex + cityName;
			}
			if(matcher1 != null || matcher2 != null)
			{
				if(x >= getInstance().MAX_MAP_SIZE || x < 0 ||
						y >= getInstance().MAX_MAP_SIZE || y < 0)
					return mapCommands.invalidRange.regex + (getInstance().MAX_MAP_SIZE - 1);
				if (playerTurn.getMap().get(playerTurn.getTileByXY(x, y)).equals(TileState.FOG_OF_WAR))
					return mapCommands.visible.regex;
				MapPrinter.selectedTile = getTileByXY(x, y);
				return mapCommands.selected.regex;
			}
		}
		return mapCommands.invalidCommand.regex;
	}
	public String mapMoveRight(String command)
	{
		int number = getMoves(command);
		if(number == -1)
			return mapCommands.positiveNum.regex;
		if(number == -2)
			return mapCommands.invalidCommand.regex;
		//TODO
		return mapCommands.successful.regex;
	}
	public String mapMoveLeft(String command)
	{
		int number = getMoves(command);
		if(number == -1)
			return mapCommands.positiveNum.regex;
		if(number == -2)
			return mapCommands.invalidCommand.regex;
		//TODO
		return mapCommands.successful.regex;
	}
	public String mapMoveUp(String command)
	{
		int number = getMoves(command);
		if(number == -1)
			return mapCommands.positiveNum.regex;
		if(number == -2)
			return mapCommands.invalidCommand.regex;
		//TODO
		return mapCommands.successful.regex;
	}
	public String mapMoveDown(String command)
	{
		int number = getMoves(command);
		if(number == -1)
			return mapCommands.positiveNum.regex;
		if(number == -2)
			return mapCommands.invalidCommand.regex;
		//TODO
		return mapCommands.successful.regex;
	}

	private int getMoves(String command)
	{
		Matcher matcher;
		for(int i = 0; i < command.length(); i++)
			if ((matcher = mapCommands.compareRegex(command.substring(i), mapCommands.shortNewNumber)) != null)
			{
				if(Integer.parseInt(matcher.group("c")) < 0)
					return -1;
				return Integer.parseInt(matcher.group("c"));
			}
		return -2;
	}
}
