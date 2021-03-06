package Controllers;

import Controllers.Utilities.MapPrinter;
import Models.City.Building;
import Models.City.BuildingType;
import Models.City.Citizen;
import Models.City.City;
import Models.City.*;
import Models.Player.*;
import Models.Menu.Menu;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.*;
import Models.Terrain.*;
import Models.TypeAdapters.*;
import Models.Units.CombatUnits.*;
import Models.Units.CommandHandeling.UnitCommands;
import Models.Units.CommandHandeling.UnitCommandsHandler;
import Models.Units.NonCombatUnits.*;
import Models.Units.Unit;
import Models.Units.UnitState;
import Models.User;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import enums.cheatCode;
import enums.gameCommands.infoCommands;
import enums.gameCommands.selectCommands;
import enums.gameCommands.unitCommands;
import enums.gameEnum;
import enums.mainCommands;

import java.io.IOException;
import java.io.Serializable;
import java.util.*;
import java.util.regex.Matcher;

public class GameController implements Serializable
{
	private static GameController instance = null;
	private final ArrayList<Position> grid = new ArrayList<>();
	public final int MAX_GRID_LENGTH = 30;
	private final ArrayList<Tile> map = new ArrayList<>();
	public int MAP_SIZE;
	private final ArrayList<Player> players = new ArrayList<>();
	transient private Player playerTurn;
	public int playerTurnIndex;
	private final Position[] startingPositions = new Position[]{new Position(5, 5), new Position(1, 8), new Position(8, 1), new Position(8, 8)};
	private final RegisterController registerController = new RegisterController();
	private int turnCounter = 0;
	private int yearCounter = 2000;
	private boolean isGameStarted = true;

	public int getMAP_SIZE() {
		return MAP_SIZE;
	}

	public ArrayList<Position> getGrid() {
		return grid;
	}

	public Gson gson;


