package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mapCommands
{
    //comamnds
    mapShow("^\\s*[mM]ap\\s+[sS]how.*$"),
    shortNewNumber("^ -c\\s+(?<c>-{0,1}[0-9]+).*$"),
    newName("^ --name\\s+(?<name>\\S+)\\s*$"),
    shortNewName("^ -n\\s+(?<name>\\S+)\\s*$"),
    newPos("^ --coordinates\\s+(?<x>-{0,1}[0-9]+)\\s*,\\s*(?<y>-{0,1}[0-9]+)\\s*$"),
    shortNewPos("^ -c\\s+(?<x>-{0,1}[0-9]+)\\s*,\\s*(?<y>-{0,1}[0-9]+)\\s*$"),
    visible("you can not see this tile/city"),
    showRawMap("^\\s*show\\s+raw\\s+map\\s*$"),

    //messages
    invalidRange("please pick numbers between 0 and "),
    selected("selected"),
    invalidCommand("invalid command"),
    successful("successful"),
    positiveNum("please enter positive number");

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