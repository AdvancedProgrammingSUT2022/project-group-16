package Models.Resources;

import Models.Player.Technology;
import Models.Terrain.Improvement;

public enum ResourceType
{
	NONE(0, 0, 0, null, null, "  "),
	
	BANANA(1, 0, 0, Improvement.PLANTATION, null, "\uD83C\uDF4C"),
	CATTLE(1, 0, 0, Improvement.PASTURE, null, "\uD83D\uDC2E"),
	DEER(1, 0, 0, Improvement.CAMP, null, "\uD83E\uDD8C"),
	SHEEP(2, 0, 0, Improvement.PASTURE, null, "\uD83D\uDC11"),
	WHEAT(1, 0, 0, Improvement.FARM, null, "\uD83C\uDF3E"),
	
	COAL(0, 1, 0, Improvement.MINE, Technology.SCIENTIFIC_THEORY, "\uD83E\uDEA8"),
	HORSES(0, 1, 0, Improvement.PASTURE, Technology.ANIMAL_HUSBANDRY, "\uD83D\uDC34"),
	IRON(0, 1, 0, Improvement.MINE, Technology.IRON_WORKING, "\uD83E\uDDF2"),
	
	COTTON(0, 0, 2, Improvement.PLANTATION, null, "☁"),
	DYES(0, 0, 2, Improvement.PLANTATION, null, "\uD83E\uDEA3"),
	FURS(0, 0, 2, Improvement.CAMP, null, "\uD83E\uDDE5"),
	GEMS(0, 0, 3, Improvement.MINE, null, "\uD83D\uDC8E"),
	GOLD(0, 0, 2, Improvement.MINE, null, "\uD83E\uDE99"),
	INCENSE(0, 0, 2, Improvement.PLANTATION, null, "\uD83E\uDEB5"),
	IVORY(0, 0, 2, Improvement.CAMP, null, "\uD83E\uDDA3"),
	MARBLE(0, 0, 2, Improvement.QUARRY, null, "\uD83D\uDD3B"),
	SILK(0, 0, 2, Improvement.PLANTATION, null, "\uD83E\uDD7B"),
	SILVER(0, 0, 2, Improvement.MINE, null, "\uD83E\uDD48"),
	SUGAR(0, 0, 2, Improvement.PLANTATION, null, "\uD83C\uDF6D");
	
	public final int food;
	public final int production;
	public final int gold;
	public final Improvement requiredImprovement;
	public final Technology requiredTechnology;
	public final String symbol;
	
	ResourceType(int food, int production, int gold, Improvement requiredImprovement, Technology requiredTechnology, String symbol)
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.requiredImprovement = requiredImprovement;
		this.requiredTechnology = requiredTechnology;
		this.symbol = symbol;
	}
}















