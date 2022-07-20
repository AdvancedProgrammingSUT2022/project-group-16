package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum selectCommands
{
    //commands
    selectCombat("^\\s*[sS]elect\\s+[uU]nit\\s+[cC]ombat.*$"),
    selectNonCombat("^\\s*[sS]elect\\s+[uU]nit\\s+[nN]on\\s{0,1}[cC]ombat.*$"),
    selectCity("^\\s*[sS]elect\\s+[cC]ity.*$"),
    newName("^ --name\\s+(?<name>.+)\\s*$"),
    shortNewName("^ -n\\s+(?<name>.+)\\s*$"),
    newPos("^ --coordinates\\s+(?<x>-{0,1}[0-9]+)\\s*,\\s*(?<y>-{0,1}[0-9]+)\\s*$"),
    shortNewPos("^ -c\\s+(?<x>-{0,1}[0-9]+)\\s*,\\s*(?<y>-{0,1}[0-9]+)\\s*$"),
    buyTile("^\\s*(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),
    buyUnit("^\\s*[pP]urchase\\s+[uU]nit\\s*$"),

    //messages
    invalidRange("please pick numbers between 0 and "),
    nameDoesntExist("There is no city with this name "),
    coordinatesDoesntExistCity("There is no city with this coordinates x = "),
    coordinatesDoesntExistCUnit("There is no combat unit with this coordinates x = "),
    coordinatesDoesntExistNUnit("There is no non combat unit with this coordinates x = "),
    and(" and y = "),
    selected("selected"),
    invalidCommand("invalid command");

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