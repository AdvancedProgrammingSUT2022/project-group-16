package Controllers.Utilities;

import Models.Resources.ResourceType;
import Models.Terrain.*;
import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.util.ArrayList;
import java.util.Arrays;

public class MapPrinter
{
	private static ArrayList<Tile> tiles;
	private static int columns;
	private static int rows;
	private static StringBuilder mapString;
	
	public static String getMapString(ArrayList<Tile> map, int columns, int rows)
	{
		mapString = new StringBuilder();
		tiles = map;
		MapPrinter.columns = columns;
		MapPrinter.rows = rows;
		
		printFirstLine();
		for(int i = 1; i <= rows * 8 + 3; i++)
		{
			int x = i % 8;
			switch(x)
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
		}
		printLastLine();
		printMapGuide();
		return mapString.toString();
	}
	private static Tile getTileByXY(int x, int y)
	{
		for(Tile tile : tiles)
			if(tile.getPosition().X == x && tile.getPosition().Y == y)
				return tile;
		return null;
	}
	private static void printFirstLine()
	{
		// print the first line (considering rivers)
		
		mapString.append("    ");
		for(int i = 0; i < columns; i += 2)
		{
			Tile tile = getTileByXY(0, i);
			assert tile != null;
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
			{
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
					mapString.append(Ansi.colorize(String.format("  (%-2d,%2d) ", tile.getPosition().X, tile.getPosition().Y), tile.getTileType().attribute));
					mapString.append(Ansi.colorize("\\", borders[5].attribute));
				}
			}
			else
			{
				if(tile == null)
				{
					if(i + 1 < columns)
					{
						mapString.append("                ");
						mapString.append(Ansi.colorize("/", getTileByXY(row + 1, i + 1).getBorders()[1].attribute));
					}
				}
				else
				{
					if(tile.getCombatUnitInTile() != null)
						mapString.append(Ansi.colorize(String.format("%-16s", tile.getCombatUnitInTile().toString()), tile.getTileType().attribute));
					else
						mapString.append(Ansi.colorize("                ", tile.getTileType().attribute));
					mapString.append(Ansi.colorize("/", tile.getBorders()[4].attribute));
				}
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
					
					mapString.append(Ansi.colorize(String.format("     %2s     ", tile.getTileFeature().symbol), tile.getTileType().attribute));
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
					if(tile.getNonCombatUnitInTile() != null)
						mapString.append(Ansi.colorize(String.format("%-14s", tile.getNonCombatUnitInTile().toString()), tile.getTileType().attribute));
					else
						mapString.append(Ansi.colorize("              ", tile.getTileType().attribute));
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
					if(tile.getResource() != null)
						mapString.append(Ansi.colorize(String.format("      %2s      ", tile.getResource().getRESOURCE_TYPE().symbol), tile.getTileType().attribute));
					else
						mapString.append(Ansi.colorize("              ", tile.getTileType().attribute));
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
					mapString.append(Ansi.colorize("            ", tile.getTileType().attribute));
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
			//			assert tile != null;
			
			if(i % 2 == 0)
			{
				BorderType[] borders = tile.getBorders();
				if(i == 0)
					mapString.append(Ansi.colorize("/", borders[1].attribute));
				mapString.append(Ansi.colorize(String.format("       %2s       ", tile.getImprovement().symbol), tile.getTileType().attribute));
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
				if(tile.getCombatUnitInTile() != null)
					mapString.append(Ansi.colorize(String.format("%-16s", tile.getCombatUnitInTile().toString()), tile.getTileType().attribute));
				else
					mapString.append(Ansi.colorize("                ", tile.getTileType().attribute));
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				mapString.append(Ansi.colorize(String.format("  (%-2d,%2d) ", tile.getPosition().X, tile.getPosition().Y), tile.getTileType().attribute));
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
				if(tile.getNonCombatUnitInTile() != null)
					mapString.append(Ansi.colorize(String.format("%-14s", tile.getNonCombatUnitInTile().toString()), tile.getTileType().attribute));
				else
					mapString.append(Ansi.colorize("              ", tile.getTileType().attribute));
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				mapString.append(Ansi.colorize(String.format("     %2s     ", tile.getTileFeature().symbol), tile.getTileType().attribute));
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
				// TODO: print sth
				mapString.append(Ansi.colorize("            ", tile.getTileType().attribute));
				mapString.append(Ansi.colorize("/", borders[4].attribute));
			}
			else
			{
				// print resource
				if(tile.getResource() != null)
					mapString.append(Ansi.colorize(String.format("      %2s      ", tile.getResource().getRESOURCE_TYPE().symbol), tile.getTileType().attribute));
				else
					mapString.append(Ansi.colorize("              ", tile.getTileType().attribute));
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
				mapString.append(Ansi.colorize(String.format("       %2s       ", tile.getImprovement().symbol), tile.getTileType().attribute));
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
}





















