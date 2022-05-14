package Models.Terrain;

public enum TileFeature
{
	NONE(0, 0, 0, 0, 0, true, false, "  ", 0),
	FLOOD_PLAIN(2, 0, 0, -33, 1, true, false, "\uD83C\uDFDE", 0),
	FOREST(1, 1, 0, 25, 2, true, true, "\uD83C\uDF32", 10),
	ICE(0, 0, 0, 0, 0, false, false, "❄️", 0),
	JUNGLE(1, -1, 0, 25, 2, true, false, "\uD83C\uDF33", 13),
	MARSH(-1, 0, 0, -33, 2, true, false, "\uD83C\uDF40", 12),
	OASIS(3, 0, 1, -33, 1, true, false, "\uD83C\uDFDC", 0);
	
	public final int food;
	public final int production;
	public final int gold;
	public final int combatModifier;
	public final int movementCost;
	public final boolean isBlocker;
	public final String symbol;
	public final int inLineTurn;
	
	TileFeature(int food, int production, int gold, int combatModifier, int movementCost, boolean isPassable, boolean isBlocker, String symbol, int inLineTurn)
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.combatModifier = combatModifier;
		this.movementCost = movementCost;
		this.isBlocker = isBlocker;
		this.symbol = symbol;
		this.inLineTurn = inLineTurn;
	}
}




















