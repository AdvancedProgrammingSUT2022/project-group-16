package Views;

import Controllers.RegisterController;
import enums.mainCommands;
import enums.registerEnum;

import java.util.Scanner;

public class registerAndLoginView
{
	public static void run()
	{
		RegisterController registerController = new RegisterController();
		registerController.updateDatabase(); //update arraylist of users and get old users
		Scanner scanner = new Scanner(System.in);
		String command;

		while(scanner.hasNextLine())
		{
			command = scanner.nextLine().trim();

			if(registerEnum.compareRegex(command, registerEnum.enterMenu) != null)
				System.out.println(registerEnum.loginFirst.regex);
			else if(mainCommands.compareRegex(command, mainCommands.menuExit) != null)
			{
				scanner.close();
				break;
			}
			else if(mainCommands.compareRegex(command, mainCommands.showCurrentMenu) != null)
				System.out.println(registerEnum.currnetMenu.regex);
			else if(registerEnum.compareRegex(command, registerEnum.registerUser) != null)
				System.out.println(registerController.checkLineForRegister(command));
			else if(registerEnum.compareRegex(command, registerEnum.loginUser) != null)
			{
				if(registerController.loginPlayer(command) != null)
					System.out.println(registerController.loginPlayer(command));
				else
				{
					System.out.println(registerEnum.successfulLogin.regex);
					mainMenuVeiw.run(scanner);
				}
			}
			else
				System.out.println(mainCommands.invalidCommand.regex);
		}
	}
}
