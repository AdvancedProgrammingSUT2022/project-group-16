package Models.Terrain;

import com.diogonunes.jcolor.Attribute;

public enum TileType
{
	DESERT(),
	GRASSLAND(),
	HILLS(),
	MOUNTAIN(),
	OCEAN(),
	PLAINS(),
	SNOW(),
	TUNDRA();
	
	public final int food;
	public final int production;
	public final int gold;
	public final int combatModifier;
	public final int movementCost;
	
	TileType(int food, int production, int gold, int combatModifier, int movementCost, Attribute tileAttribute) //attribute is temp. it's for printing tile on terminal
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.combatModifier = combatModifier;
		this.movementCost = movementCost;
	}
}

























