package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum profileEnum
{
    //commands
    changeNickname("^\\s*[pP]rofile\\s+[cC]hange\\s+--[nN]ickname\\s+(?<newName>\\S+)\\s*$"),
    shortChangeNickname("^\\s*[pP]rofile\\s+[cC]hange\\s+-n\\s+(?<newName>\\S+)\\s*$"),
    changePassword("^\\s*[pP]rofile\\s+[cC]hange\\s+--password.*$"),
    getNewPassword("^ --new\\s+(?<password>\\S+).*$"),
    getOldPassword("^ --current\\s+(?<password>\\S+).*$"),

    //messages
    successfulPassChange("password changed successfully!"),
    successfulNicknameChange("nickname changed successfully!"),
    invalidOldPass("current password is invalid"),
    commonPasswords("please enter a new password"),
    currentMenu("Profile Menu");

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