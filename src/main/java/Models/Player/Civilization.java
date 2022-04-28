package Models.Player;

public enum Civilization
{
	AMERICAN("Washington"),
	ARABIAN("Harun al-Rashid"),
	ASSYRIAN("Ashurbanipal"),
	CHINESE("Wu Zetian"),
	GERMAN("Bismarck"),
	GREEK("Alexander"),
	MAYAN("Pacal"),
	PERSIAN("Darius I"),
	OTTOMAN("Suleiman"),
	RUSSIAN("Catherine");


	
	public final String leaderName;
	
	Civilization(String leaderName)
	{
		this.leaderName = leaderName;
	}
}
