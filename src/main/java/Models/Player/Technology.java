package Models.Player;

import Models.City.Product;

import java.util.ArrayList;

public enum Technology implements Product
{
	AGRICULTURE(20,0),
	ANIMAL_HUSBANDRY(35,0),
	ARCHERY(35,0),
	BRONZE_WORKING(55,0),
	CALENDAR(70,0),
	MASONRY(55,0),
	MINING(35,0),
	POTTERY(35,0),
	THE_WHEEL(55,0),
	TRAPPING(55,0),
	WRITING(55,0),
	CONSTRUCTION(100,0),
	HORSEBACK_RIDING(100,0),
	IRON_WORKING(150,0),
	MATHEMATICS(100,0),
	PHILOSOPHY(100,0),
	CHIVALRY(440,0),
	CIVIL_SERVICE(400,0),
	CURRENCY(250,0),
	EDUCATION(440,0),
	ENGINEERING(250,0),
	MACHINERY(440,0),
	METAL_CASTING(240,0),
	PHYSICS(440,0),
	STEEL(440,0),
	THEOLOGY(250,0),
	ACOUSTICS(650,0),
	ARCHAEOLOGY(1300,0),
	BANKING(650,0),
	CHEMISTRY(900,0),
	ECONOMICS(900,0),
	FERTILIZER(1300,0),
	GUN_POWDER(680,0),
	METALLURGY(900,0),
	MILITARY_SCIENCE(1300,0),
	PRINTING_PRESS(650,0),
	RIFLING(1425,0),
	SCIENTIFIC_THEORY(1300,0),
	BIOLOGY(1680,0),
	COMBUSTION(2200,0),
	DYNAMITE(1900,0),
	ELECTRICITY(1900,0),
	RADIO(2200,0),
	RAILROAD(1900,0),
	REPLACEABLE_PARTS(1900,0),
	STEAM_POWER(1680,0),
	TELEGRAPH(2200,0);
	
	public final int cost;
	public final ArrayList<Technology> requiredTechnologies = new ArrayList<>();
	public int inLineTurn;

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
		THEOLOGY.requiredTechnologies.add(THEOLOGY);
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
	
	Technology(int cost, int inLineTurn)
	{
		this.cost = cost;
		this.inLineTurn = inLineTurn;
	}

	public void addTurn(int amount)
	{
		inLineTurn += amount;
	}
}





















