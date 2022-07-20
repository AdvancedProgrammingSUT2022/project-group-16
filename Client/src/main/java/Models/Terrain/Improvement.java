package Models.Terrain;

import Models.Player.Technology;
public enum Improvement
{
	//TODO complete inline turn for ones that are not written in the doc
	NONE(0, 0, 0, null, "  ", 0),
	CAMP(0, 0, 0, Technology.TRAPPING, "\uD83C\uDFD5", 0),
	FARM(1, 0, 0, Technology.AGRICULTURE, "\uD83D\uDE9C", 6),
	LUMBER_MILL(0, 1, 0, Technology.CONSTRUCTION, "\uD83E\uDE93", 0),
	MINE(0, 1, 0, Technology.MINING, "⛏️", 6),
	PASTURE(0, 0, 0, Technology.ANIMAL_HUSBANDRY, "\uD83D\uDC15", 0),
	PLANTATION(0, 0, 0, Technology.CALENDAR, "\uD83C\uDF31", 0),
	QUARRY(0, 0, 0, Technology.MASONRY, "??", 0),
	TRADING_POST(0, 0, 1, Technology.TRAPPING, "??", 0),
	FACTORY(0, 2, 0, Technology.ENGINEERING, "\uD83C\uDFED", 0);
	
	public final int foodYield;
	public final int productionYield;
	public final int goldYield;
	public final Technology requiredTechnology;
	public final String symbol;
	public int inLineTurn;
	public  int turnToConstruct;

	Improvement(int foodYield, int productionYield, int goldYield, Technology requiredTechnology, String symbol, int inLineTurn)
	{
		this.foodYield = foodYield;
		this.productionYield = productionYield;
		this.goldYield = goldYield;
		this.requiredTechnology = requiredTechnology;
		this.symbol = symbol;
		this.inLineTurn = inLineTurn;
	}

}



























