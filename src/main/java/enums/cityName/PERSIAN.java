package enums.cityName;

public enum PERSIAN
{
    TEHRAN("Tehran"),
    TABRIZ("Tabriz"),
    MASHHAD("Mashhad"),
    KORDESTAN("Kordestan"),
    KERMAN("Kerman"),
    ARDEBIL("Ardebil");

    public final String cityName;

    PERSIAN(String cityName)
    {
        this.cityName = cityName;
    }
}
