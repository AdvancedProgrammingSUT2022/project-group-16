package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum gameEnum
{
    startGame("\\s*play\\s+game.+$"),
    newPlayer("^--player(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),
    menuExit("^\\s*menu\\s+exit\\s*$"),
    showCurrentMenu("^\\s*menu\\s+show-current\\s*$");

    public final String regex;

    gameEnum(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, gameEnum regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}