	// private constructor to prevent instantiation
	private GameController()
	{
		initGrid();

		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Construction.class, new ConstructionTypeAdapter());
		gsonBuilder.registerTypeAdapter(CombatUnit.class, new CUnitTypeAdapter());
		gsonBuilder.registerTypeAdapter(NonCombatUnit.class, new NCUnitTypeAdapter());
		gsonBuilder.registerTypeAdapter(Resource.class, new ResourceTypeAdapter());
		gsonBuilder.registerTypeAdapter(Unit.class, new UnitTypeAdapter());
		gson = gsonBuilder.create();
	}
	// this method is called to get the GameController singleton instance
	public static synchronized GameController getInstance()
	{
		if(instance == null)
			instance = new GameController();
		return instance;
	}
	public static void setInstance(GameController instance)
	{
		GameController.instance = instance;
	}
	// this method checks that everything before changing turn to the next player is done. (i.e. check if all units have used their turns) //TODO: is this needed?
	// if everything is ok, it calls the changeTurn method
	public String checkChangeTurn()
	{
		// if there is a unit which has not used its turn, it returns the unit's name with error message //TODO: is this needed?
		//TODO: check if unit has used its turn

		//if city size == 0
		if (isGameEnd() != null)
			return "game Ended";
		changeTurn();
		return "turn changed"; //TODO: return: "turn changed successfully"
	}
	// this updates changes turn to the next player (i.e. reset all units turns and decrement researching technology turns)
	private void changeTurn()
	{
		//add building benefits
		addBuildingBenefits();
		//update cities combat strength
		for(Player player : players)
		{
			player.updateTileStates();
			for(City city : player.getCities())
				city.updateCityCombatStrength();
		}

		playerTurn.setSelectedUnit(null);
		playerTurn.setSelectedCity(null);

		processResearchingTechnology();
		processFoodForChangingTurn();
		processGoldForChangingTurn();
		processResourcesForChangingTurn();
		// gain production (maybe?)

		//happiness
		if(playerTurn.getTotalPopulation() >= playerTurn.getMaxPopulation() + 10)
		{
			playerTurn.setHappiness((int) (playerTurn.getHappiness() * 0.95));
			playerTurn.setMaxPopulation(playerTurn.getTotalPopulation());
		}
		if(!playerTurn.getIsUnHappy() && playerTurn.getHappiness() < 0)
			playerTurn.isUnHappy();

		// update cups
		playerTurn.setCup(playerTurn.getCup() + playerTurn.incomeCup());

		// handle units
		//handleUnitCommands();
		updatePlayersUnitLocations();
		updateWorkersConstructions();
		updateUnitStates();

		// decrement researching technology turns
		// check for city growth

		//TODO: check that this is not a duplicate from runGame while loop
		if(players.indexOf(playerTurn) == 0)
		{
			turnCounter++;
			if (turnCounter % 5 == 0)
				yearCounter++;
			updateFortifyTilHeal();
			updateCityConstructions();
		}

		//end game
		players.removeIf(player -> player.isHasCity() && player.getCities().size() == 0);

		if (isGameEnd() != null && isGameStarted) {
			isGameStarted = false;
			Player winner = isGameEnd();
			User userWinner = Menu.allUsers.get(Menu.allUsers.indexOf(
					registerController.getUserByUsername(winner.getUsername())));
			userWinner.setScore(userWinner.getScore() + winner.getGameScore() / 10);
			registerController.writeDataOnJson();
		}

		playerTurn.setIsYourTurn(false);
		// change playerTurn
		playerTurn = players.get((players.indexOf(playerTurn) + 1) % players.size());
		playerTurn.setIsYourTurn(true);
	}

	private void addBuildingBenefits() {
		for (City city : playerTurn.getCities()) {
			for (Building building : city.getBuildings()) {
				switch (building.getBuildingType()){
					case BARRACKS, ARMORY, MILITARY_ACADEMY -> playerTurn.getUnits().forEach(unit -> unit.setXP(unit.getXP() + 15));
					case GRANARY -> city.setFoodYield(city.getFoodYield() + 5);
					case LIBRARY -> playerTurn.setCup(playerTurn.getCup() + (int) (city.getPopulation() / 2.0));
					case WALLS -> city.setHitPoints(city.getHitPoints() + 5);
					case WATER_MILL -> {
						for (Tile tile : city.getTerritory()) {
							if(Arrays.stream(tile.getBorders()).toList().contains(BorderType.RIVER)){
								city.setFoodYield(city.getFoodYield() + 2);
								break;
							}
						}
					}
					case BURIAL_TOMB -> playerTurn.setHappiness(playerTurn.getHappiness() + 2);
					case CIRCUS ->  playerTurn.setHappiness(playerTurn.getHappiness() + 3);
					case COLOSSEUM, THEATER -> playerTurn.setHappiness(playerTurn.getHappiness() + 4);
					case COURTHOUSE -> playerTurn.isHappy();
					case STABLE, WINDMILL -> city.setProductionYield((int) (city.getProductionYield() * 1.25));
					case CASTLE -> city.setHitPoints(city.getHitPoints() + 8);
					case FORGE -> city.setProductionYield((int) (city.getProductionYield() * 1.15));
					case GARDEN -> {
						for (Tile tile : city.getTerritory()) {
							if(Arrays.stream(tile.getBorders()).toList().contains(BorderType.RIVER)){
								city.addPopulation((int)(city.getPopulation() * 0.25));
								break;
							}
						}
					}
					case MARKET, BANK -> city.setGoldYield((int) (city.getGoldYield() * 1.25));
					case MINT -> {
						for (Tile tile : city.getTerritory()) {
							if(tile.getResource().getRESOURCE_TYPE().equals(ResourceType.GOLD ) ||
									tile.getResource().getRESOURCE_TYPE().equals(ResourceType.SILVER ))
								tile.getResource().getRESOURCE_TYPE().setGold(tile.getResource().getRESOURCE_TYPE().gold + 3);
						}
					}
					case UNIVERSITY, PUBLIC_SCHOOL -> playerTurn.setCup((int) (playerTurn.getCup() * 1.5));
					case WORKSHOP, ARSENAL -> city.setProductionYield((int) (city.getProductionYield() * 1.2));
					case SATRAPS_COURT -> {
						city.setGoldYield((int) (city.getGoldYield() * 1.25));
						playerTurn.setHappiness(playerTurn.getHappiness() + 2);
					}
					case FACTORY -> city.setProductionYield((int) (city.getProductionYield() * 1.5));
					case MILITARY_BASE -> city.setHitPoints(city.getHitPoints() + 12);
					case STOCK_EXCHANGE -> city.setGoldYield((int) (city.getGoldYield() * 1.33));
				}
			}
		}
	}

	private void updateUnitStates() {
		for (Unit unit : playerTurn.getUnits()) {
			if(unit.getUnitState().equals(UnitState.FORTIFIED)) ( (CombatUnit)unit).fortify();
			else if(unit.getUnitState().equals(UnitState.ALERT)) unit.setAlert();
		}
	}

	private void processFoodForChangingTurn()
	{
		int foodYieldOfPlayerTurn = 0;
		for(City city : playerTurn.getCities())
			foodYieldOfPlayerTurn += city.getFoodYield();
		//TODO: if foodYield is negative, some citizens should starve to death :')
		playerTurn.setFood(playerTurn.getFood() + foodYieldOfPlayerTurn);

		// food penalty when citizens are unhappy (our food *= 33%)
		if(playerTurn.getHappiness() < 0 && playerTurn.getFood() > 0)
			playerTurn.setFood((int) (playerTurn.getFood() * 0.33));
	}
	//TODO: gaining food should be optimized
	// this method gains food based on citizens that are working on tiles (and maybe other things)
	private void processGoldForChangingTurn()
	{
		int goldYieldOfPlayerTurn = 0;
		for(City city : playerTurn.getCities())
			goldYieldOfPlayerTurn += city.getGoldYield();

		playerTurn.setGold(playerTurn.getGold() + goldYieldOfPlayerTurn);
	}
	private void processResourcesForChangingTurn()
	{
		for(City city : playerTurn.getCities())
		{
			for(Citizen citizen : city.getCitizens())
			{
				if(citizen.getWorkingTile() == null)
					continue;
				if(citizen.getWorkingTile().getResource() == null)
					continue;
				if(!citizen.getWorkingTile().getResource().getRESOURCE_TYPE().requiredImprovement.equals(citizen.getWorkingTile().getImprovement()))
					continue;

				playerTurn.addResource(citizen.getWorkingTile().getResource().clone());
			}
		}
	}
	public void initGame()
	{
		if (players.size() == 2)
			MAP_SIZE = 10;
		else if (players.size() == 3)
			MAP_SIZE = 12;
		else
			MAP_SIZE = 15;
		initMap();
		for (Player player : players)
			player.initMap();
		initPlayers();
		setPlayersRelations();
	}
	public String gameControllerToJson(GameController gameController)
	{
		gameController.playerTurnIndex = gameController.getPlayers().indexOf(gameController.getPlayerTurn());
		for (Player player : gameController.getPlayers())
		{
			for (Unit unit : player.getUnits())
				unit.lastPositionForSave = new Position(unit.getTile().getPosition().X, unit.getTile().getPosition().Y);

			player.mapKeyset.clear();
			player.mapValueset.clear();
			for (Tile tile : player.getMap().keySet())
			{
				player.mapKeyset.add(tile);
				player.mapValueset.add(player.getMap().get(tile));
			}
		}

		String gameStr = gson.toJson(gameController);

		return gameStr;
	}
	public GameController jsonToGameController(String jsonStr)
	{
		GameController loadedGameController = gson.fromJson(jsonStr, GameController.class);
		for (Player player : loadedGameController.getPlayers())
		{
			player.setGameController(loadedGameController);
			// set player map
			HashMap<Tile, TileState> playerMap = new HashMap<>();
			for (int i = 0; i < player.mapKeyset.size(); i++)
			{
				if(player.mapValueset.get(i).equals(TileState.REVEALED))
					playerMap.put(player.mapKeyset.get(i), player.mapValueset.get(i));
				else
					playerMap.put(loadedGameController.getTileByXY(player.mapKeyset.get(i).getPosition().X, player.mapKeyset.get(i).getPosition().Y), player.mapValueset.get(i));
			}
			player.setMap(playerMap);

			// set transient fields
			for (City city : player.getCities())
			{
				city.setRulerPlayer(player);
				for (Citizen citizen : city.getCitizens())
					citizen.setCity(city);

				for (Building building : city.getBuildings())
					building.setTile(player.getTileByXY(building.getTile().getPosition().X, building.getTile().getPosition().Y));
			}
			for (Unit unit : player.getUnits())
			{
				unit.setRulerPlayer(player);

				Tile tile = player.getTileByXY(unit.lastPositionForSave.X, unit.lastPositionForSave.Y);
				unit.setTile(tile);
				if(unit instanceof CombatUnit)
					tile.setCombatUnitInTile((CombatUnit) unit);
				else
					tile.setNonCombatUnitInTile((NonCombatUnit) unit);
			}
		}
		loadedGameController.setPlayerTurn(loadedGameController.getPlayers().get(loadedGameController.playerTurnIndex));

		return loadedGameController;
	}
	public synchronized String playerToJson(Player player)
	{
		player.MAP_SIZE = MAP_SIZE;
		player.updateTileStates();

		player.enemyUnits.clear();
		// set map
		player.mapKeyset.clear();
		player.mapValueset.clear();
		for (Tile tile : player.getMap().keySet())
		{
			if(tile.getCombatUnitInTile() != null && !tile.getCombatUnitInTile().getRulerPlayer().getCivilization().equals(player.getCivilization()))
			{
				tile.getCombatUnitInTile().lastPositionForSave = new Position(tile.getPosition().X, tile.getPosition().Y);
				player.enemyUnits.add(tile.getCombatUnitInTile());
			}
			if(tile.getNonCombatUnitInTile() != null && !tile.getNonCombatUnitInTile().getRulerPlayer().getCivilization().equals(player.getCivilization()))
			{
				tile.getNonCombatUnitInTile().lastPositionForSave = new Position(tile.getPosition().X, tile.getPosition().Y);
				player.enemyUnits.add(tile.getNonCombatUnitInTile());
			}

			player.mapKeyset.add(tile);
			player.mapValueset.add(player.getMap().get(tile));
		}
		// set units last position
		for (Unit unit : player.getUnits())
			unit.lastPositionForSave = new Position(unit.getTile().getPosition().X, unit.getTile().getPosition().Y);


		// set trade requests
		for (TradeRequest tradeRequest : player.getTradeRequests())
			tradeRequest.senderCivilization = tradeRequest.getSender().getCivilization();


		return gson.toJson(player);
	}

	// TODO: must be on client's side
	public Player jsonToPlayer(String playerJson)
	{
		Player player = gson.fromJson(playerJson, Player.class);

		// set units
		for (Unit unit : player.getUnits())
		{
			unit.setRulerPlayer(player);
			unit.setTile(player.getTileByXY(unit.lastPositionForSave.X, unit.lastPositionForSave.Y));
			if(unit instanceof CombatUnit)
				unit.getTile().setCombatUnitInTile((CombatUnit) unit);
			else
				unit.getTile().setNonCombatUnitInTile((NonCombatUnit) unit);
			unit.setDestination(player.getTileByXY(unit.getDestination().getPosition().X, unit.getDestination().getPosition().Y));
		}
		// set city tiles
		for (City city : player.getCities())
		{
			for (int i = 0; i < city.getTerritory().size(); i++)
				city.getTerritory().set(i, player.getTileByXY(city.getTerritory().get(i).getPosition().X, city.getTerritory().get(i).getPosition().Y));
			city.setCapitalTile(player.getTileByXY(city.getCapitalTile().getPosition().X, city.getCapitalTile().getPosition().Y));
			// set buildings
			for (Building building : city.getBuildings())
			{
				building.setTile(player.getTileByXY(building.getTile().getPosition().X, building.getTile().getPosition().Y));
				building.setCity(city);
			}
			// set citizens
			for (int i = 0; i < city.getCitizens().size(); i++)
			{
				city.getCitizens().get(i).setCity(city);
				city.getCitizens().get(i).setWorkingTile(player.getTileByXY(city.getCitizens().get(i).getWorkingTile().getPosition().X, city.getCitizens().get(i).getWorkingTile().getPosition().Y));
			}

			if(city.getCurrentConstruction() instanceof CombatUnit)
				city.setCurrentConstruction((player.getTileByXY(((CombatUnit) city.getCurrentConstruction()).getTile().getPosition().X, ((CombatUnit) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
			else if(city.getCurrentConstruction() instanceof NonCombatUnit)
				city.setCurrentConstruction((player.getTileByXY(((NonCombatUnit) city.getCurrentConstruction()).getTile().getPosition().X, ((NonCombatUnit) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
			else if(city.getCurrentConstruction() instanceof Building)
				city.setCurrentConstruction((player.getTileByXY(((Building) city.getCurrentConstruction()).getTile().getPosition().X, ((Building) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());

			city.setGarrison(player.getTileByXY(city.getGarrison().getTile().getPosition().X, city.getGarrison().getTile().getPosition().Y).getCombatUnitInTile());
			city.setNonCombatUnit(player.getTileByXY(city.getNonCombatUnit().getTile().getPosition().X, city.getNonCombatUnit().getTile().getPosition().Y).getNonCombatUnitInTile());

			city.setRulerPlayer(player);

			if(city.getCapitalTile().getPosition().equals(player.getInitialCapitalCity().getCapitalTile().getPosition()))
				player.setInitialCapitalCity(city);
			if(city.getCapitalTile().getPosition().equals(player.getCurrentCapitalCity().getCapitalTile().getPosition()))
				player.setCurrentCapitalCity(city);

		}
		// set trade requests
		for (TradeRequest tradeRequest : player.getTradeRequests())
		{
			// TODO:
		}

		return player;
	}

	private void setPlayersRelations() {
		for (Player player : players) {
			for (Player player1 : players) {
				player.addRelationStates(player1.getCivilization(), RelationState.NEUTRAL);
			}
		}
	}

	public ArrayList<Tile> getMap()
	{
		return map;
	}
	public String getMapString()
	{
		return MapPrinter.getMapString(playerTurn, this);
	}
	public String getMapString(Player player)
	{
		return MapPrinter.getMapString(player, this);
	}
	// this method returns a mapString that all it's tiles are visible
	public String getRawMapString() throws IOException
	{
		Player tmpPlayer = new Player(Civilization.PERSIAN, "tmpPlayer", "tmpPlayer", "tmpPlayer", 0);
		tmpPlayer.initMap();
		tmpPlayer.getMap().replaceAll((k, v) -> TileState.VISIBLE);

		return MapPrinter.getMapString(tmpPlayer, this);
	}
	// this is called when GameController is created. this method only creates an array of Positions and fills grid with these positions
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
		if (MAP_SIZE == 10)
			makeMap1();
		else if (MAP_SIZE == 12)
			makeMap2();
		else if (MAP_SIZE == 15)
			makeMap3();
	}
	private void makeMap1()
	{
		map.clear();

		map.add(new Tile(getPosition(0, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 2), TileType.DESERT, TileFeature.FLOOD_PLAIN, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 3), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(0, 4), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(0, 5), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 6), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.HORSES)));
		map.add(new Tile(getPosition(0, 7), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(0, 8), TileType.SNOW, TileFeature.ICE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(0, 9), TileType.SNOW, TileFeature.ICE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(1, 0), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(1, 1), TileType.GRASSLAND, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(1, 2), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(1, 3), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 4), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(1, 5), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 6), TileType.GRASSLAND, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(1, 7), TileType.HILLS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(1, 8), TileType.TUNDRA, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 9), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(2, 0), TileType.RUIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 1), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(2, 2), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.SILVER)));
		map.add(new Tile(getPosition(2, 3), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 4), TileType.GRASSLAND, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(2, 5), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(2, 6), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 7), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 8), TileType.HILLS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(2, 9), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(3, 0), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 1), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 2), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 3), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(3, 4), TileType.GRASSLAND, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(3, 5), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(3, 6), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(3, 7), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 8), TileType.TUNDRA, TileFeature.FOREST, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, new LuxuryResource(ResourceType.MARBLE)));
		map.add(new Tile(getPosition(3, 9), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(4, 0), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 1), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(4, 2), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.SILK)));
		map.add(new Tile(getPosition(4, 3), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 4), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(4, 5), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, new LuxuryResource(ResourceType.FURS)));
		map.add(new Tile(getPosition(4, 6), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 7), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(4, 8), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 9), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 0), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(5, 1), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 2), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 3), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.IVORY)));
		map.add(new Tile(getPosition(5, 4), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.DYES)));
		map.add(new Tile(getPosition(5, 5), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 7), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new LuxuryResource(ResourceType.GOLD)));
		map.add(new Tile(getPosition(5, 8), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 9), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 0), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 1), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 2), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(6, 3), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(6, 4), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 5), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(6, 6), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(6, 7), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 8), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 9), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 0), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(7, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 2), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 4), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(7, 5), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.INCENSE)));
		map.add(new Tile(getPosition(7, 6), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 7), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 8), TileType.GRASSLAND, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(7, 9), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(8, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 2), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new LuxuryResource(ResourceType.COTTON)));
		map.add(new Tile(getPosition(8, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.GEMS)));
		map.add(new Tile(getPosition(8, 4), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 5), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 6), TileType.SNOW, TileFeature.ICE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(8, 7), TileType.PLAINS, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.SUGAR)));
		map.add(new Tile(getPosition(8, 8), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 9), TileType.PLAINS, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(9, 0), TileType.DESERT, TileFeature.FLOOD_PLAIN, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.COTTON)));
		map.add(new Tile(getPosition(9, 1), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(9, 2), TileType.RUIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(9, 3), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 4), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 5), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 6), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 7), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 8), TileType.RUIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 9), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
	}
	private void makeMap2()
	{
		map.clear();

		map.add(new Tile(getPosition(0, 0), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 1), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 2), TileType.HILLS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(0, 3), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(0, 4), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 5), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(0, 6), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(0, 7), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 8), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 9), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(0, 10), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.GOLD)));
		map.add(new Tile(getPosition(0, 11), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 0), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.GEMS)));
		map.add(new Tile(getPosition(1, 1), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(1, 2), TileType.HILLS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 3), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 4), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(1, 5), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(1, 6), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 7), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.SUGAR)));
		map.add(new Tile(getPosition(1, 8), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 9), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 10), TileType.HILLS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(1, 11), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 0), TileType.HILLS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.MARBLE)));
		map.add(new Tile(getPosition(2, 1), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(2, 2), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.SILK)));
		map.add(new Tile(getPosition(2, 3), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 4), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.DYES)));
		map.add(new Tile(getPosition(2, 5), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new LuxuryResource(ResourceType.IVORY)));
		map.add(new Tile(getPosition(2, 6), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 7), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(2, 8), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.GEMS)));
		map.add(new Tile(getPosition(2, 9), TileType.HILLS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.DYES)));
		map.add(new Tile(getPosition(2, 10), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.IVORY)));
		map.add(new Tile(getPosition(2, 11), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(3, 0), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(3, 1), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 2), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(3, 3), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 4), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(3, 5), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(3, 6), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 7), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(3, 8), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 9), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(3, 10), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 11), TileType.PLAINS, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 0), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 1), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(4, 2), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(4, 3), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 4), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(4, 5), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(4, 6), TileType.DESERT, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 7), TileType.DESERT, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(4, 8), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 9), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(4, 10), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 11), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 0), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(5, 1), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(5, 2), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 3), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 4), TileType.TUNDRA, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, new StrategicResource(ResourceType.HORSES)));
		map.add(new Tile(getPosition(5, 5), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 6), TileType.DESERT, TileFeature.FOREST, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.RIVER}, new LuxuryResource(ResourceType.INCENSE)));
		map.add(new Tile(getPosition(5, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.INCENSE)));
		map.add(new Tile(getPosition(5, 9), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(5, 10), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 11), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(6, 0), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 1), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, new LuxuryResource(ResourceType.GEMS)));
		map.add(new Tile(getPosition(6, 2), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 3), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 4), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(6, 5), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(6, 6), TileType.DESERT, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(6, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(6, 9), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(6, 10), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(6, 11), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 0), TileType.HILLS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 1), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 2), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(7, 3), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(7, 4), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(7, 5), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 6), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(7, 7), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 9), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(7, 10), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 11), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 1), TileType.PLAINS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(8, 3), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.GOLD)));
		map.add(new Tile(getPosition(8, 4), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.GOLD)));
		map.add(new Tile(getPosition(8, 5), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(8, 6), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 7), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.IRON)));
		map.add(new Tile(getPosition(8, 8), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 9), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new StrategicResource(ResourceType.HORSES)));
		map.add(new Tile(getPosition(8, 10), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(8, 11), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new StrategicResource(ResourceType.COAL)));
		map.add(new Tile(getPosition(9, 1), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 2), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.SHEEP)));
		map.add(new Tile(getPosition(9, 3), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(9, 4), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 5), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(9, 6), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 7), TileType.OCEAN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 8), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(9, 9), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(9, 10), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 11), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(10, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 1), TileType.DESERT, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.COTTON)));
		map.add(new Tile(getPosition(10, 2), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.COTTON)));
		map.add(new Tile(getPosition(10, 3), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 4), TileType.HILLS, TileFeature.FOREST, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.FURS)));
		map.add(new Tile(getPosition(10, 5), TileType.PLAINS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER, BorderType.RIVER}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(10, 6), TileType.HILLS, TileFeature.FOREST, new BorderType[]{BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.MARBLE)));
		map.add(new Tile(getPosition(10, 7), TileType.GRASSLAND, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.BANANA)));
		map.add(new Tile(getPosition(10, 8), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(10, 9), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 10), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 11), TileType.SNOW, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.SILVER)));
		map.add(new Tile(getPosition(11, 1), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 2), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 3), TileType.GRASSLAND, TileFeature.MARSH, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 4), TileType.MOUNTAIN, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 5), TileType.PLAINS, TileFeature.FLOOD_PLAIN, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.RIVER}, null));
		map.add(new Tile(getPosition(11, 6), TileType.HILLS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.RIVER, BorderType.RIVER, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.DEER)));
		map.add(new Tile(getPosition(11, 7), TileType.HILLS, TileFeature.JUNGLE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 8), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.WHEAT)));
		map.add(new Tile(getPosition(11, 9), TileType.GRASSLAND, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new BonusResource(ResourceType.CATTLE)));
		map.add(new Tile(getPosition(11, 10), TileType.PLAINS, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 11), TileType.TUNDRA, TileFeature.NONE, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, new LuxuryResource(ResourceType.FURS)));
	}
	private void makeMap3()
	{
		map.clear();

		map.add(new Tile(getPosition(0, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(0, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(1, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(2, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(3, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(4, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(5, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(6, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(7, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(8, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(9, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(10, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(11, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(12, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(13, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 0), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 1), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 2), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 3), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 4), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 5), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 6), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 7), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 8), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 9), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 10), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 11), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 12), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 13), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
		map.add(new Tile(getPosition(14, 14), TileType.DESERT, TileFeature.OASIS, new BorderType[]{BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE, BorderType.NONE}, null));
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
			new MidRange(player, MidRangeType.WARRIOR, startingTile);
			new Settler(player, startingTile);
			player.updateTileStates();
		}
	}
	public boolean isTileInPlayerTerritory(Position position)
	{
		for(City city : playerTurn.getCities())
			for(Tile tile : city.getTerritory())
			{
				if(tile.getPosition().equals(position))
					return true;
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
	private static boolean numberOfPlayers(HashMap<String, String> players)
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

	public synchronized Player getPlayerBuUsername(String username)
	{
		for (Player player : players)
			if(player.getUsername().equals(username))
				return player;

		return null;
	}
	public Player getPlayerTurn()
	{
		return playerTurn;
	}
	public void setPlayerTurn(Player playerTurn)
	{
		this.playerTurn = playerTurn;
	}
	public int getTurnCounter()
	{
		return turnCounter;
	}
	public synchronized ArrayList<Player> getPlayers()
	{
		return players;
	}
	public void removeAllPlayers()
	{
		if(players.size() > 0)
			players.subList(0, players.size()).clear();
	}

	private boolean existingPlayers(HashMap<String, String> players)
	{ //TODO: probable bug
		for(Object key : players.keySet())
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

			if(matcher1 != null && !matcher1.group("username").equals(Menu.loggedInUser.getUsername()))
			{
				players.put(matcher1.group("number"), matcher1.group("username"));
				flag = 1;
			}
			else if(matcher2 != null && !matcher2.group("username").equals(Menu.loggedInUser.getUsername()))
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
		else if(players.size() > 3)
			return gameEnum.lessThanFour.regex;
		else
			return gameEnum.successfulStartGame.regex;
	}
	public int powerForce(Unit unit)
	{
		if(unit.getClass().equals(MidRange.class))
			return ((MidRange) unit).getType().getCombatStrength();
		if(unit.getClass().equals(LongRange.class))
			return ((LongRange) unit).getType().getCombatStrength();
		return 0;
	}

	//cheat codes
	public String increaseGold(Matcher matcher)
	{
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
		for(int i = 0; i < amount; i++)
			for(int j = 0; j < players.size(); j++)
				changeTurn();
		return (cheatCode.turn.regex + cheatCode.increaseSuccessful.regex);
	}
	public String increaseHappiness(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		playerTurn.setHappiness(playerTurn.getHappiness() + amount);
		if(playerTurn.getHappiness() > 100)
			playerTurn.setHappiness(100);
		return (cheatCode.happiness.regex + cheatCode.increaseSuccessful.regex);
	}
	public String increaseHealth(Matcher matcher)
	{
		int x = Integer.parseInt(matcher.group("x"));
		int y = Integer.parseInt(matcher.group("y"));
		Tile tile = playerTurn.getTileByXY(x, y);
		// validation
		if(tile == null)
			return mainCommands.invalidCommand.regex;
		Unit unit = tile.getCombatUnitInTile();
		if(unit == null)
			return mainCommands.invalidCommand.regex;

		// increase health
		unit.setHealth(10);

		return (cheatCode.health.regex + cheatCode.increaseSuccessful.regex);
	}
	public String increaseScore(Matcher matcher)
	{
		int amount = Integer.parseInt(matcher.group("amount"));
		int index = Menu.allUsers.indexOf(registerController.getUserByUsername(playerTurn.getUsername()));
		playerTurn.setScore(playerTurn.getScore() + amount);
		Menu.allUsers.get(index).setScore(Menu.allUsers.get(index).getScore() + amount);
		registerController.writeDataOnJson();
		return cheatCode.score.regex + cheatCode.increaseSuccessful.regex;
	}
	public String winGame()
	{
		int index = Menu.allUsers.indexOf(registerController.getUserByUsername(playerTurn.getUsername()));
		playerTurn.setScore(playerTurn.getScore() + 5);
		Menu.allUsers.get(index).setScore(Menu.allUsers.get(index).getScore() + 5);
		registerController.writeDataOnJson();
		return cheatCode.youWin.regex;
	}
	public String addTechnology(Matcher matcher)
	{
		for(int i = 0; i < Technology.values().length; i++)
			if(Technology.values()[i].name().toLowerCase(Locale.ROOT).equals(matcher.group("name").toLowerCase(Locale.ROOT)) &&
					playerTurn.getTechnologies().containsAll(Technology.values()[i].requiredTechnologies))
			{
				playerTurn.setGameScore(playerTurn.getGameScore() + 3 * MAP_SIZE);
				playerTurn.addTechnology(Technology.values()[i]);
				return matcher.group("name") + cheatCode.addSuccessful.regex;
			}
		return mainCommands.invalidCommand.regex;
	}
	public String gainAllTechnologies()
	{
		for (Technology value : Technology.values())
		{
			playerTurn.addTechnology(value);
			playerTurn.setGameScore(playerTurn.getGameScore() + 3);
		}

		return "now you have access to all technologies";
	}
	public String killEnemyUnit(Matcher matcher) //TODO: change to killUnit. with this cheat code, we can kill any opponent unit.
	{ //TODO: check for bugs
		int x = Integer.parseInt(matcher.group("positionX"));
		int y = Integer.parseInt(matcher.group("positionY"));

		Tile givenTile = getTileByXY(x, y);

		// validation
		if(givenTile == null)
			return mainCommands.invalidCommand.regex;
		CombatUnit enemyCombatUnit = givenTile.getCombatUnitInTile();
		NonCombatUnit enemyNonCombatUnit = givenTile.getNonCombatUnitInTile();
		if(enemyCombatUnit == null && enemyNonCombatUnit == null)
			return mainCommands.invalidCommand.regex;

		// kill enemy unit
		if(enemyCombatUnit != null)
			enemyCombatUnit.getRulerPlayer().removeUnit(enemyCombatUnit);
		if(enemyNonCombatUnit != null)
			enemyNonCombatUnit.getRulerPlayer().removeUnit(enemyNonCombatUnit);

		return cheatCode.unitKilled.regex;
	}
	public String gainBonusResourceCheat()
	{
		for(int i = 0; i < 5; i++)
			playerTurn.addResource(new BonusResource(ResourceType.values()[i + 1]));

		return "bonus resources added successfully";
	}
	public String gainStrategicResourceCheat()
	{
		for(int i = 0; i < 3; i++)
			playerTurn.addResource(new StrategicResource(ResourceType.values()[i + 6]));

		return "strategic resources added successfully";
	}
	public String gainLuxuryResourceCheat()
	{
		for(int i = 0; i < 11; i++)
			playerTurn.addResource(new LuxuryResource(ResourceType.values()[i + 9]));

		return "luxury resources added successfully";
	}

	public int getYear() {
		return yearCounter;
	}
	public void setYear(int yearCounter) {
		this.yearCounter = yearCounter;
	}
	public static String enterMenu(Scanner scanner, Matcher matcher)
	{
		String menuName = matcher.group("menuName");

		if(mainCommands.compareRegex(menuName, mainCommands.profileName) != null)
			return mainCommands.navigationError.regex;
		else if(mainCommands.compareRegex(menuName, mainCommands.loginMenu) != null)
			return mainCommands.navigationError.regex;
		else if(mainCommands.compareRegex(menuName, mainCommands.mainMenu) != null)
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
		switch(number % 10)
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
		for(Player value : players)
		{
			if(value.getCivilization() == n)
				return true;
		}
		return false;
	}
	public int getNum(Scanner scanner, int min, int max) //TODO: should be deleted
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

	// this method is called every turn to update the technologyCounter of the player
	public String processResearchingTechnology()
	{
		Technology researchingTechnology = playerTurn.getResearchingTechnology();
		if(researchingTechnology == null)
			return null;

		int technologyIndex = -1;
		for(int i = 0; i < Technology.values().length; i++)
			if(researchingTechnology.equals(Technology.values()[i]))
			{
				technologyIndex = i;
				break;
			}
		if(technologyIndex == -1)
		{
			System.err.println("technologyIndex is -1 :(");
			System.exit(1);
		}

		playerTurn.getResearchingTechCounter()[technologyIndex]++;
		//TODO: probably should change how many turns it takes to get the researchingTechnology
		if(playerTurn.getResearchingTechCounter()[technologyIndex] >= researchingTechnology.cost / 10)
		{
			playerTurn.addTechnology(researchingTechnology);
			new Notification(playerTurn, turnCounter, "you got " + researchingTechnology.name());
			playerTurn.setResearchingTechnology(null);
			return infoCommands.successGainTech.regex + researchingTechnology.name();
		}
		//TODO: maybe change to "return "";"
		return null;
	}
	//DOC commands
	public BuildingType requiredTechForBuilding(Technology technology)
	{
		for(int i = 0; i < BuildingType.values().length; i++)
			if(BuildingType.values()[i].requiredTechnology == technology)
				return BuildingType.values()[i];
		return null;
	}
	public Improvement requiredTechForImprovement(Technology technology)
	{
		for(int i = 0; i < Improvement.values().length; i++)
			if(Improvement.values()[i].requiredTechnology == technology)
				return Improvement.values()[i];
		return null;
	}
	public String showResearch()
	{
		int flg = -1;
		for(int i = 0; i < Technology.values().length; i++)
			if(Technology.values()[i] == playerTurn.getResearchingTechnology())
				flg = i;
		if(playerTurn.getResearchingTechnology() != null)
		{
			BuildingType requiredBuilding = requiredTechForBuilding(playerTurn.getResearchingTechnology());
			Improvement requiredImprovement = requiredTechForImprovement(playerTurn.getResearchingTechnology());
			if(requiredBuilding != null && requiredImprovement != null)
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost / 10 - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ requiredBuilding.name() + " and " + requiredImprovement.name() + infoCommands.gainAfterGetTechnology.regex;
			else if(requiredBuilding != null)
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost / 10 - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ requiredBuilding.name() + infoCommands.gainAfterGetTechnology.regex;
			else if(requiredImprovement != null)
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost / 10 - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ requiredImprovement.name() + infoCommands.gainAfterGetTechnology.regex;
			else
				return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + playerTurn.getResearchingTechnology().toString().toLowerCase(Locale.ROOT) + "\n" +
						infoCommands.remainingTurns.regex + (playerTurn.getResearchingTechnology().cost / 10 - playerTurn.getResearchingTechCounter()[flg]) + "\n"
						+ infoCommands.notGain.regex;
		}
		return infoCommands.researchInfo.regex + infoCommands.currentResearching.regex + infoCommands.nothing.regex;
	}

	public String selectCUnit(String command)
	{
		int x, y;
		try{
			String[] coordinates = command.split(",");
			coordinates[0] = coordinates[0].trim();
			coordinates[1] = coordinates[1].trim();
			x = Integer.parseInt(coordinates[0]);
			y= Integer.parseInt(coordinates[1]);
		}catch (Exception e){
			return selectCommands.invalidCommand.regex;
		}
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
		return selectCommands.coordinatesDoesntExistCUnit.regex + x + selectCommands.and.regex + y;
	}
	public String selectNUnit(String command)
	{
		int x, y;
		try{
			String[] coordinates = command.split(",");
			coordinates[0] = coordinates[0].trim();
			coordinates[1] = coordinates[1].trim();
			x = Integer.parseInt(coordinates[0]);
			y= Integer.parseInt(coordinates[1]);
		}catch (Exception e){
			return selectCommands.invalidCommand.regex;
		}
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
		return selectCommands.coordinatesDoesntExistNUnit.regex + x + selectCommands.and.regex + y;
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
				return selectCommands.coordinatesDoesntExistCity.regex + x + selectCommands.and.regex + y;
			}
		}
		return selectCommands.invalidCommand.regex;
	}
	public void updatePlayersUnitLocations()
	{
		for(Unit unit : this.getPlayerTurn().getUnits())
		{
			unit.setMovementPoints(unit.getMP());
			if( unit.getMoves() != null && unit.getMoves().size() >= 0)
			{
				//unit.move(getTileByXY(unit.getMoves().get(0).X, unit.getMoves().get(0).Y));
				unit.updateUnitMovements();
			}
		}
	}
	public void updateWorkersConstructions()
	{
		for(Unit unit : this.getPlayerTurn().getUnits())
			if(unit instanceof Worker)
			{
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
	public void updateCityConstructions(){
		for (City city : playerTurn.getCities()) {
			if(city.getCurrentConstruction() != null)
				city.construct(city.getCurrentConstruction(), this);
		}
	}
	public void handleUnitCommands()
	{
		for(Unit unit : playerTurn.getUnits())
		{
			if(unit.getCommands().size() > 0)
			{
				UnitCommandsHandler.handleCommands(unit, unit.getCommands().get(0));
				if(!unit.getCommands().get(0).equals(UnitCommands.REPAIR_TILE) &&
						!unit.getCommands().get(0).equals(UnitCommands.MOVE) && !unit.getCommands().get(0).equals(UnitCommands.BUILD_FARM) &&
						!unit.getCommands().get(0).equals(UnitCommands.BUILD_MINE))
					unit.getCommands().remove(0);
			}
		}
	}
	public String moveUnit(Matcher matcher)
	{
		if (matcher == null)
			return unitCommands.wrongCoordinates.regex;
		int newX = Integer.parseInt(matcher.group("x"));
		int newY = Integer.parseInt(matcher.group("y"));
		if(newX < 0 || newX > MAP_SIZE - 1 || newY < 0 || newY > MAP_SIZE - 1)
			return unitCommands.wrongCoordinates.regex;
		if(playerTurn.getMap().get(getTileByXY(newX,newY)).equals(TileState.FOG_OF_WAR)) return "tile is in fog of war";
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else
			{
				int x = playerTurn.getSelectedUnit().getTile().getPosition().X;
				int y = playerTurn.getSelectedUnit().getTile().getPosition().Y;
				Tile originTile = getTileByXY(x, y);
				Tile destinationTile = getTileByXY(newX, newY);
				Unit unitToMove = playerTurn.getSelectedUnit();

				// move unit
				return unitToMove.move(destinationTile);
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
				//TODO: this should be checked. probably a bug
				if(playerTurn.getSelectedUnit().getUnitState().equals(UnitState.SLEEPING))
					return gameEnum.isSleep.regex;
				else
				{
					changePower(playerTurn.getSelectedUnit());
					playerTurn.getSelectedUnit().setUnitState(UnitState.SLEEPING);
					return gameEnum.slept.regex;
				}
			}
		}
		return gameEnum.nonSelect.regex;
	}
	private void changePower(Unit unit)
	{
		if(unit.getUnitState().equals(UnitState.FORTIFIED))
		{
			if(unit.getClass().equals(MidRange.class))
				unit.setPower(((MidRange) unit).getType().combatStrength);
			if(unit.getClass().equals(LongRange.class))
				unit.setPower(((LongRange) unit).getType().combatStrength);
		}
	}
	public String stayAlert()
	{
		for(Player player : players)
			for(Unit unit : player.getUnits())
			{
				if(!unit.getUnitState().equals(UnitState.ALERT))
					return unitCommands.setAlert.regex;
				for(Tile adjacentTile : playerTurn.getAdjacentTiles(unit.getTile(), 2))
				{
					CombatUnit combatUnitInAdjacentTile = adjacentTile.getCombatUnitInTile();
					NonCombatUnit nonCombatUnitInAdjacentTile = adjacentTile.getNonCombatUnitInTile();
					if((combatUnitInAdjacentTile != null && combatUnitInAdjacentTile.getRulerPlayer() != playerTurn) ||
							(nonCombatUnitInAdjacentTile != null && nonCombatUnitInAdjacentTile.getRulerPlayer() != playerTurn)) {
						unit.setUnitState(UnitState.ACTIVE);
						return unitCommands.activeUnit.regex;
					}
				}
			}
		return null;
	}
	public String alert()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			//TODO: probably should be deleted. selected unit only should be yours, not other players
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			else
			{
				changePower(playerTurn.getSelectedUnit());
				playerTurn.getSelectedUnit().setUnitState(UnitState.ALERT);
				return unitCommands.alerted.regex;
			}
		}
		return gameEnum.nonSelect.regex;
	}
	public void updateFortifyTilHeal()
	{
		//TODO: this method should be called every turn it gets to the first player
		for(Player player : players)
			for(Unit unit : player.getUnits())
				if(unit.getUnitState().equals(UnitState.FORTIFIED_FOR_HEALING) && unit.getHealth() < unit.MAX_HEALTH)
				{
					if(belongToPlayerTurn(unit.getTile()))
						unit.setHealth(unit.getHealth() + 3);
					else
						unit.setHealth(unit.getHealth() + 1);
					//TODO: different amounts for other tiles
				}
	}
	public String fortify()
	{
		Unit tmp = playerTurn.getSelectedUnit();
		if(tmp != null)
		{
			//TODO: probably should be deleted. selected unit only should be yours, not other players
			if(!playerTurn.getUnits().contains(tmp))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			else
			{
				if(tmp.getUnitState().equals(UnitState.FORTIFIED))
					return gameEnum.isFortify.regex;
				tmp.setUnitState(UnitState.FORTIFIED);
				tmp.setPower((int) (tmp.getPower() * 1.5));
				return unitCommands.fortifyActivated.regex;
			}
		}
		return gameEnum.nonSelect.regex;
	}
	public String fortifyTilHeal()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			// TODO: probably should be deleted. selected unit only should be yours, not other players
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			else
			{
				changePower(playerTurn.getSelectedUnit());
				playerTurn.getSelectedUnit().setUnitState(UnitState.FORTIFIED_FOR_HEALING);
				return unitCommands.fortifyHealActivated.regex;
			}
		}
		return gameEnum.nonSelect.regex;
	}
	//TODO: probably should be deleted. because whenever a combatUnit enters a city, it becomes a garrison for that city
	public String garrison()
	{ //TODO: check for bugs
		if(playerTurn.getSelectedUnit() != null)
		{
			//TODO: probably should be deleted. selected unit only should be yours, not other players
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			City unitCity = playerTurn.getTileCity(playerTurn.getSelectedUnit().getTile());
			if(unitCity == null)
				return unitCommands.isNotInCity.regex;
			else if(unitCity.getGarrison() != null)
				return unitCommands.hasGarrison.regex;
			else
			{
				changePower(playerTurn.getSelectedUnit());
				unitCity.setGarrison((CombatUnit) playerTurn.getSelectedUnit());
				playerTurn.getSelectedUnit().setUnitState(UnitState.ACTIVE);
				return unitCommands.garissonSet.regex;
			}
		}
		return gameEnum.nonSelect.regex;
	}
	private boolean isSiege(LongRange unit)
	{
		if(unit.getType().equals(LongRangeType.ARTILLERY))
			return true;
		if(unit.getType().equals(LongRangeType.CANON))
			return true;
		if(unit.getType().equals(LongRangeType.TREBUCHET))
			return true;
		if(unit.getType().equals(LongRangeType.CATAPULT))
			return true;
		return false;
	}
	public void checkSetupAttack()
	{
		for(Unit unit : playerTurn.getUnits())
		{
			if(unit.getClass().equals(LongRange.class) && unit.getUnitState().equals(UnitState.IS_SET)
					&& ((LongRange) unit).getSetCounter() == 1 && ((LongRange) unit).getTargetCity() != null)
			{
				((LongRange) unit).setSet(0);
				String tmp = ((CombatUnit) unit).attackToCity(((LongRange) unit).getTargetCity(), this);
				((LongRange) unit).setTargetCity(null);
				((LongRange) unit).setUnitState(UnitState.ACTIVE);
			}
			else if(unit.getClass().equals(LongRange.class) && unit.getUnitState().equals(UnitState.IS_SET)
					&& ((LongRange) unit).getSetCounter() == 0)
				((LongRange) unit).setSet(1);
		}
	}
	public String setup(int x, int y)
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(isValidCoordinate(x,y) == null)
				return unitCommands.rangeError.regex;
			else if(!playerTurn.getSelectedUnit().getClass().equals(LongRange.class))
				return unitCommands.isNotLongRange.regex;
			else if(!isSiege(((LongRange) playerTurn.getSelectedUnit())))
				return unitCommands.isNotSiege.regex;
			else if(belongToCity(isValidCoordinate(x,y)) == null)
				return unitCommands.notCityInDestination.regex;
			else if(belongToPlayerTurn(isValidCoordinate(x,y)))
				return unitCommands.playerTurnCity.regex;
			else
			{
				Tile tile = getTileByXY(x, y);
				LongRange tmp = ((LongRange) playerTurn.getSelectedUnit());
				tmp.setUnitState(UnitState.IS_SET);
				tmp.setTargetCity(belongToCity(tile));
				changePower(playerTurn.getSelectedUnit());
				return unitCommands.setupSuccessful.regex;
			}
		}
		return gameEnum.nonSelect.regex;
	}
	private boolean belongToPlayerTurn(Tile tile)
	{
		for(City city : playerTurn.getCities())
			if(city.getTerritory().contains(tile))
				return true;
		return false;
	}
	private City isCityInTile(Tile tile)
	{
		for(Player player : players)
			for(City city : player.getCities())
				if(city.getCapitalTile() == tile)
					return city;
		return null;
	}
	public City belongToCity(Tile tile)
	{
		for(Player player : players)
			for(City city : player.getCities())
				for(Tile tmp : city.getTerritory())
					if(tmp == tile)
						return city;
		return null;
	}
	public String pillage()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			else if(belongToPlayerTurn(playerTurn.getSelectedUnit().getTile()))
				return unitCommands.playerTurnCity.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.NONE))
				return unitCommands.nothingInTile.regex;
			else
			{
				changePower(playerTurn.getSelectedUnit());
				playerTurn.getSelectedUnit().getTile().setImprovement(Improvement.NONE);
				playerTurn.getSelectedUnit().getTile().setIsPillaged(true);
				return unitCommands.destroyImprovement.regex;
			}
		}
		return gameEnum.nonSelect.regex;

	}
	public String destroyCity(City city)
	{
		city.destroyCity();
		playerTurn.setHappiness((int) (playerTurn.getHappiness() * 1.1));
		return unitCommands.destroyCity.regex;
	}

	public City getCityByName(String name){
		for (Player player : GameController.getInstance().getPlayers()) {
			for (City city : player.getCities()) {
				if(city.getName().equals(name))
					return city;
			}
		}
		return null;
	}
	public String attachCity(City city)
	{
		city.attachCity();
		playerTurn.setHappiness((int) (playerTurn.getHappiness() * 0.95));
		return unitCommands.attachCity.regex;
	}
	public String attackCity(int x, int y)
	{
		if(isValidCoordinate(x, y) == null)
			return unitCommands.wrongCoordinates.regex;
		Tile tile = getTileByXY(x, y);
		if(playerTurn.getSelectedUnit() != null)
		{
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else if(playerTurn.getSelectedUnit().getClass().getSuperclass().getSimpleName().equals("NonCombatUnit"))
				return unitCommands.isNotCombat.regex;
			else if(belongToPlayerTurn(tile))
				return unitCommands.playerTurnCity.regex;
			else if(playerTurn.getSelectedUnit().getClass().equals(MidRange.class) &&
					playerTurn.getSelectedUnit().getTile().distanceTo(tile) > 1)
				return unitCommands.rangeError.regex;
			else if(playerTurn.getSelectedUnit().getClass().equals(LongRange.class) &&
					playerTurn.getSelectedUnit().getTile().distanceTo(tile) > ((LongRange) playerTurn.getSelectedUnit()).getType().getRange())
				return unitCommands.rangeError.regex;
			else if(isCityInTile(tile) == null)
				return unitCommands.notCityInDestination.regex;
			else
			{
				changePower(playerTurn.getSelectedUnit());
				return ((CombatUnit) playerTurn.getSelectedUnit()).attackToCity(isCityInTile(tile), this);
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
			for(City city : player.getCities()) {
				for(Tile tile1 : city.getTerritory())
					if (tile1.getPosition().X == x &&
							tile1.getPosition().Y == y)
						return true;
			}
		return false;
	}
	private boolean hasCity(Tile tile)
	{
		int x = tile.getPosition().X;
		int y = tile.getPosition().Y;
		for(Player player : players)
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
				playerTurn.setGameScore(playerTurn.getGameScore() + 5 * MAP_SIZE);
				((Settler) playerTurn.getSelectedUnit()).createCity();
				if(playerTurn.getCities().size() != 1)
					playerTurn.setHappiness((int) (playerTurn.getHappiness() * 0.95));
				City tmp = playerTurn.getCities().get(playerTurn.getCities().size() - 1);
				new Notification(playerTurn, turnCounter, "city " + tmp.getName() + " built");
				tmp.addCitizen(new Citizen(tmp));
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
				changePower(playerTurn.getSelectedUnit());
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
			// TODO: this should be deleted. only your units can be selected
			if(!playerTurn.getUnits().contains(playerTurn.getSelectedUnit()))
				return unitCommands.notYours.regex;
			else
			{
				if(!playerTurn.getSelectedUnit().getUnitState().equals(UnitState.SLEEPING))
					return gameEnum.awaken.regex;
				else
				{
					changePower(playerTurn.getSelectedUnit());
					playerTurn.getSelectedUnit().setUnitState(UnitState.ACTIVE);
					return gameEnum.wokeUp.regex;
				}
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public ArrayList<CombatUnit> getTileCUnits(Tile tile){
		ArrayList<CombatUnit> answer = new ArrayList<>();
		for (Player player : players)
			for (Unit unit : player.getUnits())
				if(unit instanceof CombatUnit && unit.getTile().getPosition().X == tile.getPosition().X &&
						unit.getTile().getPosition().Y == tile.getPosition().Y)
					answer.add((CombatUnit) unit);
		return answer;
	}
	public ArrayList<NonCombatUnit> getTileNCUnits(Tile tile){
		ArrayList<NonCombatUnit> answer = new ArrayList<>();
		for (Player player : players) {
			for (Unit unit : player.getUnits()) {
				if(unit instanceof NonCombatUnit && unit.getTile().getPosition().equals(tile.getPosition())){
					answer.add((NonCombatUnit) unit);
				}
			}
		}
		return answer;
	}

	private int getGoldOfUnit(Unit unit)
	{
		int number = 0;
		if(unit.getClass().equals(Settler.class))
			number = 89;
		if(unit.getClass().equals(Worker.class))
			number = 40;
		if(unit.getClass().equals(LongRange.class))
			number = ((LongRange) unit).getType().getCost();
		if(unit.getClass().equals(MidRange.class))
			number = ((MidRange) unit).getType().getCost();
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
				playerTurn.getSelectedUnit().destroy();
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
			else if(!playerTurn.getSelectedUnit().getRulerPlayer().getTechnologies().contains(Technology.THE_WHEEL)) return "do not have Wheel technology";
			else
			{
				((Worker) playerTurn.getSelectedUnit()).buildRoad();
				return unitCommands.roadBuilt.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String railRoad()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(playerTurn.getSelectedUnit().getTile().hasRailRoad())
				return unitCommands.hasRailRoad.regex;
			else if(!playerTurn.getSelectedUnit().getRulerPlayer().getTechnologies().contains(Technology.THE_WHEEL)) return "do not have Wheel technology";
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
			else if(!playerTurn.getTechnologies().contains(Improvement.FARM.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.FARM))
				return unitCommands.hasFarm.regex;
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildFarm()) != null)
					return tmp;
				return unitCommands.farmBuild.regex;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String mine()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(!playerTurn.getTechnologies().contains(Improvement.MINE.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.MINE))
				return unitCommands.hasMine.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildMine()) != null)
					return tmp;
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
			else if(!playerTurn.getTechnologies().contains(Improvement.TRADING_POST.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.TRADING_POST))
				return unitCommands.hasTradingPost.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildTradingPost()) != null)
					return tmp;
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
			else if(!playerTurn.getTechnologies().contains(Improvement.LUMBER_MILL.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.LUMBER_MILL))
				return unitCommands.hasLumberMill.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildLumberMill()) != null)
					return tmp;
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
			else if(!playerTurn.getTechnologies().contains(Improvement.PASTURE.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.PASTURE))
				return unitCommands.hasPasture.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildPasture()) != null)
					return tmp;
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
			else if(!playerTurn.getTechnologies().contains(Improvement.CAMP.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.CAMP))
				return unitCommands.hasCamp.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildCamp()) != null)
					return tmp;
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
			else if(!playerTurn.getTechnologies().contains(Improvement.PLANTATION.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.PLANTATION))
				return unitCommands.hasPlantation.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildPlantation()) != null)
					return tmp;
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
			else if(!playerTurn.getTechnologies().contains(Improvement.QUARRY.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.QUARRY))
				return unitCommands.hasQuarry.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildQuarry()) != null)
					return tmp;
				return "quarry built successfully";
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String factory()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(!playerTurn.getTechnologies().contains(Improvement.FACTORY.requiredTechnology))
				return unitCommands.dontGainRequiredTech.regex;
			else if(playerTurn.getSelectedUnit().getTile().getImprovement().equals(Improvement.FACTORY))
				return unitCommands.hasFactory.regex;
				//TODO: cant build(reasons)
			else
			{
				String tmp;
				if((tmp = ((Worker) playerTurn.getSelectedUnit()).buildFactory()) != null)
					return tmp;
				return "factory built successfully";
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String removeFeature()
	{
		if(playerTurn.getSelectedUnit() != null)
		{
			if(buildErrors() != null)
				return buildErrors();
			else if(!playerTurn.getSelectedUnit().getTile().getTileFeature().equals(TileFeature.JUNGLE) &&
					!playerTurn.getSelectedUnit().getTile().getTileFeature().equals(TileFeature.FOREST) &&
					!playerTurn.getSelectedUnit().getTile().getTileFeature().equals(TileFeature.MARSH))
				return "tile does not have removable feature";
			else
			{
				switch (playerTurn.getSelectedUnit().getTile().getTileFeature()){
					case MARSH -> ((Worker) playerTurn.getSelectedUnit()).removeMarsh();
					case JUNGLE -> ((Worker) playerTurn.getSelectedUnit()).removeJungle();
					case FOREST -> ((Worker) playerTurn.getSelectedUnit()).removeForest();
				}
				return "feature removed successfully";
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
	public Tile isValidCoordinate(int x, int y)
	{
		if(x > 9 || x < 0 || y > 9 || y < 0)
			return null;
		return getTileByXY(x, y);
	}
	private boolean hasBuilding(Tile tile)
	{
		for(Player player : players)
			for(City city : player.getCities())
				for(Building building : city.getBuildings())
					if(tile == building.getTile())
						return true;
		return false;
	}
	public String buildUnit(String type)
	{
		if(playerTurn.getSelectedCity() != null)
		{
			if(!playerTurn.getCities().contains(playerTurn.getSelectedCity()))
				return unitCommands.notYours.regex;
			else
			{
				if(containTypeMid(type) != null)
				{
					if (playerTurn.getGold() < MidRangeType.valueOf(type).getCost())
						return gameEnum.notEnoughGold.regex;
					else if(playerTurn.getSelectedCity().findTileWithNoCUnit() == null)
						return gameEnum.noEmptyTile.regex;
					return playerTurn.getSelectedCity().construct(new MidRange(containTypeMid(type)), this);
				}
				else if(containTypeLong(type) != null)
				{
					if (playerTurn.getGold() < LongRangeType.valueOf(type).getCost())
						return gameEnum.notEnoughGold.regex;
					else if(playerTurn.getSelectedCity().findTileWithNoCUnit() == null)
						return gameEnum.noEmptyTile.regex;
					return playerTurn.getSelectedCity().construct(new LongRange(containTypeLong(type)), this);
				}
				else if(type.equals("SETTLER"))
				{
					if(playerTurn.getGold() < 89)
						return gameEnum.notEnoughGold.regex;
					else if(playerTurn.getSelectedCity().findTileWithNoNCUnit() == null)
						return gameEnum.noEmptyTile.regex;
					return playerTurn.getSelectedCity().construct(new Settler(), this);
				}
				else if(type.equals("WORKER"))
				{
					if(playerTurn.getGold() < 70)
						return gameEnum.notEnoughGold.regex;
					else if(playerTurn.getSelectedCity().findTileWithNoNCUnit() == null)
						return gameEnum.noEmptyTile.regex;
					return playerTurn.getSelectedCity().construct(new Worker(), this);
				}
				return null;
			}
		}
		else
			return gameEnum.nonSelect.regex;
	}
	public String buildBuilding(BuildingType buildingType) {
		if(playerTurn.getSelectedCity() != null)
		{
			Tile destination = playerTurn.getSelectedCity().getTileWithNoBuilding();
			if(destination == null)
				return "no tile without building";
			if(!playerTurn.getCities().contains(playerTurn.getSelectedCity()))
				return unitCommands.notYours.regex;
			else
			{
				if (playerTurn.getGold() < buildingType.cost)
					return gameEnum.notEnoughGold.regex;
				else if(playerTurn.getSelectedCity().doesCityHaveBuilding(buildingType))
					return "cannot build this type of building here";
				return playerTurn.getSelectedCity().construct(new Building(buildingType,destination), this);
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
	public void setFirstHappiness()
	{
		for(Player player : players)
			player.setHappiness(100 - players.size() * 5);
	}

	public String buyTile(String command)
	{
		int x, y;
		try{
			String[] coordinates = command.split(",");
			coordinates[0] = coordinates[0].trim();
			coordinates[1] = coordinates[1].trim();
			x = Integer.parseInt(coordinates[0]);
			y= Integer.parseInt(coordinates[1]);
		}catch (Exception e){
			return selectCommands.invalidRange.regex;
		}
		if(isValidCoordinate(x,y) == null)
			return unitCommands.wrongCoordinates.regex;
		Tile tile = getTileByXY(x, y);
		if(playerTurn.getSelectedCity() != null)
			return playerTurn.getSelectedCity().purchaseTile(tile);
		return gameEnum.nonSelect.regex;
	}
	public MidRangeType containTypeMid(String type)
	{
		for(MidRangeType midRangeType : MidRangeType.values())
			if(midRangeType.name().equals(type))
				return midRangeType;
		return null;
	}
	public BuildingType containTypeBuilding(String type)
	{
		for(BuildingType buildingType : BuildingType.values())
			if(buildingType.name().equals(type))
				return buildingType;
		return null;
	}
	public LongRangeType containTypeLong(String type)
	{
		for(LongRangeType longRangeType : LongRangeType.values())
			if(longRangeType.name().equals(type))
				return longRangeType;
		return null;
	}
	public String buyBuilding(String type) {
		if (containTypeBuilding(type) == null)
			return gameEnum.invalidCommand.regex;
		BuildingType buildingType = containTypeBuilding(type);
		if (!(buildingRequiredTech(buildingType) && buildingRequiredBuilding(buildingType)))
			return mainCommands.requiredTechAndBuild.regex;
		if(playerTurn.getGold() < buildingType.cost)
			return gameEnum.notEnoughGold.regex;
		else if(playerTurn.getSelectedCity().doesCityHaveBuilding(buildingType))
			return "cannot buy this type of building";
		Tile destination = playerTurn.getSelectedCity().getTileWithNoBuilding();
		if(destination == null) return "no tile without building";
		return playerTurn.getSelectedCity().buyBuilding(new Building(buildingType,
				destination));
	}

	public String buyUnit(String type)
	{
		if(containTypeMid(type) != null)
		{
			if(!validMidRange(containTypeMid(type)))
				return mainCommands.requiredTech.regex;
			else if(playerTurn.getGold() < MidRangeType.valueOf(type).getCost())
				return gameEnum.notEnoughGold.regex;
			else if(playerTurn.getSelectedCity().findTileWithNoCUnit() == null)
				return gameEnum.noEmptyTile.regex;
			return playerTurn.getSelectedCity().buyUnit(new MidRange(playerTurn, MidRangeType.valueOf(type), playerTurn.getSelectedCity().findTileWithNoCUnit()));
		}
		else if(containTypeLong(type) != null)
		{
			if(!validLongRange(containTypeLong(type)))
				return mainCommands.requiredTech.regex;
			if(playerTurn.getGold() < LongRangeType.valueOf(type).getCost())
				return gameEnum.notEnoughGold.regex;
			else if(playerTurn.getSelectedCity().findTileWithNoCUnit() == null)
				return gameEnum.noEmptyTile.regex;
			return playerTurn.getSelectedCity().buyUnit(new LongRange(playerTurn, LongRangeType.valueOf(type), playerTurn.getSelectedCity().findTileWithNoCUnit()));
		}
		else if(type.equals("SETTLER"))
		{
			if(playerTurn.getGold() < 89)
				return gameEnum.notEnoughGold.regex;
			else if(playerTurn.getSelectedCity().findTileWithNoNCUnit() == null)
				return gameEnum.noEmptyTile.regex;
			return playerTurn.getSelectedCity().buyUnit(new Settler(playerTurn, playerTurn.getSelectedCity().findTileWithNoNCUnit()));
		}
		else if(type.equals("WORKER"))
		{
			if(playerTurn.getGold() < 70)
				return gameEnum.notEnoughGold.regex;
			else if(playerTurn.getSelectedCity().findTileWithNoNCUnit() == null)
				return gameEnum.noEmptyTile.regex;
			return playerTurn.getSelectedCity().buyUnit(new Worker(playerTurn, playerTurn.getSelectedCity().findTileWithNoNCUnit()));
		}
		return gameEnum.wrongName.regex;
	}
	private boolean buildingRequiredBuilding(BuildingType type)
	{
		if (type.requiredBuilding == null)
			return true;
		for (Building building : playerTurn.getSelectedCity().getBuildings())
			if (building.getBuildingType() == type)
				return true;
		return false;
	}
	private boolean buildingRequiredTech(BuildingType type)
	{
		return (type.requiredTechnology == null ||
				(type.requiredTechnology != null && playerTurn.getTechnologies().contains(type.requiredTechnology)));
	}
	public boolean validMidRange(MidRangeType type)
	{
		return (type.getRequiredTech() == null ||
				(type.getRequiredTech() != null && playerTurn.getTechnologies().contains(type.getRequiredTech()))) &&
				(type.getRequiredSource() == null ||
						(type.getRequiredSource() != null && playerTurn.getResources().contains(type.getRequiredSource())));
	}
	public boolean validLongRange(LongRangeType type)
	{
		return (type.getRequiredTech() == null ||
				(type.getRequiredTech() != null && playerTurn.getTechnologies().contains(type.getRequiredTech()))) &&
				(type.getRequiredSource() == null ||
						(type.getRequiredSource() != null && playerTurn.getResources().contains(type.getRequiredSource())));
	}

	public Citizen isUnemployed(City city)
	{
		for(Citizen citizen : city.getCitizens())
			if(citizen.getWorkingTile() == null)
				return citizen;
		return null;
	}
	public String lockCitizenToTile(String command)
	{
		int x, y;
		try{
			String[] coordinates = command.split(",");
			coordinates[0] = coordinates[0].trim();
			coordinates[1] = coordinates[1].trim();
			x = Integer.parseInt(coordinates[0]);
			y= Integer.parseInt(coordinates[1]);
		}catch (Exception e){
			return selectCommands.invalidRange.regex;
		}
		if(isValidCoordinate(x,y) == null)
			return unitCommands.wrongCoordinates.regex;
		Tile tile = getTileByXY(x, y);
		if(playerTurn.getSelectedCity() != null)
		{
			if(isUnemployed(playerTurn.getSelectedCity()) == null)
				return gameEnum.noUnemployed.regex;
			else
				return Objects.requireNonNull(isUnemployed(playerTurn.getSelectedCity())).setCitizenOnTile(tile);
		}
		return gameEnum.invalidCoordinate.regex;
	}
	private Citizen hasCitizenOnTile(Tile tile)
	{
		for(Citizen citizen : playerTurn.getSelectedCity().getCitizens())
			if(citizen.getWorkingTile() == tile)
				return citizen;
		return null;
	}
	public String unLockCitizenToTile(String command)
	{
		int x, y;
		try{
			String[] coordinates = command.split(",");
			coordinates[0] = coordinates[0].trim();
			coordinates[1] = coordinates[1].trim();
			x = Integer.parseInt(coordinates[0]);
			y= Integer.parseInt(coordinates[1]);
		}catch (Exception e){
			return selectCommands.invalidRange.regex;
		}
		if(isValidCoordinate(x,y) == null)
			return unitCommands.wrongCoordinates.regex;
		Tile tile = getTileByXY(x, y);
		if(playerTurn.getSelectedCity() != null)
		{
			if(hasCitizenOnTile(tile) == null)
				return gameEnum.noCitizenHere.regex;
			else
			{
				Objects.requireNonNull(hasCitizenOnTile(tile)).unEmployCitizen();
				return gameEnum.removeFromWork.regex;
			}
		}
		return gameEnum.nonSelect.regex;
	}
	private BuildingType[] accessibleBuildings(Player playerTurn){
		BuildingType[] buildingTypes = BuildingType.values();
		for (BuildingType buildingType : buildingTypes) {
			if(!playerTurn.getTechnologies().contains(buildingType.requiredTechnology) || playerHasBuilding(playerTurn, buildingType)){

			}
		}
		return null;
	}

	private boolean playerHasBuilding(Player playerTurn, BuildingType buildingType) {
		return false;
	}

	public Player isGameEnd() {
		//year 2050 and return winner
		if (yearCounter >= 2050) {
			int max = 0;
			for (Player player : players)
				if (player.getGameScore() > max)
					max = player.getGameScore();
			for (Player player : players)
				if (player.getGameScore() == max)
					return player;
		}

		//players size == 1
		if (players.size() == 1)
			return players.get(0);

		return null;
	}
}