package Views;

import Controllers.RegisterController;
import enums.registerEnum;
import enums.registerEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class registerAndLoginView
{

	public static void run()
	{
		//amir.write();
		Scanner scanner = new Scanner(System.in);
		String command;
		Matcher matcher;

		while(true)
		{
			command = scanner.nextLine().trim();

			if((matcher = registerEnum.compareRegex(command, registerEnum.enterMenu)) != null)
			{
				System.out.println("please login first");
			}
			else if((matcher = registerEnum.compareRegex(command, registerEnum.menuExit)) != null)
				break;
			else if((matcher = registerEnum.compareRegex(command, registerEnum.showCurrentMenu)) != null)
			{
				System.out.println("Login Menu");
			}
			else if((matcher = registerEnum.compareRegex(command, registerEnum.registerUser)) != null)
			{
				System.out.println(RegisterController.createUser(matcher.group("username"), matcher.group("password"), matcher.group("nickname")));
			}
			else if((matcher = registerEnum.compareRegex(command, registerEnum.loginUser)) != null)
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
