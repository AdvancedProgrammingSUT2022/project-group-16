package enums.gameCommands;

import enums.gameEnum;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum infoCommands
{
    //commands

    //messages
    showResearch("1: research"),
    showUnits("2: units"),
    showCities("3: cities"),
    showDiplomacy("4: diplomacy"),
    showVictory("5: victory"),
    showDEMOGRAPHICS("6: demographics"),
    showNOTIFICATIONS("7: notifications"),
    showMILITARY("8: military"),
    showECONOMIC("9: economic"),
    showDIPLOMATIC("10: diplomatic"),
    showDEALS("11: deals");

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