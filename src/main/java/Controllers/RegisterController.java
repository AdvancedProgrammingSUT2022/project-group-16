package Controllers;

import Models.Menu.Menu;
import Models.User;
import com.google.gson.*;
import enums.mainCommands;
import enums.registerEnum;

import java.io.*;
import java.util.Scanner;
import java.util.regex.Matcher;

public class RegisterController {

	public static void updateDatabase()
	{
		try {
			Gson gson = new Gson();
			String arr = (new BufferedReader(new FileReader(registerEnum.filePath.regex))).readLine();
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
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void writeDataOnJson()
	{
		try {
			Writer writer = new FileWriter(registerEnum.filePath.regex);
			new Gson().toJson(Menu.allUsers, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
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
				return Menu.allUsers.get(i);
		}
		return null;
	}

	public static String checkLineForRegister(Matcher usernameMatcher, Matcher nicknameMatcher,
											   Matcher passwordMatcher, String command)
	{
		usernameMatcher = null;
		passwordMatcher = null;
		nicknameMatcher = null;
		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i, command.length());
			if(usernameMatcher == null && (usernameMatcher = mainCommands.compareRegex(sub, mainCommands.getUsername)) != null)
				usernameMatcher = mainCommands.compareRegex(sub, mainCommands.getUsername);

			else if(nicknameMatcher == null && (nicknameMatcher = mainCommands.compareRegex(sub, mainCommands.getNickname)) != null)
				nicknameMatcher = mainCommands.compareRegex(sub, mainCommands.getNickname);

			else if(passwordMatcher == null && (passwordMatcher = mainCommands.compareRegex(sub, mainCommands.getPassword)) != null)
				passwordMatcher = mainCommands.compareRegex(sub, mainCommands.getPassword);
		}
		if(usernameMatcher != null && nicknameMatcher != null && passwordMatcher != null)
		{
			return RegisterController.createUser(usernameMatcher.group("username"),
					passwordMatcher.group("password"), nicknameMatcher.group("nickname"));
		}
		else
			return mainCommands.invalidCommand.regex;
	}

	public static String createUser(String username, String password, String nickname)
	{
		if (getUserByUsername(username) != null)
			return (mainCommands.specificUsername.regex + username + mainCommands.alreadyExist.regex);
		else if (RegisterController.doesNicknameExist(nickname))
			return (mainCommands.specificNickname.regex + nickname + mainCommands.alreadyExist.regex);
		else
		{
			Menu.allUsers.add(new User(username, nickname, password));
			writeDataOnJson();
			return registerEnum.successfulCreate.regex;
		}
	}

	public static boolean isPasswordCorrect(String username, String password)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getUsername().equals(username) && !Menu.allUsers.get(i).getPassword().equals(password))
				return true;
		return false;
	}

	public static String loginPlayer(Matcher usernameMatcher, Matcher passwordMatcher, String command)
	{
		usernameMatcher = null;
		passwordMatcher = null;

		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i, command.length());
			if(usernameMatcher == null && (usernameMatcher = mainCommands.compareRegex(sub, mainCommands.getUsername)) != null)
				usernameMatcher = mainCommands.compareRegex(sub, mainCommands.getUsername);
			else if(passwordMatcher == null && (passwordMatcher = mainCommands.compareRegex(sub, mainCommands.getPassword)) != null)
				passwordMatcher = mainCommands.compareRegex(sub, mainCommands.getPassword);
		}
		if (usernameMatcher != null && passwordMatcher != null)
		{
			String username = usernameMatcher.group("username");
			String password = passwordMatcher.group("password");
			if(RegisterController.getUserByUsername(username) == null
					|| RegisterController.isPasswordCorrect(username, password))
				return registerEnum.doesNotMatchuserAndPass.regex;
			else
			{
				Menu.loggedInUser = getUserByUsername(username);
				return null;
			}
		}
		else
			return mainCommands.invalidCommand.regex;
	}
}