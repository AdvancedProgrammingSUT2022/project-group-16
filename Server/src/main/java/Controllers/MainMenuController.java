package Controllers;

import Models.Menu.Menu;
import Views.gameMenuView;
import Views.profileMenuVeiw;
import enums.mainCommands;
import enums.mainMenuEnum;
import server.GameRoom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenuController
{
	private static ArrayList<GameRoom> gameRooms = new ArrayList<>();

	public String enterMenu(Scanner scanner, Matcher matcher) throws IOException {
		String menuName = matcher.group("menuName");
		if(mainCommands.compareRegex(menuName, mainCommands.profileName) != null)
		{
			profileMenuVeiw.run(scanner);
			return "1";
		}
		else if(mainCommands.compareRegex(menuName, mainCommands.startNewGame) != null)
		{
			gameMenuView.runGameMenu();
			return "1";
		}
		else if(mainCommands.compareRegex(menuName, mainCommands.loginMenu) != null)
			return mainCommands.navigationError.regex;
		else
			return mainCommands.invalidCommand.regex;
	}

	public String logoutUser()
	{
		Menu.loggedInUser = null;
		return mainMenuEnum.successfulLogout.regex;
	}

	public static GameRoom getRoomByRoomID(String roomID)
	{
		for (GameRoom gameRoom : gameRooms)
			if(gameRoom.getRoomID().equals(roomID))
				return gameRoom;
		return null;
	}
	public static GameRoom getRoomByAdminUsername(String roomAdminUsername)
	{
		for (GameRoom gameRoom : gameRooms)
			if(gameRoom.getRoomAdmin().getUsername().equals(roomAdminUsername))
				return gameRoom;
		return null;
	}
	public static void addToGameRooms(GameRoom gameRoom)
	{
		gameRooms.add(gameRoom);
	}
}
