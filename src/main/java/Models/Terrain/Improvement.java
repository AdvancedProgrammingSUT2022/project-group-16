package Models.Terrain;

import Models.Player.Technology;
public enum Improvement
{
	NONE(),
	CAMP(),
	FARM(),
	LUMBER_MILL(),
	MINE(),
	PASTURE(),
	PLANTATION(),
	QUARRY(),
	TRADING_POST(),
	MANUFACTORY();
	
	public final int turns;
	public final int foodYield;
	public final int productionYield;
	public final int goldYield;
	public final Technology requiredTechnology;
	
	Improvement(int turns, int foodYield, int productionYield, int goldYield, Technology requiredTechnology)
	{
		this.turns = turns;
		this.foodYield = foodYield;
		this.productionYield = productionYield;
		this.goldYield = goldYield;
		this.requiredTechnology = requiredTechnology;
	}
}



























