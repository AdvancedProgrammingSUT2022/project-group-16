package Models.Player;

import Controllers.GameController;
import Models.City.*;
import Models.Resources.TradeRequest;
import Models.Terrain.Position;
import Models.Resources.LuxuryResource;
import Models.Resources.Resource;
import Models.Resources.ResourceType;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.CombatUnits.LongRange;
import Models.Units.CombatUnits.MidRange;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.Unit;
import Models.User;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.IOException;
import java.util.*;

public class Player extends User
{
	transient GameController gameController;
	private boolean isYourTurn = false;
	private Unit selectedUnit = null;
	private City selectedCity = null;
	private final Civilization civilization;
	private int food = 100;
	private int cup = 0;
	private int gold = 100;
	private int happiness;
	private int XP = 0;
	private int population = 0;
	private int maxPopulation = 0;
	private int gameScore = 0;
	private final ArrayList<Technology> technologies = new ArrayList<>();
	private int[] researchingTechCounter = new int[50];
	private Technology researchingTechnology;
	private ArrayList<Resource> resources = new ArrayList<>();
	private final ArrayList<ResourceType> acquiredLuxuryResources = new ArrayList<>(); // this is for checking to increase happiness when acquiring luxury resources
	private final ArrayList<Improvement> improvements = new ArrayList<>();
	public int MAP_SIZE;
	transient private HashMap<Tile, TileState> map;
	public ArrayList<Tile> mapKeyset = new ArrayList<>();
	public ArrayList<TileState> mapValueset = new ArrayList<>();
	private ArrayList<TradeRequest> tradeRequests = new ArrayList<>();
	private boolean hasCity = false;
	private ArrayList<City> cities = new ArrayList<>();
	private final ArrayList<City> seizedCities = new ArrayList<>();//remember to check if the city is destroyed or not by its state
	private City initialCapitalCity;    //??TODO
	private City currentCapitalCity;    //??TODO
	private final ArrayList<Notification> notifications = new ArrayList<>();
	private final ArrayList<Unit> units = new ArrayList<>();
	public ArrayList<Unit> enemyUnits = new ArrayList<>();
	private int tilePurchaseCost = 10; //increases every time the player purchases a tile
	private boolean isUnHappy = false;
	private HashMap<Civilization, RelationState> relationStates = new HashMap<>();

	public Player(Civilization civilization, String username, String nickname, String password, int score) {
		super(username, nickname, password, null);
		this.civilization = civilization;
		this.happiness = 100;
		gameController = GameController.getInstance();
		this.map = new HashMap<>();
		this.setScore(score);
	}

	public int getGameScore() {
		return gameScore;
	}

	public void setGameScore(int score) {
		this.gameScore = score;
	}

	public boolean getIsYourTurn()
	{
		return isYourTurn;
	}
	public void setIsYourTurn(boolean isYourTurn)
	{
		this.isYourTurn = isYourTurn;
	}
	public HashMap<Civilization, RelationState> getRelationStates() {
		return relationStates;
	}

	public void addRelationStates(Civilization civilization, RelationState relationState) {
		this.relationStates.put(civilization,relationState);
	}

	public void declareWar(Player player){
		this.relationStates.replace(player.getCivilization(), RelationState.ENEMY);
		player.getRelationStates().replace(this.civilization, RelationState.ENEMY);
	}
	public void declareFriendship(Player player){
		this.relationStates.replace(player.getCivilization(), RelationState.FRIEND);
		player.getRelationStates().replace(this.civilization, RelationState.FRIEND);
	}

	public boolean isHasCity() {
		return hasCity;
	}

	public void setHasCity(boolean hasCity) {
		this.hasCity = hasCity;
	}

	public City getSelectedCity()
	{
		return selectedCity;
	}
	public Unit getSelectedUnit()
	{
		return selectedUnit;
	}
	public void setSelectedUnit(Unit unit)
	{
		this.selectedUnit = unit;
	}
	public void setSelectedCity(City city)
	{
		this.selectedCity = city;
	}

	public int getMaxPopulation() {
		return maxPopulation;
	}
	public void setMaxPopulation(int maxPopulation) {
		this.maxPopulation = maxPopulation;
	}
	public int getXP() {
		return XP;
	}
	public void setXP(int XP) {
		this.XP = XP;
	}
	public void setMap(HashMap<Tile, TileState> map)
	{
		this.map = map;
	}

