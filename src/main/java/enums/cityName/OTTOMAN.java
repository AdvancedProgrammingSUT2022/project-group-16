package enums.cityName;

public enum OTTOMAN
{
    ISTANBUL("Istanbul"),
    BURSA("Bursa"),
    GELIBOLU("Gelibolu"),
    EDIRNE("Edirne"),
    IZMIR("Izmir"),
    ALEPPO("Aleppo");

    public final String cityName;

    OTTOMAN(String cityName)
    {
        this.cityName = cityName;
    }
}
