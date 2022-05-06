package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum cheatCode
{
    //commands
    increaseTurns("^\\s*increase\\s+turn\\s+(?<amount>[0-9]+)\\s*$"),
    increaseGold("^\\s*increase\\s+gold\\s+(?<amount>[0-9]+)\\s*$"),
    winBattle("^\\s*win\\s+this\\s+battle\\s+(?<positionX>[0-9]+)\\s+(?<positionY>[0-9]+)\\s*$"),
    gainFood("^\\s*increase\\s+food\\s+(?<amount>[0-9]+)\\s*$"),
    gainTechnology("^\\s*add\\s+technology\\s+(?<name>\\S+)\\s*$"),
    moveUnit("^\\s*move\\s+from\\s+(?<positionX>[0-9]+)\\s+(?<positionY>[0-9]+)\\s+to\\s+(?<newPositionX>[0-9]+)\\s+(?<newPositionY>[0-9]+)\\s*$"),

    //messages
    gold("gold"),
    turn("turn"),
    food("food"),
    increaseSuccessful(" increased Successfuly"),
    addSuccessful(" added Successfuly");

    public final String regex;

    cheatCode(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, cheatCode regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}