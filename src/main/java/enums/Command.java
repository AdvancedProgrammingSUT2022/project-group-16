package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Command {
    enterMenu("^\\s*menu\\s+enter\\s+(?<menuName>(\\S+))\\s*$"),
    registerUser("^\\s*user\\s+create\\s+--username\\s+(?<username>(\\S+))\\s+--nickname\\s+(?<nickname>(\\S+))\\s+--password\\s+(?<password>(\\S+))\\s*$"),
    loginUser("^\\s*user\\s+login\\s+--username\\s+(?<username>(\\S+))\\s+--password\\s+(?<password>(\\S+))\\s*$");

    public String regex;

    Command(String regex) {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, Command regex) {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if(matcher != null) return matcher;
        return null;
    }
}
