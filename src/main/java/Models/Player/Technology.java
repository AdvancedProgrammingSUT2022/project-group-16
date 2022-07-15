package Models.Player;

import Models.City.Product;

import java.util.ArrayList;

public enum Technology implements Product
{
	AGRICULTURE(20),
	ANIMAL_HUSBANDRY(35),
	ARCHERY(35),
	BRONZE_WORKING(55),
	CALENDAR(70),
	MASONRY(55),
	MINING(35),
	POTTERY(35),
	THE_WHEEL(55),
	TRAPPING(55),
	WRITING(55),
	CONSTRUCTION(100),
	HORSEBACK_RIDING(100),
	IRON_WORKING(150),
	MATHEMATICS(100),
	PHILOSOPHY(100),
	CHIVALRY(440),
	CIVIL_SERVICE(400),
	CURRENCY(250),
	EDUCATION(440),
	ENGINEERING(250),
	MACHINERY(440),
	METAL_CASTING(240),
	PHYSICS(440),
	STEEL(440),
	THEOLOGY(250),
	ACOUSTICS(650),
	ARCHAEOLOGY(1300),
	BANKING(650),
	CHEMISTRY(900),
	ECONOMICS(900),
	FERTILIZER(1300),
	GUN_POWDER(680),
	METALLURGY(900),
	MILITARY_SCIENCE(1300),
	PRINTING_PRESS(650),
	RIFLING(1425),
	SCIENTIFIC_THEORY(1300),
	BIOLOGY(1680),
	COMBUSTION(2200),
	DYNAMITE(1900),
	ELECTRICITY(1900),
	RADIO(2200),
	RAILROAD(1900),
	REPLACEABLE_PARTS(1900),
	STEAM_POWER(1680),
	TELEGRAPH(2200);
	
	public final int cost;
	public final ArrayList<Technology> requiredTechnologies = new ArrayList<>();

	static
	{
		ANIMAL_HUSBANDRY.requiredTechnologies.add(AGRICULTURE);
		ARCHERY.requiredTechnologies.add(AGRICULTURE);
		BRONZE_WORKING.requiredTechnologies.add(MINING);
		CALENDAR.requiredTechnologies.add(POTTERY);
		MASONRY.requiredTechnologies.add(MINING);
		MINING.requiredTechnologies.add(AGRICULTURE);
		POTTERY.requiredTechnologies.add(AGRICULTURE);
		THE_WHEEL.requiredTechnologies.add(ANIMAL_HUSBANDRY);
		TRAPPING.requiredTechnologies.add(ANIMAL_HUSBANDRY);
		WRITING.requiredTechnologies.add(POTTERY);
		CONSTRUCTION.requiredTechnologies.add(MASONRY);
		HORSEBACK_RIDING.requiredTechnologies.add(THE_WHEEL);
		IRON_WORKING.requiredTechnologies.add(BRONZE_WORKING);
		MATHEMATICS.requiredTechnologies.add(THE_WHEEL);
		MATHEMATICS.requiredTechnologies.add(ARCHERY);
		PHILOSOPHY.requiredTechnologies.add(WRITING);
		CHIVALRY.requiredTechnologies.add(CIVIL_SERVICE);
		CHIVALRY.requiredTechnologies.add(HORSEBACK_RIDING);
		CHIVALRY.requiredTechnologies.add(CURRENCY);
		CIVIL_SERVICE.requiredTechnologies.add(PHILOSOPHY);
		CIVIL_SERVICE.requiredTechnologies.add(TRAPPING);
		CURRENCY.requiredTechnologies.add(MATHEMATICS);
		EDUCATION.requiredTechnologies.add(THEOLOGY);
		ENGINEERING.requiredTechnologies.add(MATHEMATICS);
		ENGINEERING.requiredTechnologies.add(CONSTRUCTION);
		MACHINERY.requiredTechnologies.add(ENGINEERING);
		METAL_CASTING.requiredTechnologies.add(IRON_WORKING);
		PHYSICS.requiredTechnologies.add(ENGINEERING);
		PHYSICS.requiredTechnologies.add(METAL_CASTING);
		STEEL.requiredTechnologies.add(METAL_CASTING);
		THEOLOGY.requiredTechnologies.add(CALENDAR);
		THEOLOGY.requiredTechnologies.add(PHILOSOPHY);
		ACOUSTICS.requiredTechnologies.add(EDUCATION);
		ARCHAEOLOGY.requiredTechnologies.add(ACOUSTICS);
		BANKING.requiredTechnologies.add(EDUCATION);
		BANKING.requiredTechnologies.add(CHIVALRY);
		CHEMISTRY.requiredTechnologies.add(GUN_POWDER);
		ECONOMICS.requiredTechnologies.add(BANKING);
		ECONOMICS.requiredTechnologies.add(PRINTING_PRESS);
		FERTILIZER.requiredTechnologies.add(CHEMISTRY);
		GUN_POWDER.requiredTechnologies.add(PHYSICS);
		GUN_POWDER.requiredTechnologies.add(STEEL);
		METALLURGY.requiredTechnologies.add(GUN_POWDER);
		MILITARY_SCIENCE.requiredTechnologies.add(ECONOMICS);
		MILITARY_SCIENCE.requiredTechnologies.add(CHEMISTRY);
		PRINTING_PRESS.requiredTechnologies.add(MACHINERY);
		PRINTING_PRESS.requiredTechnologies.add(PHYSICS);
		RIFLING.requiredTechnologies.add(METALLURGY);
		SCIENTIFIC_THEORY.requiredTechnologies.add(ACOUSTICS);
		BIOLOGY.requiredTechnologies.add(ARCHAEOLOGY);
		BIOLOGY.requiredTechnologies.add(SCIENTIFIC_THEORY);
		COMBUSTION.requiredTechnologies.add(REPLACEABLE_PARTS);
		COMBUSTION.requiredTechnologies.add(RAILROAD);
		COMBUSTION.requiredTechnologies.add(DYNAMITE);
		DYNAMITE.requiredTechnologies.add(FERTILIZER);
		DYNAMITE.requiredTechnologies.add(RIFLING);
		ELECTRICITY.requiredTechnologies.add(BIOLOGY);
		ELECTRICITY.requiredTechnologies.add(STEAM_POWER);
		RADIO.requiredTechnologies.add(ELECTRICITY);
		RAILROAD.requiredTechnologies.add(STEAM_POWER);
		REPLACEABLE_PARTS.requiredTechnologies.add(STEAM_POWER);
		STEAM_POWER.requiredTechnologies.add(SCIENTIFIC_THEORY);
		STEAM_POWER.requiredTechnologies.add(MILITARY_SCIENCE);
		TELEGRAPH.requiredTechnologies.add(ELECTRICITY);
	}
	
	Technology(int cost)
	{
		this.cost = cost;
	}
}





















