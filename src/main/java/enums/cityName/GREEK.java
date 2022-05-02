package enums.cityName;

public enum GREEK
{
    ATHENS("Athens"),
    THESSALONIKI("Thessaloniki"),
    CHANIA("Chania"),
    RHODES("Rhodes"),
    PATRAS("Patras");

    public final String cityName;

    GREEK(String cityName)
    {
        this.cityName = cityName;
    }
}
