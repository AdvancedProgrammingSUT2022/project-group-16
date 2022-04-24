package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum profileEnum
{
    changeNickname("^\\s*profile\\s+change\\s+--nickname\\s+(?<newNickname>\\S+)$"),
    changePassword("^\\s*profile\\s+change\\s+--password\\s+--current\\s+(?<currentPassword>.+)\\s+--new\\s+(?<newPassword>.+)\\s*$"),
    menuExit("^\\s*menu\\s+exit\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$");

    public final String regex;

    profileEnum(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, profileEnum regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}