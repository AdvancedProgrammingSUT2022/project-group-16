package Controllers;

import Models.Menu.Menu;
import Models.User;
import Views.mainMenuVeiw;

import java.util.Scanner;
import java.util.regex.Matcher;

public class RegisterController
{
	public static boolean doesUsernameExist(String username)
	{
		for(int i = 0; i < Menu.allUsers.size(); i++)
			if(Menu.allUsers.get(i).getUsername().equals(username))
				return true;
		return false;
	}
	
	public static boolean doesNicknameExist(String nickName)
	{
		for(int i = 0; i < Menu.allUsers.size(); i++)
			if(Menu.allUsers.get(i).getNickname().equals(nickName))
				return true;
		return false;
	}
	
	public static void createUser(String username, String password, String nickname)
	{
		// TODO: check for validation and previous existance
		
		User newUser = new User(username, nickname, password);
		Menu.allUsers.add(newUser);
	}
	
	public static boolean isPasswordCorrect(String username, String password)
	{
		for(int i = 0; i < Menu.allUsers.size(); i++)
			if(Menu.allUsers.get(i).getUsername().equals(username) && !Menu.allUsers.get(i).getPassword().equals(password))
				return true;
		return false;
	}
	
	public static void loginPlayer(String username, Scanner scanner, Matcher matcher)
	{
		mainMenuVeiw.run(scanner, matcher);
	}
	
	public static void logoutPlayer()
	{
	
	}
}
