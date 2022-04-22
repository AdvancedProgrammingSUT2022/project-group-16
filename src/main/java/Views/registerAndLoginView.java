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
			else if(command.equals("menu exit"))
				break;
			else if(command.equals("menu show-current"))
			{
				System.out.println("Login Menu");
			}
			else if((matcher = Command.compareRegex(command, Command.registerUser)) != null)
			{
				if(RegisterController.doesUsernameExist(matcher.group("username")))
				{
					System.out.println("user with username " + matcher.group("username") + " already exists");
				}
				else if(RegisterController.doesNicknameExist(matcher.group("nickname")))
				{
					System.out.println("user with nickname " + matcher.group("nickname") + " already exists");
				}
				else
				{
					System.out.println("user created successfully!");
					RegisterController.createUser(matcher.group("username"), matcher.group("password"), matcher.group("nickname"));
				}
			}
			else if((matcher = Command.compareRegex(command, Command.loginUser)) != null)
			{
				if(!RegisterController.doesUsernameExist(matcher.group("username"))
						|| RegisterController.isPasswordCorrect(matcher.group("username"), matcher.group("password")))
				{
					System.out.println("Username and password didn't match!");
				}
				else
				{
					System.out.println("user logged in successfully!");
					RegisterController.loginPlayer(matcher.group("username"), scanner, matcher);
				}
			}
			else
			{
				System.out.println("invalid command");
			}
		}
	}
}
