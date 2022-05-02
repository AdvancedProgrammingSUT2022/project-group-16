package enums.cityName;

public enum RUSSIAN
{
    MOSCOW("Moscow"),
    SAINTPETERSBURG("Saint Petersburg"),
    SAMARA("Samara"),
    KAZAN("Kazan"),
    OMSK("Omsk"),
    OZERSK("Ozersk");

    public final String cityName;

    RUSSIAN(String cityName)
    {
        this.cityName = cityName;
    }
}
