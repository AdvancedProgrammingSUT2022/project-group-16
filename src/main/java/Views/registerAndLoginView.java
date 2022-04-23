package Views;

import Controllers.RegisterController;
import enums.Command;

import java.util.Scanner;
import java.util.regex.Matcher;

public class registerAndLoginView
{
	
	public static void run()
	{
		Scanner scanner = new Scanner(System.in);
		String command;
		Matcher matcher;
		
		while(scanner.hasNextLine())
		{
			command = scanner.nextLine().trim();
			if((matcher = Command.compareRegex(command, Command.enterMenu)) != null)
			{
				System.out.println("menu navigation is not possible");
			}
			else if((matcher = Command.compareRegex(command, Command.menuExit)) != null)
				break;
			else if((matcher = Command.compareRegex(command, Command.showCurrentMenu)) != null)
			{
				System.out.println("Login Menu");
			}
			else if((matcher = Command.compareRegex(command, Command.registerUser)) != null)
			{
				System.out.println(RegisterController.createUser(matcher.group("username"), matcher.group("password"), matcher.group("nickname")));
			}
			else if((matcher = Command.compareRegex(command, Command.loginUser)) != null)
			{
				if(RegisterController.loginPlayer(matcher.group("username"), scanner, matcher) != null)
				{
					System.out.println(RegisterController.loginPlayer(matcher.group("username"), scanner, matcher));
				}
				else
				{
					System.out.println("user logged in successfully!");
					mainMenuVeiw.run(scanner, matcher);
				}
			}
			else
			{
				System.out.println("invalid command");
			}
		}
	}
}
