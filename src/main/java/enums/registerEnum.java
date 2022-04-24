package enums;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public enum registerEnum
{
	//commands
	enterMenu("^menu\\s+enter\\s+(?<menuName>.+)$"),
	registerUser("^user\\s+create\\s+--username\\s+(?<username>\\S+)\\s+--nickname\\s+(?<nickname>\\S+)\\s+--password\\s+(?<password>\\S+)$"),
	loginUser("^user\\s+login\\s+--username\\s+(?<username>\\S+)\\s+--password\\s+(?<password>\\S+)$"),

	//messages
	loginFirst("please login first"),
	currnetMenu("Login Menu"),
	successfulCreate("user created successfully!"),
	successfulLogin("user logged in successfully!"),
	doesNotMatchuserAndPass("Username and password didn't match!"),

	//filePath
	filePath("src/main/java/Database/usersDatabase.json");

	public final String regex;

	registerEnum(String regex)
	{
		this.regex = regex;
	}

	public static Matcher compareRegex(String command, registerEnum regex)
	{
		Matcher matcher = Pattern.compile(regex.regex).matcher(command);
		if(matcher.matches())
			return matcher;
		return null;
	}
}
