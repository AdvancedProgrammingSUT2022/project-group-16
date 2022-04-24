package Controllers;

import Models.Menu.Menu;
import Models.User;
import com.google.gson.*;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;

public class RegisterController {

	public static void updateDatabase()
	{
		try {
			Gson gson = new Gson();
			String arr = (new BufferedReader(new FileReader("src/main/java/Database/usersDatabase.json"))).readLine();
			if(arr != null)
			{
				arr = arr.substring(1,arr.length() - 1);
				String[] splitedArr = arr.split("},");
				for(int i = 0; i < splitedArr.length; i++)
				{
					if(i != splitedArr.length - 1){
						splitedArr[i] += "}";
					}
					Menu.allUsers.add(gson.fromJson(splitedArr[i], User.class));
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeDataOnJson()
	{
		try {
			Writer writer = new FileWriter("src/main/java/Database/usersDatabase.json");
			new Gson().toJson(Menu.allUsers, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static boolean doesUsernameExist(String username)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getUsername().equals(username))
				return true;
		return false;
	}

	public static boolean doesNicknameExist(String nickName)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getNickname().equals(nickName))
				return true;
		return false;
	}

	public static User getUserByUsername(String username)
	{
		for (int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if (Menu.allUsers.get(i).getUsername().equals(username))
			{
				return Menu.allUsers.get(i);
			}
		}
		return null;
	}

	public static String createUser(String username, String password, String nickname)
	{
		if (doesUsernameExist(username))
			return "user with username " + username + " already exists";
		else if (RegisterController.doesNicknameExist(nickname))
			return "user with nickname " + nickname + " already exists";
		else
		{
			Menu.allUsers.add(new User(username, nickname, password));
			writeDataOnJson();
			return "user created successfully!";
		}
	}

	public static boolean isPasswordCorrect(String username, String password)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getUsername().equals(username) && !Menu.allUsers.get(i).getPassword().equals(password))
				return true;
		return false;
	}

	public static String loginPlayer(String username, Scanner scanner, Matcher matcher)
	{
		if (!RegisterController.doesUsernameExist(matcher.group("username"))
				|| RegisterController.isPasswordCorrect(matcher.group("username"), matcher.group("password")))
			return "Username and password didn't match!";
		else
		{
			Menu.loggedInUser = getUserByUsername(matcher.group("username"));
			return null;
		}
	}
}