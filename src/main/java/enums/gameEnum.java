package enums;

import Models.Player.Civilization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum gameEnum
{
    startGame("^\\s*[pP]lay\\s+[gG]ame.*$"),
    newPlayer("^ --player(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),
    shortNewPlayer("^ -p(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),
    next("^\\s*[nN]ext\\s*$"),
    end("^\\s*[eE]nd\\s*$"),

    //messages
    endGame("game has ended!"),
    numberOfPlayers("invalid number of players"),
    lessThanFour("please pick less than 4 players:)"),
    playerExist("player doesn't exist"),
    successfulStartGame("game started"),
    currentMenu("Game Menu"),
    loggedInPlayerInCandidates("you can not play with yourself!"),
    AMERICAN("1: American," + Civilization.AMERICAN.leaderName),
    ARABIAN("2: ARABIAN," + Civilization.ARABIAN.leaderName),
    ASSYRIAN("3: ASSYRIAN," + Civilization.ASSYRIAN.leaderName),
    CHINESE("4: CHINESE," + Civilization.CHINESE.leaderName),
    GERMAN("5: GERMAN," + Civilization.GERMAN.leaderName),
    GREEK("6: GREEK," + Civilization.GREEK.leaderName),
    MAYAN("7: MAYAN," + Civilization.MAYAN.leaderName),
    PERSIAN("8: PERSIAN," + Civilization.PERSIAN.leaderName),
    OTTOMAN("9: OTTOMAN," + Civilization.OTTOMAN.leaderName),
    RUSSIAN("10: RUSSIAN," + Civilization.RUSSIAN.leaderName),
    pickCivilization(", please pick your Civilization!\n"),
    chooseCivilization("you chose "),
    between1And10("please pick a number between 1 and 10"),
    alreadyPicked("already picked, please choose another one"),
    unit("^\\s*[uU]nit\\s*"),
    turn("'s turn"),
    invalidPos("invalid coordinate"),
    gold("gold: "),
    production("production: "),
    food("food: "),
    cup("cup: "),
    health("health: "),
    power("power: "),
    speed("speed: ");

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
