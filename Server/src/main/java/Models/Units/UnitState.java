package Models.Units;

public enum UnitState
{
	ACTIVE("ā"),
	SLEEPING("š¤"),
	FORTIFIED("š”"),
	ALERT("š"),
	FORTIFIED_FOR_HEALING("\uD83D\uDC89"),
	IS_SET("šÆ"), // used for siege units
	IS_WORKING("\uD83D\uDEE0ļø"),
	HOSTAGE("\uD83C\uDFF4\u200Dā ļø");
	//TODO: add more states if needed
	
	
	public final String symbol;
	
	UnitState(String symbol)
	{
		this.symbol = symbol;
	}
}
