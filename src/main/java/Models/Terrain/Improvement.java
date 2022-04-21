package Models.Terrain;

import Models.Player.Technology;
public enum Improvement
{
	NONE(0, 0, 0, null),
	CAMP(0, 0, 0, Technology.TRAPPING),
	FARM(1, 0, 0, Technology.AGRICULTURE),
	LUMBER_MILL(0, 1, 0, Technology.CONSTRUCTION),
	MINE(0, 1, 0, Technology.MINING),
	PASTURE(0, 0, 0, Technology.ANIMAL_HUSBANDRY),
	PLANTATION(0, 0, 0, Technology.CALENDAR),
	QUARRY(0, 0, 0, Technology.MASONRY),
	TRADING_POST(0, 0, 1, Technology.TRAPPING),
	MANUFACTORY(0, 2, 0, Technology.ENGINEERING);
	
	public final int foodYield;
	public final int productionYield;
	public final int goldYield;
	public final Technology requiredTechnology;
	
	Improvement(int foodYield, int productionYield, int goldYield, Technology requiredTechnology)
	{
		this.foodYield = foodYield;
		this.productionYield = productionYield;
		this.goldYield = goldYield;
		this.requiredTechnology = requiredTechnology;
	}
}



























