package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mapCommands
{
    //comamnds

    //messages
    show("1: show"),
    move("2: move"),
    byPosition("1: by position"),
    byCityName("2: by city name"),
    Right("1: right"),
    Left("2: left"),
    Up("3: up"),
    Down("4: down"),
    numberOfMoves("number of moves: ");

    public final String regex;

    mapCommands(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, mapCommands regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}