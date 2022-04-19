package Models.Terrain;

import com.diogonunes.jcolor.Attribute;

public enum TileFeature
{
	NONE(),
	FLOOD_PLAIN(),
	FOREST(),
	ICE(),
	JUNGLE(),
	MARSH(),
	OASIS();
	
	public final int food;
	public final int production;
	public final int gold;
	public final int combatModifier;
	public final int movementCost;
	
	TileFeature(int food, int production, int gold, int combatModifier, int movementCost, Attribute tileAttribute)
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.combatModifier = combatModifier;
		this.movementCost = movementCost;
	}
}
