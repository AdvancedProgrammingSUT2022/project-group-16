package Models.City;

import Models.Player.Player;
import Models.Terrain.Tile;

import java.util.ArrayList;

public class City
{
	ArrayList<Tile> territory;
	Tile capitalTile;
	int foodYield;
	int productionYield;
	int goldYield;
	ArrayList<Building> buildings;
	ArrayList<Citizen> citizens;
	Player rulerPlayer;
	
	public City(Tile capitalTile, Player rulerPlayer)
	{
		this.capitalTile = capitalTile;
		this.rulerPlayer = rulerPlayer;
		territory = new ArrayList<>();
		territory.add(capitalTile); //TODO: also add adjacent tiles
		foodYield = 0;
		productionYield = 0;
		goldYield = 0;
		buildings = new ArrayList<>();
		citizens = new ArrayList<>();
	}
	
	public void createBuilding() //TODO
	{
	
	}
	public void growCity() //TODO
	{
	
	}
}





















