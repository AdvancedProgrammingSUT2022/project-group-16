package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum cheatCode
{
    //commands
    increaseTurns("^\\s*increase\\s+turn\\s+(?<amount>[0-9]+)\\s*$"),
    increaseGold("^\\s*increase\\s+gold\\s+(?<amount>[0-9]+)\\s*$"),
    killEnemyUnit("^\\s*kill\\s+enemy\\s+unit\\s+in\\s+-c\\s+(?<positionX>[0-9]+)\\s*,\\s*(?<positionY>[0-9]+)\\s*$"),
    gainFood("^\\s*increase\\s+food\\s+(?<amount>[0-9]+)\\s*$"),
    gainTechnology("^\\s*add\\s+technology\\s+(?<name>\\S+)\\s*$"),
    increaseHappiness("^\\s*increase\\s+happiness\\s+(?<amount>[0-9]+)\\s*$"),
    increaseScore("^\\s*increase\\s+score\\s+(?<amount>[0-9]+)\\s*$"),
    increaseHealth("hesoyam\\s+-c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*"),
    winGame("^\\s*win\\s+this\\s+game\\s*$"),
    moveUnit("^\\s*move\\s+from\\s+(?<positionX>[0-9]+)\\s+(?<positionY>[0-9]+)\\s+to\\s+(?<newPositionX>[0-9]+)\\s+(?<newPositionY>[0-9]+)\\s*$"),

    //messages
    gold("gold"),
    score("score"),
    turn("turn"),
    food("food"),
    health("health"),
    happiness("happiness"),
    increaseSuccessful(" increased Successfuly"),
    addSuccessful(" added Successfully"),
    unitKilled("Unit killed successfully"),
    youWin("you win!!");

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