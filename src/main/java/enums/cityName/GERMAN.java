package enums.cityName;

public enum GERMAN
{
    BERLIN("Berlin"),
    MUNICH("Munich"),
    FRANKFURT("Frankfurt"),
    HAMBURG("Hamburg"),
    NUREMBERG("Nuremberg");

    public final String cityName;

    GERMAN(String cityName)
    {
        this.cityName = cityName;
    }
}
