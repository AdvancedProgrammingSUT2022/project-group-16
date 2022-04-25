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
	}//update arrayList with Json database

	public static void writeDataOnJson()
	{
		try {
			Writer writer = new FileWriter(registerEnum.filePath.regex);
			new Gson().toJson(Menu.allUsers, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//update Json database with arrayList

	private static boolean doesNicknameExist(String nickName)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getNickname().equals(nickName))
				return true;
		return false;
	}

	private static User getUserByUsername(String username)
	{
		for (int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if (Menu.allUsers.get(i).getUsername().equals(username))
				return Menu.allUsers.get(i);
		}
		return null;
	}

	private static boolean isPasswordCorrect(String username, String password)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getUsername().equals(username) && !Menu.allUsers.get(i).getPassword().equals(password))
				return true;
		return false;
	}

	public static String checkWeaknessOfPassword(String password) {
		//check length
		if(password.length() < 5)
			return mainCommands.weakPass.regex;
		//checking weak
		int flag = 0, flag1 = 0, flag2 = 0, flag3 = 0;
		for(int i = 0; i < password.length(); i++) {
			char tmp = password.charAt(i);
			if(flag1 == 0 && (tmp > 64 && tmp < 91)) {
				flag1 = 1;
				flag++;
			}
			if(flag2 == 0 && (tmp > 96 && tmp < 123)) {
				flag2 = 1;
				flag++;
			}
			if(flag3 == 0 && (tmp > 47 && tmp < 58)) {
				flag3 = 1;
				flag++;
			}
		}
		if(flag == 3) return null;
		return mainCommands.weakPass.regex;
	}

	private static Matcher matchRegex(Matcher matcher, String line, mainCommands regex)
	{
		if(matcher == null && (matcher = mainCommands.compareRegex(line, regex)) != null)
			matcher = mainCommands.compareRegex(line, regex);
		return matcher;
	}

	public static String checkLineForRegister(String command)
	{
		Matcher usernameMatcher = null;
		Matcher passwordMatcher = null;
		Matcher nicknameMatcher = null;
		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i);
			usernameMatcher = matchRegex(usernameMatcher, sub, mainCommands.getUsername);
			usernameMatcher = matchRegex(usernameMatcher, sub, mainCommands.shortFormUsername);
			passwordMatcher = matchRegex(passwordMatcher, sub, mainCommands.getPassword);
			passwordMatcher = matchRegex(passwordMatcher, sub, mainCommands.shortFormPassword);
			nicknameMatcher = matchRegex(nicknameMatcher, sub, mainCommands.getNickname);
			nicknameMatcher = matchRegex(nicknameMatcher, sub, mainCommands.shortFormNickname);
		}
		if(usernameMatcher != null && nicknameMatcher != null && passwordMatcher != null)
		{
			return createUser(usernameMatcher.group("username"),
					passwordMatcher.group("password"), nicknameMatcher.group("nickname"));
		}
		else
			return mainCommands.invalidCommand.regex;
	}

	private static String createUser(String username, String password, String nickname)
	{
		if (getUserByUsername(username) != null)
			return (mainCommands.specificUsername.regex + username + mainCommands.alreadyExist.regex);
		else if (RegisterController.doesNicknameExist(nickname))
			return (mainCommands.specificNickname.regex + nickname + mainCommands.alreadyExist.regex);
		else if(checkWeaknessOfPassword(password) != null)
			return mainCommands.weakPass.regex;
		else
		{
			Menu.allUsers.add(new User(username, nickname, password));
			writeDataOnJson();
			return registerEnum.successfulCreate.regex;
		}
	}

	public static String loginPlayer(String command)
	{
		Matcher usernameMatcher = null;
		Matcher passwordMatcher = null;

		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i);
			usernameMatcher = matchRegex(usernameMatcher, sub, mainCommands.getUsername);
			usernameMatcher = matchRegex(usernameMatcher, sub, mainCommands.shortFormUsername);
			passwordMatcher = matchRegex(passwordMatcher, sub, mainCommands.getPassword);
			passwordMatcher = matchRegex(passwordMatcher, sub, mainCommands.shortFormPassword);
		}
		if (usernameMatcher != null && passwordMatcher != null)
		{
			String username = usernameMatcher.group("username");
			String password = passwordMatcher.group("password");
			if(getUserByUsername(username) == null || isPasswordCorrect(username, password))
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