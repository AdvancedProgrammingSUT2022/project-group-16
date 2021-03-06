package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum infoCommands {
    //commands
    infoResearch("^\\s*[iI]nfo\\s+[rR]esearch\\s*$"),
    infoTechnologies("^\\s*[iI]nfo\\s+[tT]echnology\\s*$"),
    infoUnits("^\\s*[iI]nfo\\s+[uU]nits\\s*$"),
    infoCities("^\\s*[iI]nfo\\s+[cC]ities\\s*$"),
    infoDemographics("^\\s*[iI]nfo\\s+[dD]emographics\\s*$"),
    infoNotifications("^\\s*[iI]nfo\\s+[nN]otifications\\s*$"),
    infoMilitary("^\\s*[iI]nfo\\s+[mM]ilitary\\s*$"),
    infoEconomic("^\\s*[iI]nfo\\s+[eE]conomic\\s*$"),

    //messages
    searchEconomic(": go to economics!"),
    searchCity(": go to cities!"),
    backToGame(": back to game!"),
    gained("\ngained technologies"),
    cityName("city name: "),
    chooseTechnology("choose a technology to start researching!"),
    nothing("nothing"),
    choose("you selected "),
    successful(" successfuly"),
    successGainTech("you gained technology "),
    enoughCup("you don't have enough cups for "),
    numberOfCup("number of cups: "),
    scoreBoard("Score Board: "),
    willGain("\tyou will gain "),
    requiredTurns("\n\trequired turns: "),
    currResearch(" (current research)"),
    currentResearching("Researching technology: "),
    researchInfo("Research info:\n"),
    remainingTurns("Remaining turns: "),
    gainAfterGetTechnology(" will be unlock after you reach this technology!"),
    notGain("nothing will unlock after you reach this technology"),
    alreadyResearching("you're currently researching this technology!"),
    unitType("unit type: "),
    name("\nname: "),
    civilizationName("civilization name : "),
    cities("City/Cities: "),
    units("\nUnits: "),
    averageScore("Average: "),
    unitName("unit name"),
    sendMessage("Turn: "),
    message("message: "),
    cityGot("you got the city!\nwhat do you want to do?"),
    destroy("1: destroy"),
    attach("2: attach"),
    attachedCities("attached city/cities: "),
    size("territory size: ");

    public final String regex;

    infoCommands(String regex) {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, infoCommands regex) {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if (matcher.matches())
            return matcher;
        return null;
    }
}