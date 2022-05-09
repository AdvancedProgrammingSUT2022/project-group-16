package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum unitCommands
{
    //commands
    moveTo("^\\s*[uU]nit\\s+[mM]ove\\s*[tT]o\\s+-c\\s+(?<x>[0-9]+)\\s*,\\s*(?<y>[0-9]+)\\s*$"),
    sleep("^\\s*[uU]nit\\s+[sS]leep\\s*$"),
    alert("^\\s*[uU]nit\\s+[aA]lert\\s*$"),
    fortify("^\\s*[uU]nit\\s+[fF]ortify\\s*$"),
    fortifyHeal("^\\s*[uU]nit\\s+[fF]ortify\\s+[hH]eal\\s*$"),
    garrison("^\\s*[uU]nit\\s+[gG]arrison\\s*$"),
    setup("^\\s*[uU]nit\\s+[sS]etup\\s+[rR]anged\\s*$"),
    attack("^\\s*[uU]nit\\s+[aA]ttack.*$"),
    foundCity("^\\s*[uU]nit\\s+[fF]ound\\s+[cC]ity\\s*$"),
    cancelMission("^\\s*[uU]nit\\s+[cC]ancel\\s+[mM]ission\\s*$"),
    wake("^\\s*[uU]nit\\s+[wW]ake\\s*$"),
    delete("^\\s*[uU]nit\\s+[dD]elete\\s*$"),
    buildRoad("^\\s*[uU]nit\\s+[bB]uild\\s+[rR]oad\\s*$"),
    buildRailRoad("^\\s*[uU]nit\\s+[bB]uild\\s+[rR]ail\\s*[rR]oad\\s*$"),
    buildFarm("^\\s*[uU]nit\\s+[bB]uild\\s+[fF]arm\\s*$"),
    buildMine("^\\s*[uU]nit\\s+[bB]uild\\s+[mM]ine\\s*$"),
    buildTradingPost("^\\s*[uU]nit\\s+[bB]uild\\s+[tT]rading\\s+[pP]ost\\s*$"),
    buildLumbermill("^\\s*[uU]nit\\s+[bB]uild\\s+[lL]umbermill\\s*$"),
    buildPasture("^\\s*[uU]nit\\s+[bB]uild\\s+[pP]asture\\s*$"),
    buildCamp("^\\s*[uU]nit\\s+[bB]uild\\s+[cC]amp\\s*$"),
    buildPlantation("^\\s*[uU]nit\\s+[bB]uild\\s+[pP]lantation\\s*$"),
    buildQuarry("^\\s*[uU]nit\\s+[bB]uild\\s+[qQ]uarry\\s*$"),
    removeJungle("^\\s*[uU]nit\\s+[rR]emove\\s+[jJ]ungle\\s*$"),
    removeRoute("^\\s*[uU]nit\\s+[rR]emove\\s+[rR]oute\\s*$"),
    repair("^\\s*[uU]nit\\s+[rR]epair\\s*$"),

    //messages
    notYours("selected unit is not for you"),
    notSettler("the selected unit is not settler"),
    cityBuilt("city built"),
    cancelCommand("command canceled"),
    removeUnit("unit removed"),
    gainGold("\nyou got "),
    gold(" gold");

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