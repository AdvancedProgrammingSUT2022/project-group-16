package Models.Terrain;

import Models.Player.Technology;
public enum Improvement
{
	NONE(0, 0, 0, null, "  "),
	CAMP(0, 0, 0, Technology.TRAPPING, "\uD83C\uDFD5"),
	FARM(1, 0, 0, Technology.AGRICULTURE, "\uD83D\uDE9C"),
	LUMBER_MILL(0, 1, 0, Technology.CONSTRUCTION, "\uD83E\uDE93"),
	MINE(0, 1, 0, Technology.MINING, "⛏️"),
	PASTURE(0, 0, 0, Technology.ANIMAL_HUSBANDRY, "\uD83D\uDC15"),
	PLANTATION(0, 0, 0, Technology.CALENDAR, "\uD83C\uDF31"),
	QUARRY(0, 0, 0, Technology.MASONRY, "??"),
	TRADING_POST(0, 0, 1, Technology.TRAPPING, "??"),
	MANUFACTORY(0, 2, 0, Technology.ENGINEERING, "\uD83C\uDFED");
	
	public final int foodYield;
	public final int productionYield;
	public final int goldYield;
	public final Technology requiredTechnology;
	public final String symbol;
	
	Improvement(int foodYield, int productionYield, int goldYield, Technology requiredTechnology, String symbol)
	{
		this.foodYield = foodYield;
		this.productionYield = productionYield;
		this.goldYield = goldYield;
		this.requiredTechnology = requiredTechnology;
		this.symbol = symbol;
	}
}



























