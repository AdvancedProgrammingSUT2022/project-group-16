package enums.gameCommands;

import enums.gameEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum infoCommands
{
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
    gained("gained technologies"),
    choose("choose a technology to start researching!"),
    nothing("nothing"),
    successful("");


    public final String regex;

    infoCommands(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, infoCommands regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}