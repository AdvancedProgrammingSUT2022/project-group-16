package enums.gameCommands;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum unitCommands
{
    //commands

    //messages
    moveTo("1: move to "),
    sleep("2: sleep"),
    alert("3: alert"),
    fortify("4: fortify"),
    fortifyUntilHeal("5: fortify until heal"),
    garrison("6: garrison"),
    setup("7: setup"),
    attack("8: attack"),
    found("9: found city"),
    cancle("10: cancel mission"),
    wake("11: wake"),
    delete("12: delete"),
    build("13: build (note: only workers)"),
    remove("14: remove (note: only workers)"),
    repair("15: repair (note: only workers)"),
    jungle("1: jungle"),
    route("2: route"),
    position("position:"),
    x("X: "),
    y("Y: "),
    road("1: road"),
    railRoad("2: rail road"),
    farm("3: farm"),
    mine("4: mine"),
    tradingPost("5: trading post"),
    lumberMill("6: lumber mill"),
    pasture("7: pasture"),
    camp("8: camp"),
    plantation("9: plantation"),
    quarry("10: quarry");

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