package Models.City;

import Models.Game.Position;
import Models.Terrain.Tile;

public class Building implements Constructable
{
	private final BuildingType buildingType;
	private final Tile tile;
	private int hitPoints;
	
	public Building(BuildingType buildingType, Tile tile)
	{
		this.buildingType = buildingType;
		this.tile = tile;
	}
	
	public BuildingType getBuildingType()
	{
		return buildingType;
	}
	public Tile getTile()
	{
		return tile;
	}
	public int getHitPoints()
	{
		return hitPoints;
	}
	public void setHitPoints(int hitPoints)
	{
		this.hitPoints = hitPoints;
	}
	//TODO: do special things for each type of building
}





















