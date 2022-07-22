import IO.Request;
import IO.Response;
import com.google.gson.Gson;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class MultiplayerMenuView
{
	Socket socket;
	DataInputStream dataInputStream;
	DataOutputStream dataOutputStream;
	Gson gson = new Gson();

	public void run()
	{
		Runnable getInputFromClientRunnable = new Runnable()
		{
			Scanner scanner = new Scanner(System.in);
			@Override
			public void run()
			{
				String input;
				String[] inputTokens;

				while (true)
				{
					input = scanner.nextLine();
					inputTokens = input.split(" +");

					if(input.startsWith("new room"))
						newRoomRequest(inputTokens[2]);
					else if(input.startsWith("join room"))
						joinRoomRequest(inputTokens[2]);
					else if(input.equals("ls join requests"))
						listJoinRequests(scanner);
					else if(input.equals("start game"))
						startGame();
				}
			}
		};
		Thread getInputFromClientThread = new Thread(getInputFromClientRunnable);
		getInputFromClientThread.start();
	}

	private void newRoomRequest(String roomID)
	{
		Request request = new Request();
		request.setAction("new room");
		request.addParam("roomID", roomID);

		try
		{
			dataOutputStream.writeUTF(gson.toJson(request));
			dataOutputStream.flush();
			Response response = gson.fromJson(dataInputStream.readUTF(), Response.class);
			System.out.println(response.getMassage());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	private void listJoinRequests(Scanner scanner)
	{
		Request request = new Request();
		request.setAction("get join requests");
		try
		{
			dataOutputStream.writeUTF(gson.toJson(request));
			dataOutputStream.flush();
			Response response = gson.fromJson(dataInputStream.readUTF(), Response.class);
			if(((String)response.getParams().get("join requests")).length() == 0)
				System.out.println("there is no join request!");
			else
			{
				System.out.println(response.getParams().get("join requests"));

				String input = scanner.nextLine();
				if(input.equals("back"))
					return;

				request = new Request();
				if(input.startsWith("accept"))
					request.setAction("accept join request");
				else if(input.startsWith("reject"))
					request.setAction("reject join request");
				request.addParam("index", input.substring(7));

				dataOutputStream.writeUTF(gson.toJson(request));
				dataOutputStream.flush();

				response = gson.fromJson(dataInputStream.readUTF(), Response.class);
				System.out.println(response.getMassage());
			}
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	private void startGame()
	{
		Request request = new Request();
		request.setAction("start game");
		try
		{
			dataOutputStream.writeUTF(gson.toJson(request));
			dataOutputStream.flush();
			Response response = new Response();
			response = gson.fromJson(dataInputStream.readUTF(), Response.class);

			System.out.println(response);

		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private void joinRoomRequest(String roomID)
	{
		Request request = new Request();
		request.setAction("join room");
		request.addParam("roomID", roomID);

		try
		{
			dataOutputStream.writeUTF(gson.toJson(request));
			dataOutputStream.flush();

			Response response = gson.fromJson(dataInputStream.readUTF(), Response.class);
			System.out.println(response.getMassage());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
}
