package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum Command
{
	enterMenu("^menu\\s+enter\\s+(?<menuName>\\S+)$"),
	registerUser("^user\\s+create\\s+--username\\s+(?<username>\\S+)\\s+--nickname\\s+(?<nickname>\\S+)\\s+--password\\s+(?<password>\\S+)$"),
	loginUser("^user\\s+login\\s+--username\\s+(?<username>\\S+)\\s+--password\\s+(?<password>\\S+)$"),
	menuExit("^\\s*menu\\s+exit\\s*$"),
	showCurrentMenu("^\\s*menu\\s+show-current\\s*$");
	
	public final String regex;
	
	Command(String regex)
	{
		this.regex = regex;
	}
	
	public static Matcher compareRegex(String command, Command regex)
	{
		Matcher matcher = Pattern.compile(regex.regex).matcher(command);
		if(matcher.matches())
			return matcher;
		return null;
	}
}
