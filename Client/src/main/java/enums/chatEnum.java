package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum chatEnum {
    //filePath
    filePath("src/main/java/server/database/publicChat.json");

    public final String regex;

    chatEnum(String regex) {
        this.regex = regex;
    }

    public static Matcher compareRegex(String command, chatEnum regex) {
        Matcher matcher = Pattern.compile(regex.regex).matcher(command);
        if (matcher.matches())
            return matcher;
        return null;
    }
}
