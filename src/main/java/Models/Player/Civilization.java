package Models.Player;

public enum Civilization
{
	AMERICAN("Washington", new String[]{"Washington D.C.", "Ohio", "Utah", "California", "Michigan", "Alaska"}),
	ARABIAN("Harun al-Rashid", new String[]{"Jeddah", "Medina", "Mecca", "Riyadh", "Taif"}),
	ASSYRIAN("Ashurbanipal", new String[]{"Assur", "Nimrud", "Kalishin", "Jilu", "Mavana", "Tyari"}),
	CHINESE("Wu Zetian", new String[]{"Beijing", "Shanghai", "Chengdu", "Wuhan", "Xi An"}),
	GERMAN("Bismarck", new String[]{"Winden", "Berlin", "Munich", "Frankfurt", "Hamburg", "Nuremberg"}),
	GREEK("Alexander", new String[]{"Athens", "Thessaloniki", "Chania", "Rhodes", "Patras"}),
	MAYAN("Pacal", new String[]{"Tikal", "Palenque", "Copan", "Banampak", "Yaxchilan"}),
	PERSIAN("Darius I", new String[]{"Istanbul", "Bursa", "Gelibolu", "Edirne", "Izmir", "Aleppo"}),
	OTTOMAN("Suleiman", new String[]{"Tehran", "Tabriz", "Mashhad", "Kordestan", "Kerman", "Ardebil"}),
	RUSSIAN("Catherine", new String[]{"Moscow", "Saint Petersburg", "Samara", "Kazan", "Omsk", "Ozersk"});

	public final String leaderName;
	public String[] cities;
	
	Civilization(String leaderName, String[] cities)
	{
		this.leaderName = leaderName;
		this.cities = cities;
	}
}