	public Civilization getCivilization()
	{
		return civilization;
	}

	public int getTilePurchaseCost() {
		return tilePurchaseCost;
	}
	public void setTilePurchaseCost(int tilePurchaseCost) {
		this.tilePurchaseCost = tilePurchaseCost;
	}
	public void increasePopulation(int i){
		this.population += i;
	}
	public int getFood()
	{
		return food;
	}
	public void setFood(int food)
	{
		this.food = food;
	}
	public int getGold()
	{
		return gold;
	}
	public int incomeGold()
	{
		int n = 0;
		for(City city : cities)
			n += city.getGoldYield();
		return n;
	}
	public void setGold(int gold)
	{
		this.gold = gold;
	}
	public int getHappiness()
	{
		return happiness;
	}
	public void setHappiness(int happiness)
	{
		this.happiness = happiness;
	}
	public boolean getIsUnHappy()
	{
		return isUnHappy;
	}
	public void isUnHappy()
	{
		isUnHappy = true;
		for(Unit unit : units) {
			System.out.println((int) (0.75 * gameController.powerForce(unit)));
			unit.setPower((int) (0.75 * gameController.powerForce(unit)));
			System.out.println(unit.getPower());
		}
	}
	public void isHappy()
	{
		isUnHappy = false;
		for(Unit unit : units)
		{
			if(unit.getClass().equals(MidRange.class))
				unit.setPower(((MidRange) unit).getType().getCombatStrength());
			if(unit.getClass().equals(LongRange.class))
				unit.setPower(((LongRange) unit).getType().getCombatStrength());
		}
	}

	public ArrayList<Technology> getTechnologies()
	{
		return technologies;
	}
	public void addTechnology(Technology technology)
	{
		technologies.add(technology);
	}
	public Technology getResearchingTechnology()
	{
		return researchingTechnology;
	}
	public void setResearchingTechnology(Technology technology)
	{
		researchingTechnology = technology;
	}
	public int[] getResearchingTechCounter()
	{
		return researchingTechCounter;
	}
	public ArrayList<City> getSeizedCities() {
		return seizedCities;
	}
	public ArrayList<ResourceType> getAcquiredLuxuryResources() {
		return acquiredLuxuryResources;
	}
	public void addLuxuryResource(LuxuryResource luxuryResource)
	{
		if(!acquiredLuxuryResources.contains(luxuryResource.getRESOURCE_TYPE()))
		{
			this.acquiredLuxuryResources.add(luxuryResource.getRESOURCE_TYPE());
			setHappiness((int) (getHappiness() * 1.1));
			if(getHappiness() > 100)
				setHappiness(100);
		}
	}

	public ArrayList<TradeRequest> getTradeRequests() {
		return tradeRequests;
	}

	public int getCup()
	{
		return cup;
	}
	public int incomeCup()
	{
		int n = 0;
		for(City city : cities)
			n += city.getCupYield();
		return n;
	}
	public void addResource(Resource resource)
	{
		resources.add(resource);
	}
	public void reduceCup()
	{
		this.cup = 0;
	}
	public void setCup(int cup)
	{
		this.cup = cup;
	}
	//TODO: I deleted updateCup. I hope there is no problem with that :)


	public int getPopulation() {
		int n = 0;
		for (City city : cities)
			n += city.getCitizens().size();
		return n;
	}

	public ArrayList<Resource> getResources()
	{
		return resources;
	}
	public void setResources(ArrayList<Resource> resources)
	{
		this.resources = resources;
	}

