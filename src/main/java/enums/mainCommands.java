package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mainCommands
{
    //commands
    menuExit("^\\s*menu\\s+exit\\s*$"),
    enterMenu("^\\s*menu\\s+enter\\s+(?<menuName>.+)\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$"),

    //menu names
    profileName("^\\s*[pP]rofile\\s+[mM]enu\\s*$"),
    startNewGame("^\\s*[sS]tart\\s+[nN]ew\\s+[gG]ame\\s*$"),
    mainMenu("^\\s*[mM]ain\\s+[mM]enu\\s*$"),
    loginMenu("^\\s*[lL]ogin\\s+[mM]enu\\s*$"),

    //messsages
    invalidCommand("invalid command"),
    alreadyExist(" already exists"),
    specificNickname("user with nickname "),
    specificUsername("user with username "),
    weakPass("password is weak"),
    loadImage("please upload a photo for your profile"),
    weakNewPass("new password is weak"),
    navigationError("menu navigation is not possible"),
    and(" and "),
    pickBetween("please pick a number between ");

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