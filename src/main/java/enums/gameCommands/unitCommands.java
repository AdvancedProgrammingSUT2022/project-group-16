package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum unitCommands
{
    //commands
    moveTo("^\\s*[uU]nit\\s+[mM]ove\\s*[tT]o.*$"),
    sleep("^\\s*[sS]leep\\s*$"),
    alert("^\\s*[aA]lert\\s*$"),
    fortify("^\\s*[fF]ortify\\s*$"),
    fortifyHeal("^\\s*[fF]ortify\\s+[hH]eal\\s*$"),
    garrison("^\\s*[gG]arrison\\s*$"),
    setup("^\\s*[fF]ortify\\s+[rR]anged\\s*$"),
    attack("^\\s*[aA]ttack.*$"),
    foundCity("^\\s*[fF]ound\\s+[cC]ity\\s*$"),
    cancelMission("^\\s*[cC]ancel\\s+[mM]ission\\s*$"),
    wake("^\\s*[wW]ake\\s*$"),
    delete("^\\s*[dD]elete\\s*$"),
    buildRoad("^\\s*[bB]uild\\s+[rR]oad\\s*$"),
    buildRailRoad("^\\s*[bB]uild\\s+[rR]ail\\s+[rR]oad\\s*$"),
    buildFarm("^\\s*[bB]uild\\s+[fF]arm\\s*$"),
    buildMine("^\\s*[bB]uild\\s+[mM]ine\\s*$"),
    buildTradingPost("^\\s*[bB]uild\\s+[tT]rading\\s+[pP]ost\\s*$"),
    buildLumbermill("^\\s*[bB]uild\\s+[lL]umbermill\\s*$"),
    buildPasture("^\\s*[bB]uild\\s+[pP]asture\\s*$"),
    buildCamp("^\\s*[bB]uild\\s+[rR]oad\\s*$"),
    buildPlantation("^\\s*[bB]uild\\s+[pP]lantation\\s*$"),
    buildQuarry("^\\s*[bB]uild\\s+[qQ]uarry\\s*$"),
    removeJungle("^\\s*[rR]emove\\s+[jJ]ungle\\s*$"),
    removeRoute("^\\s*[rR]emove\\s+[rR]oute\\s*$"),
    repair("^\\s*[rR]epair\\s*$")

    //messages
    ;

    public final String regex;

    unitCommands(String regex)
    {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, unitCommands regex)
    {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher.matches())
            return matcher;
        return null;
    }
}