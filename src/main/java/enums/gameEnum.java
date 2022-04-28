package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum gameEnum
{
    startGame("^\\s*play\\s+game.*$"),
    newPlayer("^ --player(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),
    shortNewPlayer("^ -p(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),

    //messages
    numberOfPlayers("invalid number of players"),
    lessThanFour("please pick less than 4 players:)"),
    playerExist("player doesn't exist"),
    successfulStartGame("game started"),
    currentMenu("Game Menu"),
    loggedInPlayerInCandidates("you can not play with yourself!");

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