	// TODO: there should be a Map class which holds all the tiles and its methods like getTileByXY and getTileByQRS and ...
	public HashMap<Tile, TileState> getMap()
	{
		return map;
	}
	public Tile getTileByXY(int x, int y)
	{
		for(Tile tile : map.keySet())
			if(tile.getPosition().X == x && tile.getPosition().Y == y)
				return tile;
		return null;
	}
	public Tile getTileByQRS(int q, int r, int s)
	{
		for(Tile tile : map.keySet())
			if(tile.getPosition().Q == q && tile.getPosition().R == r && tile.getPosition().S == s)
				return tile;
		return null;
	}
	// this method gets a tile (center tile) and returns the tiles in range of distance from it
	public ArrayList<Tile> getAdjacentTiles(Tile tile, int distance)
	{
		ArrayList<Tile> adjacentTiles = new ArrayList<>();
		for(Tile thisTile : map.keySet())
			if(tile.distanceTo(thisTile) == 1)
				adjacentTiles.add(thisTile);

		return adjacentTiles;
	}
	public ArrayList<Integer> getCityBorderIndexes(Tile tile)
	{
		ArrayList<Integer> cityBorderIndexes = new ArrayList<>();

		for (City city : cities)
		{
			if(city.getTerritory().contains(tile))
			{
				if(getTileByQRS(tile.getPosition().Q, tile.getPosition().R - 1, tile.getPosition().S + 1) != null
						&& !city.getTerritory().contains(getTileByQRS(tile.getPosition().Q, tile.getPosition().R - 1, tile.getPosition().S + 1)))
					cityBorderIndexes.add(0);
				if(getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R, tile.getPosition().S + 1) != null
						&& !city.getTerritory().contains(getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R, tile.getPosition().S + 1)))
					cityBorderIndexes.add(1);
				if(getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R + 1, tile.getPosition().S) != null
						&& !city.getTerritory().contains(getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R + 1, tile.getPosition().S)))
					cityBorderIndexes.add(2);
				if(getTileByQRS(tile.getPosition().Q, tile.getPosition().R + 1, tile.getPosition().S - 1) != null
						&& !city.getTerritory().contains(getTileByQRS(tile.getPosition().Q, tile.getPosition().R + 1, tile.getPosition().S - 1)))
					cityBorderIndexes.add(3);
				if(getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R, tile.getPosition().S - 1) != null
						&& !city.getTerritory().contains(getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R, tile.getPosition().S - 1)))
					cityBorderIndexes.add(4);
				if(getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R - 1, tile.getPosition().S) != null
						&& !city.getTerritory().contains(getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R - 1, tile.getPosition().S)))
					cityBorderIndexes.add(5);

				break;
			}
		}

		return cityBorderIndexes;
	}
	public void addCity(City newCity)
	{
		if(this.cities == null){
			this.cities = new ArrayList<>();
			this.initialCapitalCity = newCity;
			setCapitalCity(newCity);
		}
		cities.add(newCity);
	}
	public void setCapitalCity(City city){
		this.currentCapitalCity = city;
		city.getBuildings().add(new Building(BuildingType.PALACE, city.getCapitalTile()));
	}
	//TODO: set tile states maybe????!!!
	public ArrayList<City> getCities()
	{
		return cities;
	}
	public int getTotalPopulation()
	{
		int n = 0;
		for(City city : cities)
			n += city.getPopulation();
		return n;
	}
	// this method gets a tile and returns the city it is in. (or null if it is not in a city)
	public City getTileCity(Tile tile)
	{
		for(City city : cities)
			for(Tile tileInCity : city.getTerritory())
				if(tileInCity.equals(tile))
					return city;
		return null;
	}
	public City getInitialCapitalCity()
	{
		return initialCapitalCity;
	}
	public void setInitialCapitalCity(City initialCapitalCity)
	{
		this.initialCapitalCity = initialCapitalCity;
	}
	public City getCurrentCapitalCity()
	{
		return currentCapitalCity;
	}
	public void setCurrentCapitalCity(City currentCapitalCity)
	{
		this.currentCapitalCity = currentCapitalCity;
	}
	public ArrayList<Notification> getNotifications()
	{
		return notifications;
	}
	public void addNotification(Notification notification)
	{
		notifications.add(notification);
	}
	public ArrayList<Unit> getUnits()
	{
		return units;
	}
	public void addUnit(Unit unit)
	{
		units.add(unit);
	}
	// this method gets a unit and removes it from the units list and tile's combatUnitInTile or tile's nonCombatUnitInTile
	public void removeUnit(Unit unit)
	{
		if(unit == null)
			return;
		units.remove(unit);
		if(unit instanceof CombatUnit)
			unit.getTile().setCombatUnitInTile(null);
		else if(unit instanceof NonCombatUnit)
			unit.getTile().setNonCombatUnitInTile(null);
		unit.setTile(null);
	}
	private Resource getResourceByName(String name) {
		for (Resource resource : gameController.getPlayerTurn().getResources())
			if (resource.getRESOURCE_TYPE().name().equals(name))
				return resource;
		return null;
	}
	public void checkRequests(TradeRequest request) {
		int coins;
		Resource resource;
		if(request.getOfferToSell().charAt(0) > 48 && request.getOfferToSell().charAt(0) < 57) {
			coins = Integer.parseInt(request.getOfferToSell());
			resource = getResourceByName(request.getWantToBuy());
			gameController.getPlayerTurn().setGold(gameController.getPlayerTurn().getGold() + coins);
			request.getSender().setGold(request.getSender().getGold() - coins);
			request.getSender().addResource(resource);

			//after trade, they become friends
			gameController.getPlayerTurn().getRelationStates().replace(request.getSender().getCivilization(), RelationState.FRIEND);
			request.getSender().getRelationStates().replace(gameController.getPlayerTurn().getCivilization(), RelationState.FRIEND);
		}
		else if(request.getOfferToSell().equals("peace")) {
			relationStates.replace(request.getSender().getCivilization(), RelationState.NEUTRAL);
			request.getSender().getRelationStates().replace(this.getCivilization(), RelationState.NEUTRAL);

		}
		else {
			coins = Integer.parseInt(request.getWantToBuy());
			resource = getResourceByName(request.getOfferToSell());
			if (coins <= gameController.getPlayerTurn().getGold()) {
				gameController.getPlayerTurn().setGold(gameController.getPlayerTurn().getGold() - coins);
				gameController.getPlayerTurn().addResource(resource);
				request.getSender().setGold(request.getSender().getGold() + coins);
			}
			//after trade, they become friends
			gameController.getPlayerTurn().getRelationStates().replace(request.getSender().getCivilization(), RelationState.FRIEND);
			request.getSender().getRelationStates().replace(gameController.getPlayerTurn().getCivilization(), RelationState.FRIEND);
		}
	}
	public void setGameController(GameController gameController)
	{
		this.gameController = gameController;
	}
	public void updateTileStates()
	{
		//iterate through all tiles and change their state based on their relative position to units and cities

		// this is all tiles that can be seen by the units and cities
		HashSet<Tile> tilesInSight = new HashSet<>();

		//tiles in sight of units
		for(Unit unit : units)
			for(Tile tile : map.keySet())
			{
				int distance = tile.distanceTo(unit.getTile());

				if(distance == 0 || distance == 1)
					tilesInSight.add(tile);
				else if(distance == 2)
				{
					Position unitPosition = unit.getTile().getPosition();
					Position tilePosition = tile.getPosition();

					if(unitPosition.Q == tilePosition.Q)
					{
						Tile tileBetween = getTileByQRS(unitPosition.Q, (unitPosition.R + tilePosition.R) / 2, (unitPosition.S + tilePosition.S) / 2);
						if(!tileBetween.isBlocker())
							tilesInSight.add(tile);
					}
					else if(unitPosition.R == tilePosition.R)
					{
						Tile tileBetween = getTileByQRS((unitPosition.Q + tilePosition.Q) / 2, unitPosition.R, (unitPosition.S + tilePosition.S) / 2);
						if(!tileBetween.isBlocker())
							tilesInSight.add(tile);
					}
					else if(unitPosition.S == tilePosition.S)
					{
						Tile tileBetween = getTileByQRS((unitPosition.Q + tilePosition.Q) / 2, (unitPosition.R + tilePosition.R) / 2, unitPosition.S);
						if(!tileBetween.isBlocker())
							tilesInSight.add(tile);
					} //----------------
					if(tilePosition.Q - unitPosition.Q == 1 && tilePosition.R - unitPosition.R == -2)
					{
						Tile northNeighbor = getTileByQRS(tilePosition.Q, tilePosition.R - 1, tilePosition.S + 1);
						Tile northEastNeighbor = getTileByQRS(unitPosition.Q + 1, unitPosition.R - 1, unitPosition.S);
						if((northNeighbor != null && !northNeighbor.isBlocker()) ||
								(northEastNeighbor != null && !northEastNeighbor.isBlocker()))
							tilesInSight.add(tile);
					}
					else if(tilePosition.Q - unitPosition.Q == 2 && tilePosition.R - unitPosition.R == -1)
					{
						Tile northEastNeighbor = getTileByQRS(unitPosition.Q + 1, unitPosition.R - 1, unitPosition.S);
						Tile southEastNeighbor = getTileByQRS(unitPosition.Q + 1, unitPosition.R, unitPosition.S - 1);
						if((northEastNeighbor != null && !northEastNeighbor.isBlocker()) ||
								(southEastNeighbor != null && !southEastNeighbor.isBlocker()))
							tilesInSight.add(tile);
					}
					else if(tilePosition.Q - unitPosition.Q == 1 && tilePosition.R - unitPosition.R == 1)
					{
						Tile southEastNeighbor = getTileByQRS(unitPosition.Q + 1, unitPosition.R, unitPosition.S - 1);
						Tile southNeighbor = getTileByQRS(unitPosition.Q, unitPosition.R + 1, unitPosition.S - 1);
						if((southEastNeighbor != null && !southEastNeighbor.isBlocker()) ||
								(southNeighbor != null && !southNeighbor.isBlocker()))
							tilesInSight.add(tile);
					}
					else if(tilePosition.Q - unitPosition.Q == -1 && tilePosition.R - unitPosition.R == 2)
					{
						Tile southNeighbor = getTileByQRS(unitPosition.Q, unitPosition.R + 1, unitPosition.S - 1);
						Tile southWestNeighbor = getTileByQRS(unitPosition.Q - 1, unitPosition.R + 1, unitPosition.S);
						if((southNeighbor != null && !southNeighbor.isBlocker()) ||
								(southWestNeighbor != null && !southWestNeighbor.isBlocker()))
							tilesInSight.add(tile);
					}
					else if(tilePosition.Q - unitPosition.Q == -2 && tilePosition.R - unitPosition.R == 1)
					{
						Tile southWestNeighbor = getTileByQRS(unitPosition.Q - 1, unitPosition.R + 1, unitPosition.S);
						Tile northWestNeighbor = getTileByQRS(unitPosition.Q - 1, unitPosition.R, unitPosition.S + 1);
						if((southWestNeighbor != null && !southWestNeighbor.isBlocker()) ||
								(northWestNeighbor != null && !northWestNeighbor.isBlocker()))
							tilesInSight.add(tile);
					}
					else if(tilePosition.Q - unitPosition.Q == -1 && tilePosition.R - unitPosition.R == -1)
					{
						Tile northWestNeighbor = getTileByQRS(unitPosition.Q - 1, unitPosition.R, unitPosition.S + 1);
						Tile northNeighbor = getTileByQRS(unitPosition.Q, unitPosition.R - 1, unitPosition.S + 1);
						if((northWestNeighbor != null && !northWestNeighbor.isBlocker()) ||
								(northNeighbor != null && !northNeighbor.isBlocker()))
							tilesInSight.add(tile);
					}
				}
			}
		//tiles in sight of cities
		for(City city : cities)
			for(Tile tile : city.getTerritory())
			{
				tilesInSight.add(tile);
				tilesInSight.addAll(getAdjacentTiles(tile, 1));
			}

		/* update tileStates */
		// tileStates that are in sight
		HashSet<Tile> tilesToBeVisible = new HashSet<>();
		for(Tile tile : tilesInSight)
			if(map.get(tile).equals(TileState.FOG_OF_WAR))
				map.replace(tile, TileState.VISIBLE);
			else if(map.get(tile).equals(TileState.REVEALED))
				tilesToBeVisible.add(tile);
		for(Tile tile : tilesToBeVisible)
		{
			map.remove(tile);
			tilesInSight.remove(tile);
			Tile visibleTile = gameController.getTileByXY(tile.getPosition().X, tile.getPosition().Y);
			map.put(visibleTile, TileState.VISIBLE);
			tilesInSight.add(visibleTile);
		}

		// collect all tiles that are not in sight and are not fog of war to make them REVEALED
		HashSet<Tile> tilesToBeRevealed = new HashSet<Tile>();
		for(Tile tile : map.keySet())
		{
			if(tilesInSight.contains(tile))
				continue;
			if(map.get(tile).equals(TileState.VISIBLE))
				tilesToBeRevealed.add(tile);
		}
		for(Tile tile : tilesToBeRevealed)
		{
			map.remove(tile);
			map.put(gameController.getTileByXY(tile.getPosition().X, tile.getPosition().Y).clone(), TileState.REVEALED);
		}
	}

	// gets the ArrayList<Tile> map from gameController and fills the HashMap<Tile, TileState> map
	public void initMap()
	{
		for (Tile tile : gameController.getMap())
			map.put(tile, TileState.FOG_OF_WAR);
	}
}























