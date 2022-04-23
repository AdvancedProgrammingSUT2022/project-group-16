package Models.City;

import Models.Game.Position;
public class Building
{
	private BuildingType buildingType;
	private Position position;
	private int hitPoints;
	
	public Building(BuildingType buildingType, Position position)
	{
		this.buildingType = buildingType;
		this.position = position;
	}
	
	public BuildingType getBuildingType()
	{
		return buildingType;
	}
	public void setBuildingType(BuildingType buildingType)
	{
		this.buildingType = buildingType;
	}
	public Position getPosition()
	{
		return position;
	}
	public void setPosition(Position position)
	{
		this.position = position;
	}
	
	//TODO do special things for each type of building
}
