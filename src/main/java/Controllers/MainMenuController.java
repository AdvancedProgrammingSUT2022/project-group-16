package Controllers;

import Models.Menu.Menu;
import Views.gameMenuView;
import Views.profileMenuVeiw;
import enums.mainMenuEnum;
import enums.registerEnum;

import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenuController
{
	public static void startNewGame()
	{
		gameMenuView.run();
	}

	public static void enterProfileMenu(Scanner scanner, Matcher matcher)
	{
		profileMenuVeiw.run(scanner, matcher);
	}

	private Boolean doesPlayerExist(String name)
	{
		return false;
	}

	public static int enterMenu(Scanner scanner, Matcher matcher)
	{
		String menuName = matcher.group("menuName");
		if((matcher = mainMenuEnum.compareRegex(menuName, mainMenuEnum.profileName)) != null)
			enterProfileMenu(scanner, matcher);
		else if((matcher = mainMenuEnum.compareRegex(menuName, mainMenuEnum.startNewGame)) != null)
			startNewGame();
		else
			return 1;
		return 0;
	}

	public static String logoutUser()
	{
		Menu.loggedInUser = null;
		return "user logged out successfully!";
	}
}
