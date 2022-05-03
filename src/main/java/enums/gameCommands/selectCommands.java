package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum selectCommands
{
    //commands
    selectCombat("^\\s*[sS]elect\\s+[uU]nit\\s+[cC]ombat.*$"),
    selectNonCombat("^\\s*[sS]elect\\s+[uU]nit\\s+[nN]on[cC]ombat.*$"),
    selectCity("^\\s*[sS]elect\\s+[cC]ity.*$"),
    newName("^ --name\\s+(?<name>\\S+)\\s*$"),
    shortNewName("^ -n\\s+(?<name>\\S+)\\s*$"),
    newPos("^ --coordinates\\s+(?<x>-[0,1][0-9]+)\\s+,\\s+(?<y>-[0,1][0-9]+).*$"),
    shortNewPos("^ -c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+).*$"),

    //messages
    invalidCoordinates("invalid coordinates");

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