package Models.Terrain;

import com.diogonunes.jcolor.Attribute;

public enum TileType
{
	DESERT(0, 0, 0, -33, 1, true, false, Attribute.BACK_COLOR(180, 180, 80)),
	GRASSLAND(2, 0, 0, -33, 1, true, false, Attribute.BACK_COLOR(50, 210, 0)),
	HILLS(0, 2, 0, 25, 2, true, true, Attribute.BACK_COLOR(130, 210, 50)),
	MOUNTAIN(0, 0, 0, 25, 0, false, true, Attribute.BACK_COLOR(100, 100, 90)),
	OCEAN(0, 0, 0, 25, 0, false, false, Attribute.BACK_COLOR(30, 100, 180)),
	PLAINS(1, 1, 0, -33, 1, true, false, Attribute.BACK_COLOR(120, 230, 100)),
	SNOW(0, 0, 0, -33, 1, true, false, Attribute.BACK_COLOR(240, 240, 240)),
	TUNDRA(1, 0, 0, -33, 1, true, false, Attribute.BACK_COLOR(180, 180, 240));
	
	public final int food;
	public final int production;
	public final int gold;
	public final int combatModifier;
	public final int movementCost;
	public final Attribute attribute;
	public final boolean isBlocker;
	
	TileType(int food, int production, int gold, int combatModifier, int movementCost, boolean isPassable, boolean isBlocker, Attribute attribute) // attribute is temp. it's for printing tile on terminal
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.combatModifier = combatModifier;
		this.movementCost = movementCost;
		this.attribute = attribute;
		this.isBlocker = true;
	}
}

























