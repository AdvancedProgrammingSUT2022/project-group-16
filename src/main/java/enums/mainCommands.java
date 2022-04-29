package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum mainCommands
{
    menuExit("^\\s*menu\\s+exit\\s*$"),
    enterMenu("^\\s*menu\\s+enter\\s+(?<menuName>.+)\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$"),
    getUsername("^--username\\s+(?<username>\\S+).*$"),
    getNickname("^--nickname\\s+(?<nickname>\\S+).*$"),
    getPassword("^--password\\s+(?<password>\\S+).*$"),
    getOldPassword("^--current\\s+(?<password>\\S+).*$"),
    profileName("^\\s*[pP]rofile\\s+[mM]enu\\s*$"),
    startNewGame("^\\s*[sS]tart\\s+[nN]ew\\s+[gG]ame\\s*$"),
    mainMenu("^\\s*[mM]ain\\s+[mM]enu\\s*$"),
    loginMenu("^\\s*[lL]ogin\\s+[mM]enu\\s*$"),
    getNewPassword("^--new\\s+(?<password>\\S+).*$"),
    shortFormPassword("^-p\\s+(?<password>\\S+).*$"),
    shortFormUsername("^-u\\s+(?<username>\\S+).*$"),
    shortFormNickname("^-n\\s+(?<nickname>\\S+).*$"),

    //messsages
    invalidCommand("invalid command"),
    endGame("game has ended!"),
    alreadyExist(" already exists"),
    specificNickname("user with nickname "),
    specificUsername("user with username "),
    weakPass("password is weak"),
    weakNewPass("new password is weak"),
    navigationError("menu navigation is not possible"),
    nothing("nothing"),
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