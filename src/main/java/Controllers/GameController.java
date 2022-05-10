package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.City.BuildingType;
import Models.City.City;
import Models.Game.Position;
import Models.Player.*;
import Models.Menu.Menu;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.MidRange;
import Models.Units.CombatUnits.MidRangeType;
import Models.Units.CommandHandeling.UnitCommandsHandler;
import Models.Units.NonCombatUnits.*;
import Models.Units.Unit;
import Models.User;
import enums.cheatCode;
import enums.gameCommands.infoCommands;
import enums.gameCommands.mapCommands;
import enums.gameCommands.selectCommands;
import enums.gameCommands.unitCommands;
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
	public final int MAP_SIZE = 10;
	private final ArrayList<Player> players = new ArrayList<>();
	private Player playerTurn;
	private final Position[] startingPositions = new Position[]{new Position(5, 5), new Position(1, 8), new Position(8, 1), new Position(8, 8)};
	private final RegisterController registerController = new RegisterController();
	private int turnCounter = 0;

	public static GameController getInstance()
	{
		if(instance == null)
		{
			instance = new GameController();
			instance.initGrid();
			instance.initMap();
		}
		return instance;
	}

	// this method checks that everything before changing turn is done. (i.e. check if all units have used their turns)
	// if everything is ok, it calls the changeTurn method
	public String checkChangeTurn()
	{
		// if there is a unit which has not used its turn, it returns the unit's name with error message
		for(Unit unit : playerTurn.getUnits())
		{
			//TODO: check if unit has used its turn
			if(false)
			{
				return "unit in " + unit.getTile().getPosition().X + "," + unit.getTile().getPosition().Y + " has not used its turn";
			}
		}
		changeTurn();
		return null;
	}
	// this updates all turns at the end of each turn (i.e. reset all units turns and decrement researching technology turns)
	private void changeTurn()
	{
		// reset all units turns
		for(Unit unit : playerTurn.getUnits())
		{
			// set their turns to default
		}
		playerTurn.setSelectedUnit(null);
		playerTurn.setSelectedCity(null);

		// consume food for this player (consumes 1 food for each citizen)
		for(City city : playerTurn.getCities())
			playerTurn.setFood(playerTurn.getFood() - city.getPopulation());
		// decrement researching technology turns

		// check for city growth


		// change playerTurn
		playerTurn = players.get((players.indexOf(playerTurn) + 1) % players.size());
	}
	public void initGame()
	{
		initPlayers();
		// TODO: set tileStates for each player
	}
	public ArrayList<Tile> getMap()
	{
		return map;
	}
	public String getMapString()
	{
		return MapPrinter.getMapString(playerTurn);
	}
	public String getMapString(Player player)
	{
		return MapPrinter.getMapString(player);
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
		
		for(int i = 0; i < MAP_SIZE; i++)
			for(int j = 0; j < MAP_SIZE; j++)
			{
				BorderType[] borders = new BorderType[6];
				for(int k = 0; k < 6; k++)
					borders[k] = BorderType.values()[borderRandom.nextInt(2)];
				// TODO: bug with the resource and unit. fix it!!!
				map.add(new Tile(getPosition(i, j), TileType.PLAINS,
						TileFeature.NONE, borders,
						null));
			}
	}
	// set playerTurn and set two units for each player and set their tileStates
	private void initPlayers()
	{
		playerTurn = players.get(0);
		// create two units for each player and set their starting positions and tileStates
		for(int i = 0; i < players.size(); i++)
		{
			Player player = players.get(i);
			Tile startingTile = player.getTileByXY(startingPositions[i].X, startingPositions[i].Y);
			// TODO: this units are temp. they should be modified in their constructors
			MidRange warrior = new MidRange(player, MidRangeType.WARRIOR, startingTile, MidRangeType.WARRIOR.movement, MidRangeType.WARRIOR.combatStrength);
			Settler settler = new Settler(player, startingTile);
			startingTile.setCombatUnitInTile(warrior);
			startingTile.setNonCombatUnitInTile(settler);
			player.updateTileStates();
		}
	}
	public boolean isTileInPlayerTerritory(Position position){
		for (City city : playerTurn.getCities()) {
			for (Tile tile : city.getTerritory()) {
				if(tile.getPosition().equals(position)) return true;
			}
		}
		return false;
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
	public void addPlayer(Player player)
	{
		players.add(player);
	}
	public void deletePlayer(Player player)
	{
		players.remove(player);
	}
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
		//TODO: write try catch for catching invalid input
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setGold(playerTurn.getGold() + amount);
		return (cheatCode.gold.regex + cheatCode.increaseSuccessful.regex);
	}
	public String increaseFood(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setFood(playerTurn.getFood() + amount);
		return (cheatCode.food.regex + cheatCode.increaseSuccessful.regex);
	}
	//TODO: this should be shorter. it should not do anything with cups.
	public String increaseTurns(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		turnCounter += amount;
		addTurn(amount);
		for(Player player : players)
			for (int i = 0; i < amount; i++)
				player.setCup(player.incomeCup());
		return (cheatCode.turn.regex + cheatCode.increaseSuccessful.regex);
	}

	public String increaseHappiness(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setHappiness(playerTurn.getHappiness() + amount);
		return (cheatCode.happiness.regex + cheatCode.increaseSuccessful.regex);
	}
	public String increaseHealth(Matcher matcher)
	{
		int x = Integer.parseInt(matcher.group("x"));
		int y = Integer.parseInt(matcher.group("y"));
		for(Unit unit : playerTurn.getUnits())
			if(unit.getTile().getPosition().X == x &&
					unit.getTile().getPosition().Y == y &&
					!unit.getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				unit.setHealth(100);
		return (cheatCode.health.regex + cheatCode.increaseSuccessful.regex);
	}
	public String increaseScore(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setScore(playerTurn.getScore() + amount);
		return cheatCode.score.regex + cheatCode.increaseSuccessful.regex;
	}
	public String addTechnology(Matcher matcher)
	{
		for(int i = 0; i < Technology.values().length; i++)
			if(Technology.values()[i].name().toLowerCase(Locale.ROOT).equals(matcher.group("name").toLowerCase(Locale.ROOT)) &&
					playerTurn.getTechnologies().containsAll(Technology.values()[i].requiredTechnologies))
			{
				playerTurn.addTechnology(Technology.values()[i]);
				return  matcher.group("name") + cheatCode.addSuccessful.regex;
			}
		return mainCommands.invalidCommand.regex;
	}
	public String winBattle(Matcher matcher)
	{
		int x = Integer.parseInt(matcher.group("positionX"));
		int y = Integer.parseInt(matcher.group("positionY"));
		//TODO:win battle
		return cheatCode.addSuccessful.regex;
	}
	public String moveUnit(Matcher matcher)
	{
		//TODO: move unit
		int x = Integer.parseInt(matcher.group("positionX"));
		int y = Integer.parseInt(matcher.group("positionY"));
		int newX = Integer.parseInt(matcher.group("newPositionX"));
		int newY = Integer.parseInt(matcher.group("newPositionY"));
		return cheatCode.addSuccessful.regex;
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
	public ArrayList<User> convertMapToArr(HashMap<String, String> players)
	{
		ArrayList<User> newArr = new ArrayList<>();
		newArr.add(Menu.loggedInUser);
		for(int i = 1; i < players.size() + 1; i++)
			newArr.add(registerController.getUserByUsername(players.entrySet().toArray()[i - 1].toString().substring(2)));
		return newArr;
	}
	public Civilization findCivilByNumber(int number)
	{
		switch (number % 10)
		{
			case 1:
				return Civilization.AMERICAN;
			case 2:
				return Civilization.ARABIAN;
			case 3:
				return Civilization.ASSYRIAN;
			case 4:
				return Civilization.CHINESE;
			case 5:
				return Civilization.GERMAN;
			case 6:
				return Civilization.GREEK;
			case 7:
				return Civilization.MAYAN;
			case 8:
				return Civilization.PERSIAN;
			case 9:
				return Civilization.OTTOMAN;
			case 0:
				return Civilization.RUSSIAN;
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
		if(n.length() == 0)
			return false;
		for(int i = 0; i < n.length(); i++)
		{
			if(n.charAt(i) > 57 || n.charAt(i) < 48)
				return false;
		}
		return true;
	}

	public void addTurn(int amount)
	{
		for(Player player : players)
		{
			int flg = -1;
			for(int i = 0; i < Technology.values().length; i++)
				if(Technology.values()[i] == player.getResearchingTechnology()) flg = i;
			if(flg != -1)
				player.addResearchingTechCounter(flg, amount);
		}
	}

	public String checkTechnology()
	{
		int flg = -1;
		for(int i = 0; i < Technology.values().length; i++)
			if(Technology.values()[i] == playerTurn.getResearchingTechnology()) flg = i;
		if(playerTurn.getResearchingTechnology() != null &&
				playerTurn.getResearchingTechnology().cost - playerTurn.getResearchingTechCounter()[flg] <= 0)
		{
			Technology tmp = playerTurn.getResearchingTechnology();
			playerTurn.addTechnology(tmp);
			playerTurn.setResearchingTechnology(null);
			return infoCommands.successGainTech.regex + tmp.name();
		}
		return null;
	}
	//DOC commands
	public BuildingType requiredTechForBuilding(Technology technology)
	{
		for(int i = 0; i < BuildingType.values().length; i++)
			if(BuildingType.values()[i].requiredTechnology == technology) return BuildingType.values()[i];
		return null;
	}
	public Improvement requiredTechForImprovement(Technology technology)
	{
		for(int i = 0; i < Improvement.values().length; i++)
			if(Improvement.values()[i].requiredTechnology == technology) return Improvement.values()[i];
		return null;
	}
	public String showResearch()
	{
		int flg = -1;
		for(int i = 0; i < Technology.values().length; i++)
			if(Technology.values()[i] == playerTurn.getResearchingTechnology()) flg = i;
		if(playerTurn.getResearchingTechnology() != null)
		{
			BuildingType requiredBuilding = requiredTechForBuilding(playerTurn.getResearchingTechnology());
			Improvement requiredImprovement = requiredTechForImprovement(playerTurn.getResearchingTechnology());
			if(requiredBuilding != null && requiredImprovement != null)
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ requiredBuilding.name() + " and " + requiredImprovement.name() + infoCommands.gainAfterGetTechnology.regex;
			else if(requiredBuilding != null)
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ requiredBuilding.name() + infoCommands.gainAfterGetTechnology.regex;
			else if(requiredImprovement != null)
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ requiredImprovement.name() + infoCommands.gainAfterGetTechnology.regex;
			else
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ infoCommands.notGain.regex;
		}
		return infoCommands.researchInfo.regex+infoCommands.currentResearching.regex + ": " + infoCommands.nothing.regex;
	}


	public String selectCUnit(String command)
	{
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
				if(x >= getInstance().MAP_SIZE || x < 0 ||
						y >= getInstance().MAP_SIZE || y < 0)
					return selectCommands.invalidRange.regex + (getInstance().MAP_SIZE - 1);
				for(Player player : players)
					for(int j = 0; j < player.getUnits().size(); j++)
						if(player.getUnits().get(j).getTile().getPosition().X == x &&
								player.getUnits().get(j).getTile().getPosition().Y == y &&
								!player.getUnits().get(j).getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
						{
							playerTurn.setSelectedUnit(player.getUnits().get(j));
							return selectCommands.selected.regex;
						}
				return selectCommands.coordinatesDoesntExistCUnit.regex+ x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}
	public String selectNUnit(String command)
	{
		int flag = -1;
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
				if(x >= getInstance().MAP_SIZE || x < 0 ||
						y >= getInstance().MAP_SIZE || y < 0)
					return selectCommands.invalidRange.regex + (getInstance().MAP_SIZE - 1);
				for(Player player : players)
					for(int j = 0; j < player.getUnits().size(); j++)
						if(player.getUnits().get(j).getTile().getPosition().X == x &&
								player.getUnits().get(j).getTile().getPosition().Y == y &&
								player.getUnits().get(j).getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
						{
							playerTurn.setSelectedUnit(player.getUnits().get(j));
							return selectCommands.selected.regex;
						}
				return selectCommands.coordinatesDoesntExistNUnit.regex+ x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}

	public String selectCity(String command)
	{
		int x = 0, y = 0;
		int flag = 0;
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
				for(Player player : players)
					for(int j = 0; j < player.getCities().size(); j++)
						if(player.getCities().get(j).getName().equals(cityName))
						{
							playerTurn.setSelectedCity(player.getCities().get(j));
							return selectCommands.selected.regex;
						}
				return selectCommands.nameDoesntExist.regex + cityName;
			}
			else if(matcher4 != null)
			{
				String cityName = matcher4.group("name");
				for(Player player : players)
					for(int j = 0; j < player.getCities().size(); j++)
						if(player.getCities().get(j).getName().equals(cityName))
						{
							playerTurn.setSelectedCity(player.getCities().get(j));
							return selectCommands.selected.regex;
						}
				return selectCommands.nameDoesntExist.regex + cityName;
			}
			if(matcher1 != null || matcher2 != null)
			{
				if(x >= getInstance().MAP_SIZE || x < 0 ||
						y >= getInstance().MAP_SIZE || y < 0)
					return selectCommands.invalidRange.regex + (getInstance().MAP_SIZE - 1);
				for(Player player : players)
					for(int j = 0; j < player.getCities().size(); j++)
						if(player.getCities().get(j).getCapitalTile().getPosition().X == x &&
								player.getCities().get(j).getCapitalTile().getPosition().Y == y)
						{
							playerTurn.setSelectedCity(player.getCities().get(j));
							return selectCommands.selected.regex;
						}
				return selectCommands.coordinatesDoesntExistCity.regex+ x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}
	public void updatePlayersUnitLocations(){
		for (Unit unit : this.getPlayerTurn().getUnits()) {
			if(unit.getMoves() != null && unit.getMoves().size() > 0){
				unit.move(getTileByXY(unit.getMoves().get(0).X, unit.getMoves().get(0).Y));
			}
		}
	}
	public void updateWorkersConstructions(){
		for (Unit unit : this.getPlayerTurn().getUnits()) {
			if(unit instanceof Worker){
				if(((Worker) unit).getTurnsTillBuildRoad() < 3)
					((Worker) unit).buildRoad();
				if(((Worker) unit).getTurnsTillBuildRailRoad() < 3)
					((Worker) unit).buildRailRoad();
				if(((Worker) unit).getTurnsTillRepairment() < 3)
					((Worker) unit).repairTile();
				if(((Worker) unit).getImprovements().get(0).inLineTurn < ((Worker) unit).getImprovements().get(0).turnToConstruct)
					((Worker) unit).buildFarm();
				if(((Worker) unit).getImprovements().get(1).inLineTurn < ((Worker) unit).getImprovements().get(1).turnToConstruct)
					((Worker) unit).buildMine();
			}
		}
	}
	public void handleUnitCommands(){
		for (Unit unit : playerTurn.getUnits()) {
			if(unit.getCommands().size() > 0){
				UnitCommandsHandler.handleCommands(unit, unit.getCommands().get(0));
				unit.getCommands().remove(0);
			}
		}
	}
	public String moveUnit(int x, int y)
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			//TODO: add errors
			else
			{
				playerTurn.getSelectedUnit().move(getTileByXY(x,y));
				return unitCommands.moveSuccessfull.regex;
			}
		}
		return gameEnum.nonSelect.regex;

	}
	public String sleep()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else
			{
				if (playerTurn.getSelectedUnit().isSleep())
					return gameEnum.isSleep.regex;
				else {
					playerTurn.getSelectedUnit().changeSleepWake();
					return gameEnum.slept.regex;
				}
			}
		}
		return gameEnum.nonSelect.regex;
	}
	public void stayAlert()
	{
		for (Unit tmp : playerTurn.getUnits())
			for(Player player : players)
				for(Unit unit : player.getUnits())
					if(player != playerTurn &&
							tmp.getTile().distanceTo(unit.getTile()) <= 2 &&
							tmp.isSleep())
						tmp.changeSleepWake();
	}
	public String alert()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			else
			{
				if (playerTurn.getSelectedUnit().isSleep())
					return gameEnum.isSleep.regex;
				else {
					playerTurn.getSelectedUnit().changeSleepWake();
					return unitCommands.standByUnit.regex;
				}
			}
		}
		return gameEnum.nonSelect.regex;
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
	public String attack()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			//TODO: DOC
			else
			{
				//TODO: add more errors
				playerTurn.getSelectedUnit().getTile().setImprovement(Improvement.NONE);
				return unitCommands.destroyImprovement.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	private boolean isCapitalCity(Tile tile)
	{
		int x = tile.getPosition().X;
		int y = tile.getPosition().Y;
		for(Player player : players)
			for (City city : player.getCities())
				if(city.getCapitalTile().getPosition().X == x &&
					city.getCapitalTile().getPosition().Y == y)
					return true;
		return false;
	}
	private boolean hasCity(Tile tile)
	{
		int x = tile.getPosition().X;
		int y = tile.getPosition().Y;
		for (Player player : players)
			for(City city : player.getCities())
				for(Tile tmp : city.getTerritory())
					if(tmp.getPosition().X == x &&
						tmp.getPosition().Y == y)
						return true;
		return false;
	}
	public String found()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(!playerTurn.getSelectedUnit().getClass().equals(Settler.class))
				return unitCommands.notSettler.regex;
			else if(isCapitalCity(playerTurn.getSelectedUnit().getTile()))
				return unitCommands.isCapitalCity.regex;
			else if(hasCity(playerTurn.getSelectedUnit().getTile()))
				return unitCommands.hasCity.regex;
			else
			{
				((Settler) playerTurn.getSelectedUnit()).createCity();
				return unitCommands.cityBuilt.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String cancel()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else
			{
				playerTurn.getSelectedUnit().getCommands().clear();
				return unitCommands.cancelCommand.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String wake()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else
			{
				if (!playerTurn.getSelectedUnit().isSleep())
					return gameEnum.awaken.regex;
				else {
					playerTurn.getSelectedUnit().changeSleepWake();
					return gameEnum.wokeUp.regex;
				}
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	private int getGoldOfUnit(Unit unit)
	{
		int number = 0;
		if(unit.getClass().equals(Settler.class)) number = 89;
		if(unit.getClass().equals(Worker.class)) number = 40;
		if(unit.getClass().equals(LongRange.class)) number = ((LongRange) unit).getType().getCost();
		if(unit.getClass().equals(MidRange.class)) number = ((MidRange) unit).getType().getCost();
		return number / 10;
	}
	public String delete()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else
			{
				int gold = getGoldOfUnit(playerTurn.getSelectedUnit());
				playerTurn.getSelectedUnit().removeUnit();
				playerTurn.setGold(playerTurn.getGold() + gold);
				return unitCommands.removeUnit.regex + unitCommands.gainGold.regex + (gold) + unitCommands.gold.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}

	private String buildErrors()
	{
		if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
			return unitCommands.notYours.regex;
		else if(!playerTurn.getSelectedUnit().getClass().equals(Worker.class))
			return unitCommands.notWorker.regex;
		return null;
	}
	public String road()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().hasRoad())
				return unitCommands.hasRoad.regex;
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildRoad();
				return unitCommands.roadBuilt.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String  railRoad()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().hasRailRoad())
				return unitCommands.hasRailRoad.regex;
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildRailRoad();
				return unitCommands.railRoadBuilt.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String farm()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.FARM))
				return unitCommands.hasFarm.regex;
			//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildFarm();
				return unitCommands.farmBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String  mine()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.MINE))
				return unitCommands.hasMine.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildMine();
				return unitCommands.mineBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String tradingPost()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.TRADING_POST))
				return unitCommands.hasTradingPost.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildTradingPost();
				return unitCommands.tradingPostBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String lumberMill()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.LUMBER_MILL))
				return unitCommands.hasLumberMill.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildLumberMill();
				return unitCommands.lumberMillBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String pasture()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.PASTURE))
				return unitCommands.hasPasture.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildPasture();
				return unitCommands.pastureBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String camp()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.CAMP))
				return unitCommands.hasCamp.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildCamp();
				return unitCommands.campBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String plantation()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.PLANTATION))
				return unitCommands.hasPlantation.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildPlantation();
				return unitCommands.plantationBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String quarry()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.QUARRY))
				return unitCommands.hasQuarry.regex;
				//TODO: cant build(reasons)
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildQuarry();
				return unitCommands.quarryBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String removeJungle()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(!playerTurn.getSelectedUnit().getTile().getTileFeature().equals(TileFeature.JUNGLE) &&
					!playerTurn.getSelectedUnit().getTile().getTileFeature().equals(TileFeature.FOREST))
				return unitCommands.hasntJungle.regex;
			else
			{
				((Worker) playerTurn.getSelectedUnit()).removeJungle();
				return unitCommands.jungleRemoved.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String removeRoute()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(!playerTurn.getSelectedUnit().getTile().hasRoad() &&
					!playerTurn.getSelectedUnit().getTile().hasRailRoad())
				return unitCommands.hasntRoad.regex;
			else
			{
				((Worker) playerTurn.getSelectedUnit()).removeRoads();
				return unitCommands.roadRemoved.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String repair()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(!playerTurn.getSelectedUnit().getTile().isRuined())
				return unitCommands.isNotRuined.regex;
			else
			{
				((Worker) playerTurn.getSelectedUnit()).repairTile();
				return unitCommands.repairedSuccessful.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}

	public void resetSelectedObjects()
	{
		getPlayerTurn().setSelectedUnit(null);
		getPlayerTurn().setSelectedCity(null);
	}
}