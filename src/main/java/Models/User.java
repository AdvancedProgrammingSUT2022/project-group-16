package Models;

import Models.chat.Message;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

public class User
{
	private final String username;
	private String nickname;
	private String password;
	private URL photo;
	private long lastTimeOfWin;
	private String lastLogin;
	private int score = 0;
	private HashMap<String, ArrayList<Message>> privateChats = new HashMap<>();

	public User(String username, String nickname, String password, URL photo) {
		this.username = username;
		this.nickname = nickname;
		this.password = password;
		this.photo = photo;
	}
	
	public String getUsername()
	{
		return username;
	}
	public String getNickname()
	{
		return nickname;
	}
	public void setNickname(String nickname)
	{
		this.nickname = nickname;
	}
	public String getPassword()
	{
		return password;
	}
	public void setPassword(String password)
	{
		this.password = password;
	}
	public URL getPhoto() {
		return photo;
	}
	public void setPhoto(URL photo) {
		this.photo = photo;
	}
	public long getLastTimeOfWin() {
		return lastTimeOfWin;
	}
	public void setLastTimeOfWin(long lastTimeOfWin) {
		this.lastTimeOfWin = lastTimeOfWin;
	}
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	public int getScore()
	{
		return score;
	}
	public void setScore(int score)
	{
		this.score = score;
	}

	public HashMap<String, ArrayList<Message>> getPrivateChats() {
		return privateChats;
	}
}
