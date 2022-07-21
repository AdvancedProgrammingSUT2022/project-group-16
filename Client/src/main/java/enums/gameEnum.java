package enums;


import Models.Player.Civilization;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum gameEnum {
    startGame("^\\s*[pP]lay\\s+[gG]ame.*$"),
    newPlayer("^ --player(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),
    shortNewPlayer("^ -p(?<number>[0-9]+)(\\s+)(?<username>\\S+).*$"),
    next("^\\s*[nN]ext\\s*$"),
    end("^\\s*[eE]nd\\s*$"),
    buildUnit("^\\s*[bB]uild\\s+[uU]nit\\s*$"),
    //    lockCitizenToTile("^\\s*[lL]ock\\s+[cC]itizen\\s+[tT]o\\s+[tT]ile\\s+-c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),
    lockCitizenToTile("(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),
    unLockCitizenToTile("^\\s*[uU]nlock\\s+[cC]itizen\\s+[fF]rom\\s+[tT]ile\\s+-c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),

    //messages
    invalidCommand("invalid command"),
    invalidCoordinate("invalid coordinates"),
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
    currentConstruction("current construction: "),
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
    fogOfWar("this tile is fog of war"),
    isFortifyTilHeal("unit already fortified til heal"),
    awaken("unit is awake"),
    nonSelect("nothing has been selected"),
    buyTile("tile bought successfully"),
    cantBuyTile("this tile is too far"),
    belongToCivilization("this tile belongs to another player"),
    notEnoughGold("your gold is not enough"),
    unitBought("unit bought successfully"),
    wrongName("wrong unit name"),
    notYourTile("not your tile"),
    notYourCivilization("not your civilization"),
    successfulBuild("build successfully"),
    noEmptyTile("there is no empty tile"),
    noUnemployed("no unemployed citizen"),
    successfulLock("citizen locked on the tile successfully"),
    farTile("the tile is far from city"),
    anotherCitizenWorking("another citizen is working in tile"),
    noCitizenHere("there is no citizen in this tile"),
    removeFromWork("citizen removed from work successfully"),

    //file path
    filePath("src/main/java/database/savedGames.json");;

    public final String regex;

    gameEnum(String regex) {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, gameEnum regex) {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if (matcher.matches())
            return matcher;
        return null;
    }
}