package Models.Player;

import Controllers.GameController;
import Models.City.*;
import Models.Game.Position;
import Models.Resources.LuxuryResource;
import Models.Resources.Resource;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.Unit;
import Models.User;

import java.util.*;

public class Player extends User
{
	GameController gameController;
	private Unit selectedUnit = null;
	private City selectedCity = null;
	private final Civilization civilization;
	private int food = 0;
	private int cup = 0;
	private int gold = 0;
	private int happiness = 0;
	private int population = 0; //TODO: maybe it's better to delete this line
	private final ArrayList<Technology> technologies = new ArrayList<>();
	private int[] researchingTechCounter = new int[50];
	private Technology researchingTechnology;
	private ArrayList<Resource> resources;
	private final ArrayList<LuxuryResource> acquiredLuxuryResources = new ArrayList<>(); // this is for checking to increase happiness when acquiring luxury resources
	private final ArrayList<Improvement> improvements = new ArrayList<>();
	private HashMap<Tile, TileState> map; //TODO: make this final when no change is needed
	private ArrayList<City> cities = new ArrayList<>();
	private City initialCapitalCity;    //??TODO
	private City currentCapitalCity;    //??TODO
	private final ArrayList<Notification> notifications = new ArrayList<>();
	private ArrayList<Unit> units = new ArrayList<>();

	public Player(Civilization civilization, String username, String nickname, String password, int score)
	{
		super(username, nickname, password);
		this.civilization = civilization;
		this.happiness = 100;
		gameController = GameController.getInstance();
		this.map = new HashMap<>();
		this.setScore(score);
		for(Tile tile : gameController.getMap())
			this.map.put(tile, TileState.FOG_OF_WAR);
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

	public int getPopulation() {
		return population;
	}
	public void setPopulation(int population) {
		this.population = population;
	}

	public Civilization getCivilization()
	{
		return civilization;
	}
	public void setCapitalCity(City city)
	{
		currentCapitalCity = city;
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
	public void addResearchingTechCounter(int index, int amount)
	{
		researchingTechCounter[index] += amount;
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
	public void reduceCup()
	{
		this.cup = 0;
	}
	public void setCup(int cup)
	{
		this.cup = cup;
	}
	//TODO: I deleted updateCup. I hope there is no problem with that :)

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
	public void addCity(City newCity)
	{
		cities.add(newCity);
	}
//	public void setMap(ArrayList<Tile> map)
//	{
//		this.map = new HashMap<>();
//		for(Tile tile : map)
//			this.map.put(tile, TileState.FOG_OF_WAR);
//	}
	//TODO: set tile states maybe????!!!
	public ArrayList<City> getCities()
	{
		return cities;
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
						if(!tileBetween.getTileType().isBlocker && !tileBetween.getTileFeature().isBlocker)
							tilesInSight.add(tile);
					}
					else if(unitPosition.R == tilePosition.R)
					{
						Tile tileBetween = getTileByQRS((unitPosition.Q + tilePosition.Q) / 2, unitPosition.R, (unitPosition.S + tilePosition.S) / 2);
						if(!tileBetween.getTileType().isBlocker && !tileBetween.getTileFeature().isBlocker)
							tilesInSight.add(tile);
					}
					else if(unitPosition.S == tilePosition.S)
					{
						Tile tileBetween = getTileByQRS((unitPosition.Q + tilePosition.Q) / 2, (unitPosition.R + tilePosition.R) / 2, unitPosition.S);
						if(!tileBetween.getTileType().isBlocker && !tileBetween.getTileFeature().isBlocker)
							tilesInSight.add(tile);
					}
					if(tilePosition.Q - unitPosition.Q == 1 && tilePosition.R - unitPosition.R == -2 && tilePosition.S - unitPosition.S == 1
							&& !getTileByXY(unitPosition.X - 1, unitPosition.Y).getTileType().isBlocker && !getTileByQRS(unitPosition.Q + 1, unitPosition.R - 1, unitPosition.S).getTileType().isBlocker)
						tilesInSight.add(tile);
					else if(tilePosition.Q - unitPosition.Q == 2 && tilePosition.R - unitPosition.R == -1 && tilePosition.S - unitPosition.S == -1
							&& !getTileByQRS(unitPosition.Q + 1, unitPosition.R - 1, unitPosition.S).getTileType().isBlocker && !getTileByQRS(unitPosition.Q + 1, unitPosition.R, unitPosition.S - 1).getTileType().isBlocker)
						tilesInSight.add(tile);
					else if(tilePosition.Q - unitPosition.Q == 1 && tilePosition.R - unitPosition.R == 1 && tilePosition.S - unitPosition.S == -2
							&& !getTileByQRS(unitPosition.Q + 1, unitPosition.R, unitPosition.S - 1).getTileType().isBlocker && !getTileByQRS(unitPosition.Q, unitPosition.R + 1, unitPosition.S - 1).getTileType().isBlocker)
						tilesInSight.add(tile);
					else if(tilePosition.Q - unitPosition.Q == -1 && tilePosition.R - unitPosition.R == 2 && tilePosition.S - unitPosition.S == -1
							&& !getTileByQRS(unitPosition.Q, unitPosition.R + 1, unitPosition.S - 1).getTileType().isBlocker && !getTileByQRS(unitPosition.Q - 1, unitPosition.R + 1, unitPosition.S).getTileType().isBlocker)
						tilesInSight.add(tile);
					else if(tilePosition.Q - unitPosition.Q == -2 && tilePosition.R - unitPosition.R == 1 && tilePosition.S - unitPosition.S == 1
							&& !getTileByQRS(unitPosition.Q - 1, unitPosition.R + 1, unitPosition.S).getTileType().isBlocker && !getTileByQRS(unitPosition.Q - 1, unitPosition.R, unitPosition.S + 1).getTileType().isBlocker)
						tilesInSight.add(tile);
					else if(tilePosition.Q - unitPosition.Q == -1 && tilePosition.R - unitPosition.R == -1 && tilePosition.S - unitPosition.S == 2
							&& !getTileByQRS(unitPosition.Q - 1, unitPosition.R, unitPosition.S + 1).getTileType().isBlocker && !getTileByQRS(unitPosition.Q, unitPosition.R - 1, unitPosition.S + 1).getTileType().isBlocker)
						tilesInSight.add(tile);
						
				}
			}
		//tiles in sight of cities
		for(City city : cities)
			for(Tile tile : city.getTerritory())
			{
				tilesInSight.add(tile);
				tilesInSight.addAll(getAdjacentTiles(tile));
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
}























