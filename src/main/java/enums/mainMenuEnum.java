package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mainMenuEnum
{
    //commands
    enterMenu("^\\s*menu\\s+enter\\s+(?<menuName>.+)\\s*$"),
    profileName("^\\s*[pP]rofile\\s+[mM]enu\\s*$"),
    startNewGame("^\\s*[sS]tart\\s+[nN]ew\\s+[gG]ame\\s*$"),
    menuExit("^\\s*menu\\s+exit\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$"),
    logoutUser("^\\s*user\\s+logout\\s*$"),

    //messages
    currentMenu("Main Menu"),
    invalidCommand("invalid command"),
    successfulLogout("user logged out successfully!");

    public final String regex;

    mainMenuEnum(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, mainMenuEnum regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}
