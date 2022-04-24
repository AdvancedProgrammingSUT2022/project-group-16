package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mainCommands
{
    menuExit("^\\s*menu\\s+exit\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$"),
    getUsername("^--username\\s+(?<username>\\S+).*$"),
    getNickname("^--nickname\\s+(?<nickname>\\S+).*$"),
    getPassword("^--password\\s+(?<password>\\S+).*$"),

    //messsages
    invalidCommand("invalid command"),
    alreadyExist(" already exists"),
    specificNickname("user with nickname "),
    specificUsername("user with username ");

    public final String regex;

    mainCommands(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, mainCommands regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}