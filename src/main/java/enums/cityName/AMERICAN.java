package enums.cityName;

public enum AMERICAN
{
    WASHINGTON("Washington D.C."),
    OHIO("Ohio"),
    Utah("Utah"),
    CALIFORNIA("California"),
    MICHIGAN("Michigan"),
    ALASKA("Alaska");

    public final String cityName;

    AMERICAN(String cityName)
    {
        this.cityName = cityName;
    }
}
