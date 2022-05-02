package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mapCommands
{
    //comamnds
    mapShow("^\\s*[mM]ap\\s+[sS]how\\s*$"),
    mapMoveRight("^\\s*[mM]ap\\s+[mM]ove\\s+[rR]ight\\s*$"),
    mapMoveLeft("^\\s*[mM]ap\\s+[mM]ove\\s+[lL]eft\\s*$"),
    mapMoveUp("^\\s*[mM]ap\\s+[mM]ove\\s+[uU]p\\s*$"),
    mapMoveDown("^\\s*[mM]ap\\s+[mM]ove\\s+[dD]own\\s*$"),
    newNumber("^ --cells\\s+(?<c>[0-9]+)\\s*$"),
    shortNewNumber("^ -c\\s+(?<c>-[0,1][0-9]+)\\s*$"),
    //messages
    ;

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