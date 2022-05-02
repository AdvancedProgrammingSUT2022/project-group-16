package enums.cityName;

public enum CHINESE
{
    BEIJING("Beijing"),
    SHANGHAI("Shanghai"),
    CHENGDU("Chengdu"),
    WUHAN("Wuhan"),
    XIAN("Xi An");

    public final String cityName;

    CHINESE(String cityName)
    {
        this.cityName = cityName;
    }
}
