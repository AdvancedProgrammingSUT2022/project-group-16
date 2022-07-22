package Models.City;

import Models.Terrain.Tile;

public class Building extends Construction
{
	private final BuildingType buildingType;
	private Tile tile;
	private int hitPoints;
	transient private City city;

	public Building(BuildingType buildingType, Tile tile)
	{
		this.buildingType = buildingType;
		this.tile = tile;
	}

	public City getCity() {
		return city;
	}

	public void setCity(City city) {
		this.city = city;
	}

	public BuildingType getBuildingType()
	{
		return buildingType;
	}
	public Tile getTile()
	{
		return tile;
	}
	public void setTile(Tile tile)
	{
		this.tile = tile;
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

	public int happinessFromBuilding(BuildingType buildingType)
	{
		if(buildingType.equals(BuildingType.BURIAL_TOMB)) return 2;
		if(buildingType.equals(BuildingType.CIRCUS)) return 3;
		if(buildingType.equals(BuildingType.COLOSSEUM)) return 4;
		if(buildingType.equals(BuildingType.SATRAPS_COURT)) return 2;
		if(buildingType.equals(BuildingType.THEATER)) return 4;
		return 0;
	}
	public String toString(){
		return "Building";
	}
}





















