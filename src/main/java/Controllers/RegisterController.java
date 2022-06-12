package Controllers;

import Models.Menu.Menu;
import Models.User;
import com.google.gson.*;
import com.sun.jna.platform.win32.WinGDI;
import enums.mainCommands;
import enums.registerEnum;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;

public class RegisterController {

	private final URL guestImage = getClass().getResource("photos/profilePhotos/guest.png");

	public void updateDatabase()
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

	public void writeDataOnJson()
	{
		try {
			Writer writer = new FileWriter(registerEnum.filePath.regex);
			new Gson().toJson(Menu.allUsers, writer);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}//update Json database with arrayList

	private boolean doesNicknameExist(String nickName)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getNickname().equals(nickName))
				return true;
		return false;
	}

	public User getUserByUsername(String username)
	{
		for (int i = 0; i < Menu.allUsers.toArray().length; i++)
		{
			if (Menu.allUsers.get(i).getUsername().equals(username))
				return Menu.allUsers.get(i);
		}
		return null;
	}

	public boolean isPasswordCorrect(String username, String password)
	{
		for (int i = 0; i < Menu.allUsers.size(); i++)
			if (Menu.allUsers.get(i).getUsername().equals(username) && !Menu.allUsers.get(i).getPassword().equals(password))
				return true;
		return false;
	}

	public String checkWeaknessOfPassword(String password) {
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

	private Matcher matchRegex(Matcher matcher, String line, registerEnum regex)
	{
		if(matcher == null && (matcher = registerEnum.compareRegex(line, regex)) != null)
			matcher = registerEnum.compareRegex(line, regex);
		return matcher;
	}

	public String checkLineForRegister(String command) throws MalformedURLException {
		Matcher usernameMatcher = null;
		Matcher passwordMatcher = null;
		Matcher nicknameMatcher = null;
		for(int i = 0; i < command.length(); i++)
		{
			String sub = command.substring(i);
			usernameMatcher = matchRegex(usernameMatcher, sub, registerEnum.getUsername);
			usernameMatcher = matchRegex(usernameMatcher, sub, registerEnum.shortFormUsername);
			passwordMatcher = matchRegex(passwordMatcher, sub, registerEnum.getPassword);
			passwordMatcher = matchRegex(passwordMatcher, sub, registerEnum.shortFormPassword);
			nicknameMatcher = matchRegex(nicknameMatcher, sub, registerEnum.getNickname);
			nicknameMatcher = matchRegex(nicknameMatcher, sub, registerEnum.shortFormNickname);
		}
		if(usernameMatcher != null && nicknameMatcher != null && passwordMatcher != null)
		{
			return createUser(usernameMatcher.group("username"),
					passwordMatcher.group("password"), nicknameMatcher.group("nickname"), guestImage);
		}
		else
			return mainCommands.invalidCommand.regex;
	}

	public String createUser(String username, String password, String nickname, URL photo) {
		if (getUserByUsername(username) != null)
			return (mainCommands.specificUsername.regex + username + mainCommands.alreadyExist.regex);
		else if (doesNicknameExist(nickname))
			return (mainCommands.specificNickname.regex + nickname + mainCommands.alreadyExist.regex);
		else if(checkWeaknessOfPassword(password) != null)
			return mainCommands.weakPass.regex;
		else
		{
			Menu.allUsers.add(new User(username, nickname, password, photo));
			writeDataOnJson();
			return registerEnum.successfulCreate.regex;
		}
	}

	public String loginPlayer(String command)
	{
		Matcher usernameMatcher = null;
		Matcher passwordMatcher = null;

		for(int i = 0; i < command.length(); i++)
		{
			usernameMatcher = matchRegex(usernameMatcher, command.substring(i), registerEnum.getUsername);
			usernameMatcher = matchRegex(usernameMatcher, command.substring(i), registerEnum.shortFormUsername);
			passwordMatcher = matchRegex(passwordMatcher, command.substring(i), registerEnum.getPassword);
			passwordMatcher = matchRegex(passwordMatcher, command.substring(i), registerEnum.shortFormPassword);
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