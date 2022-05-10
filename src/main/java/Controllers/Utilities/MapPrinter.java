package Controllers.Utilities;

import Models.City.City;
import Models.Player.Player;
import Models.Player.TileState;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import Controllers.GameController;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapPrinter
{
	// our map is a square with size of MapSize * MapSize
	private static HashMap<Tile, TileState> map;
	private static Player player;
	private static int mapSize;
	private static StringBuilder mapString;
	private final static Attribute FOG_OF_WAR_ATTRIBUTE = Attribute.BACK_COLOR(255, 255, 255); // JColor.Attribute of fog of war
	private final static String REVEALED_SYMBOL = "*REVEALED*";
	
	public static String getMapString(Player player)
	{
		MapPrinter.player = player;
		MapPrinter.map = player.getMap();
		GameController gameController = GameController.getInstance();
		MapPrinter.mapSize = gameController.MAP_SIZE;
		mapString = new StringBuilder();
		
		printFirstLine();
		for(int i = 1; i <= gameController.MAP_SIZE * 8 + 3; i++)
			switch(i % 8)
			{
				case 1:
					printLine1(i);
					break;
				case 2:
					printLine2(i);
					break;
				case 3:
					printLine3(i);
					break;
				case 4:
					printLine4(i);
					break;
				case 5:
					printLine5(i);
					break;
				case 6:
					printLine6(i);
					break;
				case 7:
					printLine7(i);
					break;
				case 0:
					printLine8(i);
					break;
			}
		printLastLine();
		printMapGuide();
		
		return mapString.toString();
	}
	private static void printFog(int number)
	{
		for(int i = 0; i < number; i++)
			mapString.append(Ansi.colorize(" ", FOG_OF_WAR_ATTRIBUTE));
	}
	private static void printBorder(Tile tile, int borderIndex)
	{
		BorderType[] borders = tile.getBorders();
		if(borderIndex == 0 || borderIndex == 3)
		{
			Tile neighborTile;
			if(borderIndex == 0)
				neighborTile = player.getTileByXY(tile.getPosition().X - 1, tile.getPosition().Y);
			else
				neighborTile = player.getTileByXY(tile.getPosition().X + 1, tile.getPosition().Y);
			
			if(map.get(tile).equals(TileState.FOG_OF_WAR) || (neighborTile != null && map.get(neighborTile).equals(TileState.FOG_OF_WAR)))
				printFog(10);
			else
			{
				Attribute attribute = (borders[borderIndex].equals(BorderType.RIVER)) ? BorderType.RIVER.attribute : tile.getTileType().attribute;
				mapString.append(Ansi.colorize("__________", attribute));
			}
		}
		else if(borderIndex == 1 || borderIndex == 2 || borderIndex == 5 || borderIndex == 4)
		{
			Tile neighborTile = null;
			if(borderIndex == 1)
				neighborTile = player.getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R, tile.getPosition().S + 1);
			else if(borderIndex == 2)
				neighborTile = player.getTileByQRS(tile.getPosition().Q - 1, tile.getPosition().R + 1, tile.getPosition().S);
			else if(borderIndex == 4)
				neighborTile = player.getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R, tile.getPosition().S - 1);
			else if(borderIndex == 5)
				neighborTile = player.getTileByQRS(tile.getPosition().Q + 1, tile.getPosition().R - 1, tile.getPosition().S);
			
			if(map.get(tile).equals(TileState.FOG_OF_WAR) || (neighborTile != null && map.get(neighborTile).equals(TileState.FOG_OF_WAR)))
				printFog(1);
			else
			{
				if(borderIndex == 1 || borderIndex == 4)
					mapString.append(Ansi.colorize("/", borders[borderIndex].attribute));
				else if(borderIndex == 2 || borderIndex == 5)
					mapString.append(Ansi.colorize("\\", borders[borderIndex].attribute));
			}
		}
	}
	private static void printMapGuide()
	{
		mapString.append("\n");
		
		mapString.append(Ansi.colorize("Map Guide:\n", Attribute.WHITE_BACK(), Attribute.BLACK_TEXT()));
		
		mapString.append(String.format("%-15s    ", "Tile types:"));
		Arrays.asList(TileType.values()).forEach((tileType)->{
			mapString.append(tileType + ":");
			mapString.append(Ansi.colorize("    ", tileType.attribute));
			mapString.append("    ");
		});
		mapString.append("\n");
		mapString.append(String.format("%-15s    ", "Tile features:"));
		Arrays.asList(TileFeature.values()).forEach((tileFeature)->{
			if(!tileFeature.equals(TileFeature.NONE))
				mapString.append(String.format("%s:%-6s", tileFeature, tileFeature.symbol));
		});
		mapString.append("\n");
		mapString.append(String.format("%-15s    ", "Resources:"));
		Arrays.asList(ResourceType.values()).forEach((resourceType->{
			if(!resourceType.equals(ResourceType.NONE))
				mapString.append(String.format("%s:%-6s", resourceType, resourceType.symbol));
		}));
		mapString.append("\n");
		mapString.append(String.format("%-15s    ", "Improvements:"));
		Arrays.asList(Improvement.values()).forEach((improvement->{
			if(!improvement.equals(Improvement.NONE))
				mapString.append(String.format("%s:%-6s", improvement, improvement.symbol));
		}));
		mapString.append("\n");
	}
	private static void printFirstLine()
	{
		// print the first line (considering rivers)
		mapString.append("    ");
		for(int i = 0; i < mapSize; i += 2)
		{
			printBorder(player.getTileByXY(0, i), 0);
			if(i != mapSize - 1)
				mapString.append("                  ");
		}
		mapString.append("\n");
	}
	private static void printLine1(int line)
	{
		// print position and CUnit
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
				if(tile == null)
				{
					if(i == 0)
						mapString.append("    ");
					mapString.append("          ");
					printBorder(player.getTileByXY(row - 1, i + 1), 2);
				}
				else
				{
					if(i == 0)
					{
						mapString.append("   ");
						printBorder(tile, 1);
					}
					printPosition(tile);
					printBorder(tile, 5);
				}
			else if(tile == null)
			{
				if(i + 1 < mapSize)
				{
					mapString.append("                ");
					printBorder(player.getTileByXY(row + 1, i + 1), 1);
				}
			}
			else
			{
				printCUnit(tile);
				printBorder(tile, 4);
			}
		}
		mapString.append("\n");
	}
	private static void printLine2(int line)
	{
		// printing tileFeature and NCUnit
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(tile == null)
				{
					if(i == 0)
						mapString.append("   ");
					mapString.append("            ");
					printBorder(player.getTileByXY(row - 1, i + 1), 2);
				}
				else
				{
					if(i == 0)
					{
						mapString.append("  ");
						printBorder(tile, 1);
					}
					printTileFeature(tile);
					printBorder(tile, 5);
				}
			}
			else
			{
				if(tile == null)
				{
					if(i + 1 < mapSize)
					{
						mapString.append("              ");
						printBorder(player.getTileByXY(row + 1, i + 1), 1);
					}
				}
				else
				{
					printNCUnit(tile);
					printBorder(tile, 4);
				}
			}
		}
		mapString.append("\n");
	}
	private static void printLine3(int line)
	{
		// printing resource and empty
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(tile == null)
				{
					if(i == 0)
						mapString.append("  ");
					mapString.append("              ");
					printBorder(player.getTileByXY(row - 1, i + 1), 2);
				}
				else
				{
					if(i == 0)
					{
						mapString.append(" ");
						printBorder(tile, 1);
					}
					printResource(tile);
					printBorder(tile, 5);
				}
			}
			else
			{
				if(tile == null)
				{
					if(i + 1 < mapSize)
					{
						mapString.append("            ");
						printBorder(player.getTileByXY(row + 1, i + 1), 1);
					}
				}
				else
				{
					printTemp(tile);
					printBorder(tile, 4);
				}
			}
		}
		mapString.append("\n");
	}
	private static void printLine4(int line)
	{
		// printing improvement and border
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(i == 0)
					printBorder(tile, 1);
				printImprovement(tile);
				printBorder(tile, 5);
			}
			else
			{
				printBorder(player.getTileByXY(row + 1, i), 0);
				if(tile == null)
				{
					if(i + 2 < mapSize)
						printBorder(player.getTileByXY(row + 1, i + 1), 1);
				}
				else
					printBorder(player.getTileByXY(row, i), 4);
			}
		}
		mapString.append("\n");
	}
	private static void printLine5(int line)
	{
		// printing CUnit and position
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(i == 0)
					printBorder(tile, 2);
				printCUnit(tile);
				printBorder(tile, 4);
			}
			else
			{
				printPosition(tile);
				printBorder(tile, 5);
			}
		}
		mapString.append("\n");
	}
	private static void printLine6(int line)
	{
		// printing NCUnit and tileFeature
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(i == 0)
				{
					mapString.append(" ");
					printBorder(tile, 2);
				}
				printNCUnit(tile);
				printBorder(tile, 4);
			}
			else
			{
				printTileFeature(tile);
				printBorder(tile, 5);
			}
		}
		mapString.append("\n");
	}
	private static void printLine7(int line)
	{
		// printing temp and resource
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(i == 0)
				{
					mapString.append("  ");
					printBorder(tile, 2);
				}
				printTemp(tile);
				printBorder(tile, 4);
			}
			else
			{
				// print resource
				printResource(tile);
				printBorder(tile, 5);
			}
		}
		mapString.append("\n");
	}
	private static void printLine8(int line)
	{
		// printing border and improvement
		for(int i = 0; i < mapSize; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = player.getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(i == 0)
				{
					mapString.append("   ");
					printBorder(tile, 2);
				}
				printBorder(tile, 3);
				printBorder(tile, 4);
			}
			else
			{
				printImprovement(tile);
				printBorder(tile, 5);
			}
		}
		mapString.append("\n");
	}
	private static void printLastLine()
	{
		mapString.append("                 ");
		for(int i = 1; i < mapSize; i += 2)
		{
			Tile tile = player.getTileByXY(mapSize - 1, i);
			assert tile != null;
			printBorder(tile, 2);
			printBorder(tile, 3);
			printBorder(tile, 4);
			if(i < mapSize)
				mapString.append("                ");
		}
		mapString.append("\n");
	}
	private static void printPosition(Tile tile)
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(10);
		else
			mapString.append(Ansi.colorize(String.format("  (%-2d,%2d) ", tile.getPosition().X, tile.getPosition().Y),
					tile.getTileType().attribute, Attribute.BLACK_TEXT()));
	}
	private static void printTileFeature(Tile tile)
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(12);
		else
		{
			mapString.append(Ansi.colorize("     ", tile.getTileType().attribute));
			if(tile.getTileFeature() != TileFeature.NONE)
				mapString.append(String.format("%2s", tile.getTileFeature().symbol));
			else
				mapString.append(Ansi.colorize("  ", tile.getTileType().attribute));
			mapString.append(Ansi.colorize("     ", tile.getTileType().attribute));
		}
	}
	private static void printResource(Tile tile)
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(14);
		else
		{
			mapString.append(Ansi.colorize("      ", tile.getTileType().attribute));
			if(tile.getResource() == null)
				mapString.append(Ansi.colorize("  ", tile.getTileType().attribute));
			else
				mapString.append(String.format("%2s", tile.getResource().getRESOURCE_TYPE().symbol));
			mapString.append(Ansi.colorize("      ", tile.getTileType().attribute));
		}
	}
	private static void printImprovement(Tile tile)
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(16);
		else
		{
			mapString.append(Ansi.colorize("       ", tile.getTileType().attribute));
			if(tile.getImprovement().equals(Improvement.NONE))
				mapString.append(Ansi.colorize("  ", tile.getTileType().attribute));
			else
				mapString.append(Ansi.colorize(String.format("%2s", tile.getImprovement().symbol)));
			mapString.append(Ansi.colorize("       ", tile.getTileType().attribute));
		}
	}
	private static void printCUnit(Tile tile)
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(16);
		else
		{
			if(tile.getCombatUnitInTile() == null)
				mapString.append(Ansi.colorize("                ", tile.getTileType().attribute));
			else
			{
				Attribute unitAttribute = (player.getSelectedUnit() == tile.getCombatUnitInTile()) ? Attribute.YELLOW_BACK() : tile.getTileType().attribute;
				mapString.append(Ansi.colorize(String.format("  %-14s", tile.getCombatUnitInTile().toString()), unitAttribute,
						Attribute.BLACK_TEXT()));
			}
			
		}
	}
	private static void printNCUnit(Tile tile)
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(14);
		else
		{
			if(tile.getNonCombatUnitInTile() == null)
				mapString.append(Ansi.colorize("              ", tile.getTileType().attribute));
			else
			{
				Attribute unitAttribute = (player.getSelectedUnit() == tile.getNonCombatUnitInTile()) ? Attribute.YELLOW_BACK() : tile.getTileType().attribute;
				mapString.append(Ansi.colorize(String.format("  %-12s", tile.getNonCombatUnitInTile().toString()), unitAttribute,
						Attribute.BLACK_TEXT()));
			}
		}
	}
	private static void printTemp(Tile tile) // TODO:
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(12);
		else if(map.get(tile).equals(TileState.REVEALED))
			mapString.append(Ansi.colorize("<<REVEALED>>", tile.getTileType().attribute, Attribute.BLACK_TEXT()));
		else
			mapString.append(Ansi.colorize("            ", tile.getTileType().attribute));
	}
}





















