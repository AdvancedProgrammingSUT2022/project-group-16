package Controllers.Utilities;

import Models.Player.TileState;
import Models.Resources.ResourceType;
import Models.Terrain.*;
import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapPrinter
{
	private static HashMap<Tile, TileState> map;
	private static int columns;
	private static int rows;
	private static StringBuilder mapString;
	private final static Attribute FOG_OF_WAR_ATTRIBUTE = Attribute.WHITE_BACK();
	private final static String REVEALED_SYMBOL = "*REVEALED*";
	
	public static String getMapString(HashMap<Tile, TileState> map, int columns, int rows)
	{
		mapString = new StringBuilder();
		MapPrinter.map = map;
		MapPrinter.columns = columns;
		MapPrinter.rows = rows;
		
		printFirstLine();
		for(int i = 1; i <= rows * 8 + 3; i++)
			switch(i % 8)
			{
				case 1 -> printLine1(i);
				case 2 -> printLine2(i);
				case 3 -> printLine3(i);
				case 4 -> printLine4(i);
				case 5 -> printLine5(i);
				case 6 -> printLine6(i);
				case 7 -> printLine7(i);
				case 0 -> printLine8(i);
			}
		printLastLine();
		printMapGuide();
		return mapString.toString();
	}
	private static Tile getTileByXY(int x, int y)
	{
		for(Map.Entry<Tile, TileState> entry : map.entrySet())
			if(entry.getKey().getPosition().X == x && entry.getKey().getPosition().Y == y)
				return entry.getKey();
		return null;
	}
	private static void printFog(int number)
	{
		for(int i = 0; i < number; i++)
			mapString.append(Ansi.colorize(" ", FOG_OF_WAR_ATTRIBUTE));
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
		for(int i = 0; i < columns; i += 2)
		{
			Tile tile = getTileByXY(0, i);
			mapString.append(Ansi.colorize("__________", tile.getBorders()[0].attribute));
			if(i != columns - 1)
				mapString.append("                  ");
		}
		mapString.append("\n");
	}
	private static void printLine1(int line)
	{
		// print position and CUnit
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			
			if(i % 2 == 0)
				if(tile == null)
				{
					if(i == 0)
						mapString.append("    ");
					mapString.append("          ");
					mapString.append(Ansi.colorize("\\", getTileByXY(row - 1, i + 1).getBorders()[2].attribute));
				}
				else
				{
					BorderType[] borders = tile.getBorders();
					if(i == 0)
					{
						mapString.append("   ");
						mapString.append(Ansi.colorize("/", borders[1].attribute));
					}
					printPosition(tile);
					mapString.append(Ansi.colorize("\\", borders[5].attribute));
				}
			else if(tile == null)
			{
				if(i + 1 < columns)
				{
					mapString.append("                ");
					mapString.append(Ansi.colorize("/", getTileByXY(row + 1, i + 1).getBorders()[1].attribute));
				}
			}
			else
			{
				printCUnit(tile);
				mapString.append(Ansi.colorize("/", tile.getBorders()[4].attribute));
			}
		}
		mapString.append("\n");
	}
	private static void printLine2(int line)
	{
		// printing tileFeature and NCUnit
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(tile == null)
				{
					if(i == 0)
						mapString.append("   ");
					mapString.append("            ");
					mapString.append(Ansi.colorize("\\", getTileByXY(row - 1, i + 1).getBorders()[2].attribute));
				}
				else
				{
					BorderType[] borders = tile.getBorders();
					if(i == 0)
					{
						mapString.append("  ");
						mapString.append(Ansi.colorize("/", borders[1].attribute));
					}
					printTileFeature(tile);
					mapString.append(Ansi.colorize("\\", borders[5].attribute));
				}
			}
			else
			{
				if(tile == null)
				{
					if(i + 1 < columns)
					{
						mapString.append("              ");
						mapString.append(Ansi.colorize("/", getTileByXY(row + 1, i + 1).getBorders()[1].attribute));
					}
				}
				else
				{
					printNCUnit(tile);
					mapString.append(Ansi.colorize("/", tile.getBorders()[4].attribute));
				}
			}
		}
		mapString.append("\n");
	}
	private static void printLine3(int line)
	{
		// printing resource and empty
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				if(tile == null)
				{
					if(i == 0)
						mapString.append("  ");
					mapString.append("              ");
					mapString.append(Ansi.colorize("\\", getTileByXY(row - 1, i + 1).getBorders()[2].attribute));
				}
				else
				{
					BorderType[] borders = tile.getBorders();
					if(i == 0)
					{
						mapString.append(" ");
						mapString.append(Ansi.colorize("/", borders[1].attribute));
					}
					printResource(tile);
					mapString.append(Ansi.colorize("\\", borders[5].attribute));
				}
			}
			else
			{
				if(tile == null)
				{
					if(i + 1 < columns)
					{
						mapString.append("            ");
						mapString.append(Ansi.colorize("/", getTileByXY(row + 1, i + 1).getBorders()[1].attribute));
					}
				}
				else
				{
					// print something (empty or something about building)
					printTemp(tile);
					mapString.append(Ansi.colorize("/", tile.getBorders()[4].attribute));
				}
			}
		}
		mapString.append("\n");
	}
	private static void printLine4(int line)
	{
		// printing improvement and border
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			
			if(i % 2 == 0)
			{
				BorderType[] borders = tile.getBorders();
				if(i == 0)
					mapString.append(Ansi.colorize("/", borders[1].attribute));
				printImprovement(tile);
				mapString.append(Ansi.colorize("\\", borders[5].attribute));
			}
			else
			{
				mapString.append(Ansi.colorize("__________", getTileByXY(row + 1, i).getBorders()[0].attribute));
				if(tile == null)
				{
					if(i + 2 < columns)
						mapString.append(Ansi.colorize("/", getTileByXY(row + 1, i + 1).getBorders()[1].attribute));
				}
				else
				{
					mapString.append(Ansi.colorize("/", getTileByXY(row, i).getBorders()[4].attribute));
				}
			}
		}
		mapString.append("\n");
	}
	private static void printLine5(int line)
	{
		// printing CUnit and position
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			BorderType[] borders = tile.getBorders();
			
			if(i % 2 == 0)
			{
				if(i == 0)
					mapString.append(Ansi.colorize("\\", borders[2].attribute));
				printCUnit(tile);
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				printPosition(tile);
				mapString.append(Ansi.colorize("\\", borders[5].attribute));
			}
		}
		mapString.append("\n");
	}
	private static void printLine6(int line)
	{
		// printing NCUnit and tileFeature
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			BorderType[] borders = tile.getBorders();
			
			if(i % 2 == 0)
			{
				if(i == 0)
				{
					mapString.append(" ");
					mapString.append(Ansi.colorize("\\", borders[2].attribute));
				}
				printNCUnit(tile);
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				printTileFeature(tile);
				mapString.append(Ansi.colorize("\\", borders[5].attribute));
			}
		}
		mapString.append("\n");
	}
	private static void printLine7(int line)
	{
		// printing empty and resource
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			BorderType[] borders = tile.getBorders();
			
			if(i % 2 == 0)
			{
				if(i == 0)
				{
					mapString.append("  ");
					mapString.append(Ansi.colorize("\\", borders[2].attribute));
				}
				printTemp(tile);
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				// print resource
				printResource(tile);
				mapString.append(Ansi.colorize("\\", borders[5].attribute));
			}
		}
		mapString.append("\n");
	}
	private static void printLine8(int line)
	{
		// printing border and improvement
		for(int i = 0; i < columns; i++)
		{
			int row = (i % 2 == 0) ? (line - 1) / 8 : Math.floorDiv((line - 5), 8);
			Tile tile = getTileByXY(row, i);
			BorderType[] borders = tile.getBorders();
			
			if(i % 2 == 0)
			{
				if(i == 0)
				{
					mapString.append("   ");
					mapString.append(Ansi.colorize("\\", borders[2].attribute));
				}
				mapString.append(Ansi.colorize("__________", borders[3].attribute));
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				printImprovement(tile);
				mapString.append(Ansi.colorize("\\", borders[5].attribute));
			}
		}
		mapString.append("\n");
	}
	private static void printLastLine()
	{
		mapString.append("                 ");
		for(int i = 1; i < columns; i += 2)
		{
			Tile tile = getTileByXY(rows - 1, i);
			assert tile != null;
			BorderType[] bordrs = tile.getBorders();
			mapString.append(Ansi.colorize("\\", bordrs[2].attribute));
			mapString.append(Ansi.colorize("__________", bordrs[3].attribute));
			mapString.append(Ansi.colorize("/", bordrs[4].attribute));
			if(i < columns)
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
			mapString.append(String.format("%2s", tile.getTileFeature().symbol));
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
				mapString.append(Ansi.colorize(String.format("%-16s", tile.getCombatUnitInTile().toString()), tile.getTileType().attribute,
						Attribute.BLACK_TEXT()));
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
				mapString.append(Ansi.colorize(String.format("%-14s", tile.getNonCombatUnitInTile().toString()), tile.getTileType().attribute,
						Attribute.BLACK_TEXT()));
		}
	}
	private static void printTemp(Tile tile) // TODO:
	{
		if(map.get(tile).equals(TileState.FOG_OF_WAR))
			printFog(12);
		else
			mapString.append(Ansi.colorize("            ", tile.getTileType().attribute));
	}
}





















