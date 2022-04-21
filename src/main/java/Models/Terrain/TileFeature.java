package Models.Terrain;

import com.diogonunes.jcolor.Attribute;

public enum TileFeature
{
	NONE(0, 0, 0, 0, 0, true, false, null),
	FLOOD_PLAIN(2, 0, 0, -33, 1, true, false, Attribute.BACK_COLOR(0, 85, 0)),
	FOREST(1, 1, 0, 25, 2, true, true, Attribute.BACK_COLOR(115, 185, 20)),
	ICE(0, 0, 0, 0, 0, false, false, Attribute.BACK_COLOR(110, 220, 210)),
	JUNGLE(1, -1, 0, 25, 2, true, false, Attribute.BACK_COLOR(69, 175, 10)),
	MARSH(-1, 0, 0, -33, 2, true, false, Attribute.BACK_COLOR(16, 67, 2)),
	OASIS(3, 0, 1, -33, 1, true, false, Attribute.BACK_COLOR(210, 210, 0));
	
	public final int food;
	public final int production;
	public final int gold;
	public final int combatModifier;
	public final int movementCost;
	public final Attribute attribute;
	public final boolean isBlocker;
	
	TileFeature(int food, int production, int gold, int combatModifier, int movementCost, boolean isPassable, boolean isBlocker, Attribute attribute)
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.combatModifier = combatModifier;
		this.movementCost = movementCost;
		this.attribute = attribute;
		this.isBlocker = isBlocker;
	}
}
