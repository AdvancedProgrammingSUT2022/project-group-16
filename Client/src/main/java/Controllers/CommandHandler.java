package Controllers;

import IO.Client;
import IO.Request;
import IO.Response;
import Models.City.Building;
import Models.City.BuildingType;
import Models.City.City;
import Models.City.Construction;
import Models.Player.Civilization;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Player.TileState;
import Models.Resources.Resource;
import Models.Resources.TradeRequest;
import Models.Terrain.Improvement;
import Models.Terrain.Tile;
import Models.TypeAdapters.*;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.Unit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import javafx.application.Platform;
import javafx.css.Match;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;

public class CommandHandler
{
	private static CommandHandler instance;

	private Gson gson;
	private Socket socket = Client.socket;
	private DataInputStream socketDIS;
	private DataOutputStream socketDOS;
	{
		try
		{
			socketDIS = new DataInputStream(socket.getInputStream());
			socketDOS = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	private Player player;


	private CommandHandler()
	{
		GsonBuilder gsonBuilder = new GsonBuilder();
		gsonBuilder.registerTypeAdapter(Construction.class, new ConstructionTypeAdapter());
		gsonBuilder.registerTypeAdapter(CombatUnit.class, new CUnitTypeAdapter());
		gsonBuilder.registerTypeAdapter(NonCombatUnit.class, new NCUnitTypeAdapter());
		gsonBuilder.registerTypeAdapter(Resource.class, new ResourceTypeAdapter());
		gsonBuilder.registerTypeAdapter(Unit.class, new UnitTypeAdapter());
		gson = gsonBuilder.create();
	}
	public static CommandHandler getInstance()
	{
		if(instance == null)
			instance = new CommandHandler();
		return instance;
	}

	public void setSocket(Socket socket)
	{
		this.socket = socket;
		try
		{
			socketDIS = new DataInputStream(socket.getInputStream());
			socketDOS = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	public void setPlayer(Player player)
	{
		this.player = player;
	}
	public Player getPlayer()
	{
		return player;
	}

	public Player jsonToPlayer(String playerJson)
	{
		Player player = gson.fromJson(playerJson, Player.class);

		// set map
		HashMap<Tile, TileState> map = new HashMap<>();
		for (int i = 0; i < player.mapKeyset.size(); i++)
			map.put(player.mapKeyset.get(i), player.mapValueset.get(i));
		player.setMap(map);

		// set units
		for (Unit unit : player.getUnits())
		{
			unit.setRulerPlayer(player);
			unit.setTile(player.getTileByXY(unit.lastPositionForSave.X, unit.lastPositionForSave.Y));
			if(unit instanceof CombatUnit)
				unit.getTile().setCombatUnitInTile((CombatUnit) unit);
			else
				unit.getTile().setNonCombatUnitInTile((NonCombatUnit) unit);
			if(unit.getDestination() != null)
				unit.setDestination(player.getTileByXY(unit.getDestination().getPosition().X, unit.getDestination().getPosition().Y));
		}
		if(player.getSelectedUnit() != null && player.getSelectedUnit() instanceof CombatUnit && player.getSelectedUnit().getTile() != null)
			player.setSelectedUnit(player.getTileByXY(player.getSelectedUnit().getTile().getPosition().X, player.getSelectedUnit().getTile().getPosition().Y).getCombatUnitInTile());
		else if(player.getSelectedUnit() != null && player.getSelectedUnit() instanceof NonCombatUnit && player.getSelectedUnit().getTile() != null)
			player.setSelectedUnit(player.getTileByXY(player.getSelectedUnit().getTile().getPosition().X, player.getSelectedUnit().getTile().getPosition().Y).getNonCombatUnitInTile());
		// set enemy units
		for (Unit enemyUnit : player.enemyUnits)
		{
			Player enemyPlayer;
			try
			{
				enemyPlayer = new Player(Civilization.OTTOMAN, "enemy", "enemy", "enemy", 0, 0);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
			enemyUnit.setRulerPlayer(enemyPlayer);
			enemyUnit.setTile(player.getTileByXY(enemyUnit.lastPositionForSave.X, enemyUnit.lastPositionForSave.Y));
			if(enemyUnit instanceof CombatUnit)
				player.getTileByXY(enemyUnit.getTile().getPosition().X, enemyUnit.getTile().getPosition().Y).setCombatUnitInTile((CombatUnit) enemyUnit);
			else
				player.getTileByXY(enemyUnit.getTile().getPosition().X, enemyUnit.getTile().getPosition().Y).setNonCombatUnitInTile((NonCombatUnit) enemyUnit);
		}

		// set city tiles
		for (City city : player.getCities())
		{
//			if(player.getSelectedCity().getCapitalTile().getPosition().equals(city.getCapitalTile().getPosition()))
//				player.setSelectedCity(city);

			for (int i = 0; i < city.getTerritory().size(); i++)
				city.getTerritory().set(i, player.getTileByXY(city.getTerritory().get(i).getPosition().X, city.getTerritory().get(i).getPosition().Y));
			city.setCapitalTile(player.getTileByXY(city.getCapitalTile().getPosition().X, city.getCapitalTile().getPosition().Y));
			// set buildings
			for (Building building : city.getBuildings())
			{
				building.setTile(player.getTileByXY(building.getTile().getPosition().X, building.getTile().getPosition().Y));
				building.setCity(city);
			}
			// set citizens
			for (int i = 0; i < city.getCitizens().size(); i++)
			{
				city.getCitizens().get(i).setCity(city);
				if(city.getCitizens().get(i).getWorkingTile() == null)
					continue;
				city.getCitizens().get(i).setWorkingTile(player.getTileByXY(city.getCitizens().get(i).getWorkingTile().getPosition().X, city.getCitizens().get(i).getWorkingTile().getPosition().Y));
			}

			if(city.getCurrentConstruction() != null)
			{
				if(city.getCurrentConstruction() instanceof CombatUnit && ((CombatUnit) city.getCurrentConstruction()).getTile() != null)
					city.setCurrentConstruction((player.getTileByXY(((CombatUnit) city.getCurrentConstruction()).getTile().getPosition().X, ((CombatUnit) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
				else if(city.getCurrentConstruction() instanceof NonCombatUnit && ((NonCombatUnit) city.getCurrentConstruction()).getTile() != null)
					city.setCurrentConstruction((player.getTileByXY(((NonCombatUnit) city.getCurrentConstruction()).getTile().getPosition().X, ((NonCombatUnit) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
				else if(city.getCurrentConstruction() instanceof Building)
					city.setCurrentConstruction((player.getTileByXY(((Building) city.getCurrentConstruction()).getTile().getPosition().X, ((Building) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
			}

			if(city.getGarrison() != null)
				city.setGarrison(player.getTileByXY(city.getGarrison().getTile().getPosition().X, city.getGarrison().getTile().getPosition().Y).getCombatUnitInTile());
			if(city.getNonCombatUnit() != null)
				city.setNonCombatUnit(player.getTileByXY(city.getNonCombatUnit().getTile().getPosition().X, city.getNonCombatUnit().getTile().getPosition().Y).getNonCombatUnitInTile());

			city.setRulerPlayer(player);

//			if(city.getCapitalTile().getPosition().equals(player.getInitialCapitalCity().getCapitalTile().getPosition()))
//				player.setInitialCapitalCity(city);
//			if(city.getCapitalTile().getPosition().equals(player.getCurrentCapitalCity().getCapitalTile().getPosition()))
//				player.setCurrentCapitalCity(city);

		}

		// set trade requests
		outerloop:
		for (TradeRequest tradeRequest : player.getTradeRequests())
			for (Player player1 : getPlayers())
				if(player1.getCivilization().equals(tradeRequest.senderCivilization))
				{
					tradeRequest.setSender(player1);
					continue outerloop;
				}

		return player;
	}

	private void updatePlayer(Response response)
	{
		if (response.getParams().get("player") == null)
			return;
		String playerJson = (String) response.getParams().get("player");
		Player updatedPlayer = jsonToPlayer(playerJson);

		setPlayer(updatedPlayer);
	}

	private Response sendRequestAndGetResponse(Request request)
	{
		try
		{
			socketDOS.writeUTF(request.toJson());
			socketDOS.flush();

			Response response = Response.fromJson(socketDIS.readUTF());

			updatePlayer(response);

			return response;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}

	// cheat codes
	public void increaseGold(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void increaseTurns(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void increaseFood(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void addTechnology(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void gainAllTechnologies(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void increaseHappiness(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void killEnemyUnit(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void increaseHealth(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void increaseScore(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void gainBonusResourceCheat(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void gainStrategicResourceCheat(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}
	public void gainLuxuryResourceCheat(String command)
	{
		Request request = new Request();
		request.setAction("cheat code");
		request.addParam("description", command);

		sendRequestAndGetResponse(request);
	}


	public String checkChangeTurn()
	{
		Request request = new Request();
		request.setAction("next turn");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String getYear()
	{
		Request request = new Request();
		request.setAction("getYear");

		return (String) sendRequestAndGetResponse(request).getParams().get("year");
	}
	public String getTurnCounter()
	{
		Request request = new Request();
		request.setAction("getTurnCounter");

		return (String) sendRequestAndGetResponse(request).getParams().get("turnCounter");
	}
	public String showResearch()
	{
		Request request = new Request();
		request.setAction("showResearch");

		return (String) sendRequestAndGetResponse(request).getParams().get("research");
	}
	public BuildingType requiredTechForBuilding(Technology technology)
	{
		for(int i = 0; i < BuildingType.values().length; i++)
			if(BuildingType.values()[i].requiredTechnology == technology)
				return BuildingType.values()[i];
		return null;
	}
	public Improvement requiredTechForImprovement(Technology technology)
	{
		for(int i = 0; i < Improvement.values().length; i++)
			if(Improvement.values()[i].requiredTechnology == technology)
				return Improvement.values()[i];
		return null;
	}
	public ArrayList<Player> getPlayers()
	{
		ArrayList<String> playersJson = new ArrayList<>();
		for (int i = 0; i < getNumberOfPlayers(); i++)
		{
			Request request = new Request();
			request.setAction("getPlayerByIndex");
			request.addParam("index", String.valueOf(i));
			Response response = sendRequestAndGetResponse(request);
			playersJson.add((String) response.getParams().get("playerByIndex"));
		}

		ArrayList<Player> players = new ArrayList<>();
		for (String s : playersJson)
			players.add(jsonToPlayer(s));

		for (int i = 0; i < players.size(); i++)
			if(players.get(i).getCivilization().equals(player.getCivilization()))
			{
				players.set(i, player);
				break;
			}

		return players;
	}
	public int getNumberOfPlayers()
	{
		Request request = new Request();
		request.setAction("getNumberOfPlayers");

		Response response = sendRequestAndGetResponse(request);
		return Integer.parseInt((String) response.getParams().get("numberOfPlayers"));
	}
	public Player isGameEnd()
	{
		Request request = new Request();
		request.setAction("isGameEnd");
		Player winnerPlayer = jsonToPlayer((String) sendRequestAndGetResponse(request).getParams().get("player"));

		return winnerPlayer;
	}
	public void winGame()
	{
//		Request request = new Request();
//		request.setAction("");
	}
	public String moveUnit(String command)
	{
		Request request = new Request();
		request.setAction("move unit");
		request.addParam("command", command);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String fortify()
	{
		Request request = new Request();
		request.setAction("fortify");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String wake()
	{
		Request request = new Request();
		request.setAction("wake");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String pillage()
	{
		Request request = new Request();
		request.setAction("pillage");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String delete()
	{
		Request request = new Request();
		request.setAction("delete");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String sleep()
	{
		Request request = new Request();
		request.setAction("sleep");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String garrison()
	{
		Request request = new Request();
		request.setAction("garrison");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String attackCity(Matcher matcher)
	{
		Request request = new Request();
		request.setAction("attackCity");
		request.addParam("x", matcher.group("x"));
		request.addParam("y", matcher.group("y"));

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String setup(Matcher matcher)
	{
		Request request = new Request();
		request.setAction("setUp");
		request.addParam("x", matcher.group("x"));
		request.addParam("y", matcher.group("y"));

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String alert()
	{
		Request request = new Request();
		request.setAction("alert");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String fortifyTilHeal()
	{
		Request request = new Request();
		request.setAction("fortifyTillHeal");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String cancel()
	{
		Request request = new Request();
		request.setAction("cancel");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String found()
	{
		Request request = new Request();
		request.setAction("found");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String road()
	{
		Request request = new Request();
		request.setAction("road");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String railRoad()
	{
		Request request = new Request();
		request.setAction("railRoad");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String farm()
	{
		Request request = new Request();
		request.setAction("farm");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String mine()
	{
		Request request = new Request();
		request.setAction("mine");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String tradingPost()
	{
		Request request = new Request();
		request.setAction("tradingPost");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String lumberMill()
	{
		Request request = new Request();
		request.setAction("lumberMill");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String factory()
	{
		Request request = new Request();
		request.setAction("factory");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String camp()
	{
		Request request = new Request();
		request.setAction("camp");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String pasture()
	{
		Request request = new Request();
		request.setAction("pasture");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String plantation()
	{
		Request request = new Request();
		request.setAction("plantation");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String quarry()
	{
		Request request = new Request();
		request.setAction("quarry");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String removeRoute()
	{
		Request request = new Request();
		request.setAction("removeRoute");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String removeFeature()
	{
		Request request = new Request();
		request.setAction("removeFeature");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String repair()
	{
		Request request = new Request();
		request.setAction("repair");

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String buyUnit(String type)
	{
		Request request = new Request();
		request.setAction("buyUnit");
		request.addParam("type", type);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String buyBuilding(String type)
	{
		Request request = new Request();
		request.setAction("buyBuilding");
		request.addParam("type", type);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String buyTile(String coordinates)
	{
		Request request = new Request();
		request.setAction("buyTile");
		request.addParam("coordinates", coordinates);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String buildBuilding(BuildingType buildingType)
	{
		Request request = new Request();
		request.setAction("buildBuilding");
		request.addParam("type", buildingType.toString());

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String buildUnit(String type)
	{
		Request request = new Request();
		request.setAction("buildUnit");
		request.addParam("type", type);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String unLockCitizenToTile(String coordinates)
	{
		Request request = new Request();
		request.setAction("unlockCitizenToTile");
		request.addParam("coordinates", coordinates);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public String lockCitizenToTile(String coordinates)
	{
		Request request = new Request();
		request.setAction("lockCitizenToTile");
		request.addParam("coordinates", coordinates);

		return sendRequestAndGetResponse(request).getMassage();
	}
	public void selectCUnit(String positionX, String positionY)
	{
		Request request = new Request();
		request.setAction("selectCUnit");
		request.addParam("x", positionX);
		request.addParam("y", positionY);

		sendRequestAndGetResponse(request);
	}
	public void selectNCUNit(String positionX, String positionY)
	{
		Request request = new Request();
		request.setAction("selectNCUnit");
		request.addParam("x", positionX);
		request.addParam("y", positionY);

		sendRequestAndGetResponse(request);
	}
	public void selectCity(String positionX, String positionY)
	{
		Request request = new Request();
		request.setAction("selectCity");
		request.addParam("x", positionX);
		request.addParam("y", positionY);

		sendRequestAndGetResponse(request);
	}
}
















