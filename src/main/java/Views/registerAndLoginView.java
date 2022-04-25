package Views;

import Controllers.RegisterController;
import enums.mainCommands;
import enums.registerEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class registerAndLoginView
{

	public static void run()
	{
		RegisterController.updateDatabase(); //update arraylist of users and get old users
		Scanner scanner = new Scanner(System.in);
		String command;
		Matcher matcher;

		while(scanner.hasNextLine())
		{
			command = scanner.nextLine().trim();
			if((matcher = registerEnum.compareRegex(command, registerEnum.enterMenu)) != null)
				System.out.println(registerEnum.loginFirst.regex);
			else if((matcher = mainCommands.compareRegex(command, mainCommands.menuExit)) != null)
			{
				scanner.close();
				break;
			}
			else if((matcher = mainCommands.compareRegex(command, mainCommands.showCurrentMenu)) != null)
				System.out.println(registerEnum.currnetMenu.regex);
			else if((matcher = registerEnum.compareRegex(command, registerEnum.registerUser)) != null)
				System.out.println(RegisterController.checkLineForRegister(command));
			else if((matcher = registerEnum.compareRegex(command, registerEnum.loginUser)) != null)
			{
				if(RegisterController.loginPlayer(command) != null)
					System.out.println(RegisterController.loginPlayer(command));
				else
				{
					System.out.println(registerEnum.successfulLogin.regex);
					mainMenuVeiw.run(scanner, matcher);
				}
			}
			else
				System.out.println(mainCommands.invalidCommand.regex);
		}
	}
}
