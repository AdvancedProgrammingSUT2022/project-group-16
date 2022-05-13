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
    buildBuilding("^\\s*[bB]uild\\s+[bB]uilding\\s+-c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),
    buyTile("^\\s*[pP]urchase\\s+[tT]ile\\s+-c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),
    buyUnit("^\\s*[pP]urchase\\s+[uU]nit\\s*$"),

    //messages
    invalidCommand("invalid command"),
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
    goldYield("gold yield: "),
    gold("gold: "),
    goldIncome("gold income: "),
    production("production: "),
    happiness("happiness: "),
    foodYield("food yield: "),
    food("food: "),
    cupYield("cup yield: "),
    cup("cup: "),
    cupIncome("cup income: "),
    health("health: "),
    power("power: "),
    population("population: "),
    employedCitizens("employed citizens: "),
    unEmployedCitizens("unemployed citizens : "),
    speed("speed: "),
    slept("unit slept"),
    productionCost("production cost: "),
    wokeUp("unit woke up"),
    fortifyActive("Fortify activated!"),
    isSleep("unit already is asleep"),
    isAlert("unit already alerted"),
    isFortify("unit already fortified"),
    isFortifyTilHeal("unit already fortified til heal"),
    awaken("unit is awake"),
    nonSelect("nothing has been selected");

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