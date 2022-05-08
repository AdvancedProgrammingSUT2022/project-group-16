package Models.Player;

import Controllers.GameController;
import Models.City.City;
import Models.City.Product;
import Models.Resources.LuxuryResource;
import Models.Resources.Resource;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.Units.CombatUnits.LongRange;
import Models.Units.Unit;
import Models.User;

import java.util.*;

public class Player extends User
{
	private Unit selectedUnit = null;
	private City selectedCity = null;
	private final Civilization civilization;
	private int food = 0;
	private int cup = 0;
	private int gold = 0;
	private int happiness = 0;
	private int population = 0;
	private final ArrayList<Technology> technologies = new ArrayList<>();
	private Technology researchingTechnology;
	private int[] researchingTechCounter = new int[50];
	private ArrayList<Resource> resources;
	private final ArrayList<LuxuryResource> acquiredLuxuryResources = new ArrayList<>(); // this is for checking to increase happiness when acquiring luxury resources
	private ArrayList<Improvement> improvements = new ArrayList<>();
	private final HashMap<Tile, TileState> map;
	private ArrayList<City> cities = new ArrayList<>();
	private City initialCapitalCity;    //??TODO
	private City currentCapitalCity;    //??TODO
	private final Stack<Notification> notifications = new Stack<>();
	private ArrayList<Unit> units = new ArrayList<>();

	public Player(Civilization civilization, String username, String nickname, String password)
	{
		super(username, nickname, password);
		this.civilization = civilization;
		map = new HashMap<>();
		for(Tile tile : GameController.getInstance().getMap())
			map.put(tile, TileState.FOG_OF_WAR);
		setTileStates();
	}

	public City getSelectedCity() {
		return selectedCity;
	}
	public Unit getSelectedUnit() {
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
	public int getGold() {
		return gold;
	}
	public int incomeGold()
	{
		int n = 0;
		for(City city : cities)
			n += city.getGoldYield();
		return n;
	}
	public void setGold(int amount)
	{
		this.gold += amount;
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
	public int[] getResearchingTechCounter() {
		return researchingTechCounter;
	}
	public void addResearchingTechCounter(int index, int amount) {
		researchingTechCounter[index] += amount;
	}

	public int getCup() {
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
	public void setCup(int amount) {
		this.cup += amount;
	}

	public ArrayList<Resource> getResources()
	{
		return resources;
	}
	public void setResources(ArrayList<Resource> resources)
	{
		this.resources = resources;
	}

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
	public void addCity(City newCity) {
		cities.add(newCity);
	}
	public void setMap(ArrayList<Tile> map)
	{
		// TODO
	}
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
	public void addUnit(Unit unit) {
		units.add(unit);
	}
	public void setTileStates()
	{
		Random tileStateRandom = new Random();
		for(Map.Entry<Tile, TileState> entry : map.entrySet())
			map.replace(entry.getKey(), TileState.values()[tileStateRandom.nextInt(TileState.values().length)]);
	}
//	public ArrayList<Resource> getStrategicResources()
//	{
//
//	}
}























