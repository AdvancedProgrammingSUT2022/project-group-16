package Controllers;

import Models.Menu.Menu;
import enums.mainCommands;
import enums.mainMenuEnum;
import server.GameRoom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;

public class MainMenuController
{
	public static ArrayList<GameRoom> gameRooms = new ArrayList<>();


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
			if(gameRoom.getRoomAdmin().getUser().getUsername().equals(roomAdminUsername))
				return gameRoom;
		return null;
	}
	public static void addToGameRooms(GameRoom gameRoom)
	{
		gameRooms.add(gameRoom);
	}
}
