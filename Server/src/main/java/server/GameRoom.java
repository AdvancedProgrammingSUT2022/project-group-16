package server;

import IO.RequestHandler;
import Models.User;

import java.util.ArrayList;

public class GameRoom
{
	private RequestHandler roomAdmin;
	private ArrayList<RequestHandler> joinedClients;
	private final String roomID;
	private ArrayList<RequestHandler> joinRequests = new ArrayList<>();

	public GameRoom(RequestHandler roomAdmin, String roomID)
	{
		this.roomAdmin = roomAdmin;
		this.roomID = roomID;
		joinedClients = new ArrayList<>();
	}

	public RequestHandler getRoomAdmin()
	{
		return roomAdmin;
	}
	public String getRoomID()
	{
		return roomID;
	}
	public ArrayList<RequestHandler> getJoinedClients()
	{
		return joinedClients;
	}
	public void addToJoinedClients(RequestHandler requestHandler)
	{
		joinedClients.add(requestHandler);
	}
	public void removeFromJoinedRequests(RequestHandler requestHandler)
	{
		joinRequests.remove(requestHandler);
	}

	public ArrayList<RequestHandler> getJoinRequests()
	{
		return joinRequests;
	}
	public void addToJoinRequests(RequestHandler joinRequest)
	{
		this.joinRequests.add(joinRequest);
	}
}

















