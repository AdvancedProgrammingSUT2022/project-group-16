package Models.City;

import Models.Player.Technology;
public enum BuildingType
{
	BARRACKS(),
	GRANARY(),
	LIBRARY(),
	MONUMENT(),
	WALLS(),
	WATER_MILL(),
	ARMORY(),
	BURIAL_TOMB(),
	CIRCUS(),
	COLOSSEUM(),
	COURTHOUSE(),
	STABLE(),
	TEMPLE(),
	CASTLE(),
	FORGE(),
	GARDEN(),
	MARKET(),
	MINT(),
	MONASTERY(),
	UNIVERSITY(),
	WORKSHOP(),
	BANK(),
	MILITARY_ACADEMY(),
	MUSEUM(),
	OPERA_HOUSE(),
	PUBLIC_SCHOOL(),
	SATRAPS_COURT(),
	THEATER(),
	WINDMILL(),
	ARSENAL(),
	BROADCAST_TOWER(),
	FACTORY(),
	HOSPITAL(),
	MILITARY_BASE(),
	STOCK_EXCHANGE();
	
	BuildingType(int turns, int cost, int maintenanceCost, int healthPoints, Technology requiredTechnology)
	{
	}
}



















