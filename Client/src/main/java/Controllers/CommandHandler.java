package Controllers;

import IO.Request;
import IO.Response;
import Models.City.Building;
import Models.City.BuildingType;
import Models.City.City;
import Models.City.Construction;
import Models.Player.Player;
import Models.Player.Technology;
import Models.Resources.Resource;
import Models.Resources.TradeRequest;
import Models.Terrain.Improvement;
import Models.TypeAdapters.*;
import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;
import Models.Units.Unit;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import javafx.css.Match;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.regex.Matcher;

public class CommandHandler
{
	private static CommandHandler instance;

	private Gson gson;
	private Socket socket;
	private DataInputStream socketDIS;
	private DataOutputStream socketDOS;
	private Player player;


	private CommandHandler()
	{
		GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
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

		// set units
		for (Unit unit : player.getUnits())
		{
			unit.setRulerPlayer(player);
			unit.setTile(player.getTileByXY(unit.lastPositionForSave.X, unit.lastPositionForSave.Y));
			if(unit instanceof CombatUnit)
				unit.getTile().setCombatUnitInTile((CombatUnit) unit);
			else
				unit.getTile().setNonCombatUnitInTile((NonCombatUnit) unit);
			unit.setDestination(player.getTileByXY(unit.getDestination().getPosition().X, unit.getDestination().getPosition().Y));
		}
		// set city tiles
		for (City city : player.getCities())
		{
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
				city.getCitizens().get(i).setWorkingTile(player.getTileByXY(city.getCitizens().get(i).getWorkingTile().getPosition().X, city.getCitizens().get(i).getWorkingTile().getPosition().Y));
			}

			if(city.getCurrentConstruction() instanceof CombatUnit)
				city.setCurrentConstruction((player.getTileByXY(((CombatUnit) city.getCurrentConstruction()).getTile().getPosition().X, ((CombatUnit) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
			else if(city.getCurrentConstruction() instanceof NonCombatUnit)
				city.setCurrentConstruction((player.getTileByXY(((NonCombatUnit) city.getCurrentConstruction()).getTile().getPosition().X, ((NonCombatUnit) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());
			else if(city.getCurrentConstruction() instanceof Building)
				city.setCurrentConstruction((player.getTileByXY(((Building) city.getCurrentConstruction()).getTile().getPosition().X, ((Building) city.getCurrentConstruction()).getTile().getPosition().Y)).getCombatUnitInTile());

			city.setGarrison(player.getTileByXY(city.getGarrison().getTile().getPosition().X, city.getGarrison().getTile().getPosition().Y).getCombatUnitInTile());
			city.setNonCombatUnit(player.getTileByXY(city.getNonCombatUnit().getTile().getPosition().X, city.getNonCombatUnit().getTile().getPosition().Y).getNonCombatUnitInTile());

			city.setRulerPlayer(player);

			if(city.getCapitalTile().getPosition().equals(player.getInitialCapitalCity().getCapitalTile().getPosition()))
				player.setInitialCapitalCity(city);
			if(city.getCapitalTile().getPosition().equals(player.getCurrentCapitalCity().getCapitalTile().getPosition()))
				player.setCurrentCapitalCity(city);

		}
		// set trade requests

		// here
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
		String playerJson = (String) response.getParams().get("player");
		Player updatedPlayer = gson.fromJson(playerJson, Player.class);
	}

	// cheat codes
	public void increaseGold(Matcher matcher)
	{

	}
	public void increaseTurns(Matcher matcher)
	{

	}
	public void increaseFood(Matcher matcher)
	{

	}
	public void addTechnology(Matcher matcher)
	{

	}
	public void increaseHappiness(Matcher matcher)
	{

	}
	public void killEnemyUnit(Matcher matcher)
	{

	}
	public String moveUnit(Matcher matcher)
	{

	}
	public void increaseHealth(Matcher matcher)
	{

	}
	public void increaseScore(Matcher matcher)
	{

	}
	public void gainBonusResourceCheat()
	{

	}
	public void gainStrategicResourceCheat()
	{

	}
	public void gainLuxuryResourceCheat()
	{

	}


	public String checkChangeTurn()
	{

	}
	public String getYear()
	{

	}
	public String getTurnCounter()
	{

	}
	public String showResearch()
	{

	}
	public BuildingType requiredTechForBuilding(Technology technology)
	{

	}
	public Improvement requiredTechForImprovement(Technology technology)
	{

	}
	public ArrayList<Player> getPlayers()
	{
		// TODO: probable bug here

	}
	public Player isGameEnd()
	{

	}
	public void winGame()
	{

	}
	public String fortify()
	{

	}
	public String wake()
	{

	}
	public String pillage()
	{

	}
	public String delete()
	{

	}
	public String sleep()
	{

	}
	public String garrison()
	{

	}
	public String attackCity(Matcher matcher)
	{

	}
	public String setup(Matcher matcher)
	{

	}
	public String alert()
	{

	}
	public String fortifyTilHeal()
	{

	}
	public String cancel()
	{

	}
	public String found()
	{

	}
	public String road()
	{
	}
	public String railRoad()
	{
	}
	public String farm()
	{
	}
	public String mine()
	{
	}
	public String tradingPost()
	{
	}
	public String lumberMill()
	{
	}
	public String factory()
	{
	}
	public String camp()
	{
	}
	public String pasture()
	{
	}
	public String plantation()
	{
	}
	public String quarry()
	{
	}
	public String removeRoute()
	{
	}
	public String removeFeature()
	{
	}
	public String repair()
	{
	}
	public String buyUnit(String string)
	{

	}
	public String buyBuilding(String string)
	{

	}
	public String buyTile(Matcher matcher)
	{

	}
	public String buildBuilding(BuildingType buildingType)
	{

	}
	public String buildUnit(String type)
	{

	}
	public String unLockCitizenToTile(Matcher matcher)
	{

	}
	public String lockCitizenToTile(Matcher matcher)
	{

	}
}
















