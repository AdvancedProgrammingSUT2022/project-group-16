package Models.Player;

import Models.City.City;
import Models.Game.Position;
import Models.Terrain.Tile;
import Models.Units.Unit;
import Models.User;
import com.sun.jna.platform.unix.Resource;

import java.util.ArrayList;
import java.util.Stack;

public class Player extends User
{
	private Civilization civilization;
	private int food;
	private int gold;
	private int happiness;
	private ArrayList<Technology> technologies;
	private Technology researchingTechnology;
	private ArrayList<Resource> resources;
	private ArrayList<Tile> map;
	private ArrayList<Position> visiblePositions;
	private ArrayList<Position> revealedPositions;
	private ArrayList<Position> fogPositions;
	private ArrayList<City> cities;
	private City initialCapitalCity;    //??TODO
	private City currentCapitalCity;    //??TODO
	private Stack<Notification> notifications;
	private ArrayList<Unit> units;
//	Unit selectedUnit;  //??TODO probably unnecessary
	
	
	public Player(Civilization civilization, String username, String nickname, String password)
	{
		super(username, nickname, password);
		this.civilization = civilization;
		food = 0;
		gold = 0;
		happiness = 0;
		technologies = new ArrayList<>();
		resources = new ArrayList<>();
//		map = GameController.getMap(); TODO
		visiblePositions = new ArrayList<>();
		revealedPositions = new ArrayList<>();
		fogPositions = new ArrayList<>();
		cities = new ArrayList<>();
		notifications = new Stack<>();
		units = new ArrayList<Unit>();
	}
	
	public Civilization getCivilization()
	{
		return civilization;
	}
	public void setCivilization(Civilization civilization)
	{
		this.civilization = civilization;
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
	public void setTechnologies(ArrayList<Technology> technologies)
	{
		this.technologies = technologies;
	}
	public Technology getResearchingTechnology()
	{
		return researchingTechnology;
	}
	public void setResearchingTechnology(Technology researchingTechnology)
	{
		this.researchingTechnology = researchingTechnology;
	}
	public ArrayList<Resource> getResources()
	{
		return resources;
	}
	public void setResources(ArrayList<Resource> resources)
	{
		this.resources = resources;
	}
	public ArrayList<Tile> getMap()
	{
		return map;
	}
	public void setMap(ArrayList<Tile> map)
	{
		this.map = map;
	}
	public ArrayList<Position> getVisiblePositions()
	{
		return visiblePositions;
	}
	public void setVisiblePositions(ArrayList<Position> visiblePositions)
	{
		this.visiblePositions = visiblePositions;
	}
	public ArrayList<Position> getRevealedPositions()
	{
		return revealedPositions;
	}
	public void setRevealedPositions(ArrayList<Position> revealedPositions)
	{
		this.revealedPositions = revealedPositions;
	}
	public ArrayList<Position> getFogPositions()
	{
		return fogPositions;
	}
	public void setFogPositions(ArrayList<Position> fogPositions)
	{
		this.fogPositions = fogPositions;
	}
	public ArrayList<City> getCities()
	{
		return cities;
	}
	public void setCities(ArrayList<City> cities)
	{
		this.cities = cities;
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
	public void setNotifications(Stack<Notification> notifications)
	{
		this.notifications = notifications;
	}
	public ArrayList<Unit> getUnits()
	{
		return units;
	}
	public void setUnits(ArrayList<Unit> units)
	{
		this.units = units;
	}
}























