package Models.Resources;

import Models.Terrain.Improvement;
import Models.Player.Technology;
public enum ResourceType
{
	NONE(),
	
	BANANA(),
	CATTLE(),
	DEER(),
	SHEEP(),
	WHEAT(),
	
	COAL(),
	HORSES(),
	IRON(),

	COTTON(),
	DYES(),
	FURS(),
	GEMS(),
	GOLD(),
	INCENSE(),
	IVORY(),
	MARBLE(),
	SILK(),
	SILVER(),
	SUGAR();
	
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















