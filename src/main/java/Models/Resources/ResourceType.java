package Models.Resources;

import Models.Player.Technology;
import Models.Terrain.Improvement;

public enum ResourceType
{
	NONE(0, 0, 0, null, null),
	
	BANANA(1, 0, 0, Improvement.PLANTATION, null),
	CATTLE(1, 0, 0, Improvement.PASTURE, null),
	DEER(1, 0, 0, Improvement.CAMP, null),
	SHEEP(2, 0, 0, Improvement.PASTURE, null),
	WHEAT(1, 0, 0, Improvement.FARM, null),
	
	COAL(0, 1, 0, Improvement.MINE, Technology.SCIENTIFIC_THEORY),
	HORSES(0, 1, 0, Improvement.PASTURE, Technology.ANIMAL_HUSBANDRY),
	IRON(0, 1, 0, Improvement.MINE, Technology.IRON_WORKING),
	
	COTTON(0, 0, 2, Improvement.PLANTATION, null),
	DYES(0, 0, 2, Improvement.PLANTATION, null),
	FURS(0, 0, 2, Improvement.CAMP, null),
	GEMS(0, 0, 3, Improvement.MINE, null),
	GOLD(0, 0, 2, Improvement.MINE, null),
	INCENSE(0, 0, 2, Improvement.PLANTATION, null),
	IVORY(0, 0, 2, Improvement.CAMP, null),
	MARBLE(0, 0, 2, Improvement.QUARRY, null),
	SILK(0, 0, 2, Improvement.PLANTATION, null),
	SILVER(0, 0, 2, Improvement.MINE, null),
	SUGAR(0, 0, 2, Improvement.PLANTATION, null);
	
	public final int food;
	public final int production;
	public final int gold;
	public final Improvement requiredImprovement;
	public final Technology requiredTechnology;
	
	ResourceType(int food, int production, int gold, Improvement requiredImprovement, Technology requiredTechnology)
	{
		this.food = food;
		this.production = production;
		this.gold = gold;
		this.requiredImprovement = requiredImprovement;
		this.requiredTechnology = requiredTechnology;
	}
}















