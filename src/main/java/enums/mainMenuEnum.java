package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mainMenuEnum
{
    enterMenu("^\\s*menu\\s+enter\\s+(?<menuName>.+)\\s*$"),
    profileName("^\\s*Profile\\s+Menu\\s*$"),
    startNewGame("^\\s*start\\s+new\\s+game\\s*$"),
    menuExit("^\\s*menu\\s+exit\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$"),
    logoutUser("^\\s*user\\s+logout\\s*$");

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
