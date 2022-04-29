package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum selectCommands
{
    //commands

    //messages
    unit("1: unit"),
    city("2: city"),
    combat("1: combat"),
    nonCombat("2: non combat"),
    name("1: name"),
    position("2: position"),
    cityName("city name: ");

    public final String regex;

    selectCommands(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, selectCommands regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}