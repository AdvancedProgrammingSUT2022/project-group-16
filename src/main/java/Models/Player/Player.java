package Models.Player;

import Controllers.GameController;
import Models.City.*;
import Models.Resources.LuxuryResource;
import Models.Resources.Resource;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
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
	private Technology researchingTechnology;
	private int[] researchingTechCounter = new int[50];
	private ArrayList<Resource> resources;
	private final ArrayList<LuxuryResource> acquiredLuxuryResources = new ArrayList<>(); // this is for checking to increase happiness when acquiring luxury resources
	private final ArrayList<Improvement> improvements = new ArrayList<>();
	private HashMap<Tile, TileState> map; //TODO: make this final
	private ArrayList<City> cities = new ArrayList<>();
	private City initialCapitalCity;    //??TODO
	private City currentCapitalCity;    //??TODO
	private final Stack<Notification> notifications = new Stack<>();
	private ArrayList<Unit> units = new ArrayList<>();

	public Player(Civilization civilization, String username, String nickname, String password)
	{
		super(username, nickname, password);
		this.civilization = civilization;
		gameController = GameController.getInstance();
		this.map = new HashMap<>();
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
	//TODO: I deleted updateCup. I hope there isn't any problem with that :)

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
	public Stack<Notification> getNotifications()
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
	public void updateTileStates()
	{
		//TODO: set tile states based on units and cities positions
		//TODO: is this the best way to do it?
		
		// Bad smell code :(
		HashSet<Tile> tilesInSight = new HashSet<>();
		
		// set tileStates of tiles around units
		for(Unit unit : units)
		{
			tilesInSight.add(unit.getTile());
			
			Tile tile;
			Tile outerTile;
			// for all 6 borders:
			// northern border
			tile = getTileByQRS(unit.getTile().getPosition().Q, unit.getTile().getPosition().R - 1, unit.getTile().getPosition().S + 1);
			if(tile != null)
			{
				tilesInSight.add(tile);
				if(!tile.getTileType().isBlocker && (outerTile = getTileByQRS(tile.getPosition().Q, tile.getPosition().R - 1, tile.getPosition().S + 1)) != null)
					tilesInSight.add(outerTile);
			}
			// northern-eastern border
			tile = getTileByQRS(unit.getTile().getPosition().Q + 1, unit.getTile().getPosition().R - 1, unit.getTile().getPosition().S);
			if(tile != null)
			{
				tilesInSight.add(tile);
				if(!tile.getTileType().isBlocker && (outerTile = getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R - 1, tile.getPosition().S)) != null)
					tilesInSight.add(outerTile);
			}
			// southern-eastern border
			tile = getTileByQRS(unit.getTile().getPosition().Q + 1, unit.getTile().getPosition().R, unit.getTile().getPosition().S - 1);
			if(tile != null)
			{
				tilesInSight.add(tile);
				if(!tile.getTileType().isBlocker && (outerTile = getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R, tile.getPosition().S - 1)) != null)
					tilesInSight.add(outerTile);
			}
			// southern border
			tile = getTileByQRS(unit.getTile().getPosition().Q, unit.getTile().getPosition().R + 1, unit.getTile().getPosition().S - 1);
			if(tile != null)
			{
				tilesInSight.add(tile);
				if(!tile.getTileType().isBlocker && (outerTile = getTileByQRS(tile.getPosition().Q, tile.getPosition().R + 1, tile.getPosition().S - 1)) != null)
					tilesInSight.add(outerTile);
			}
			// southern-western border
			tile = getTileByQRS(unit.getTile().getPosition().Q - 1, unit.getTile().getPosition().R + 1, unit.getTile().getPosition().S);
			if(tile != null)
			{
				tilesInSight.add(tile);
				if(!tile.getTileType().isBlocker && (outerTile = getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R + 1, tile.getPosition().S)) != null)
					tilesInSight.add(outerTile);
			}
			// northern-western border
			tile = getTileByQRS(unit.getTile().getPosition().Q - 1, unit.getTile().getPosition().R, unit.getTile().getPosition().S + 1);
			if(tile != null)
			{
				tilesInSight.add(tile);
				if(!tile.getTileType().isBlocker && (outerTile = getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R, tile.getPosition().S + 1)) != null)
					tilesInSight.add(outerTile);
			}
		}
		//TODO: set tileStates of tiles around cities
		
		// set tile states
		for(Tile t : tilesInSight)
			if(!map.get(t).equals(TileState.VISIBLE))
				map.replace(t, TileState.VISIBLE);
		
	}
//	public ArrayList<Resource> getStrategicResources()
//	{
//
//	}
}























