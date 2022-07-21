package Models.Units.CombatUnits;

import Models.Player.Technology;
import Models.Resources.ResourceType;

public enum MidRangeType {

    SCOUT(25, 4, 2, null, null, false),
    SPEARMAN(50, 7, 2, null, Technology.BRONZE_WORKING, false),
    WARRIOR(40, 6, 2, null, null, false),
    HORSEMAN(80, 12, 4, ResourceType.HORSES, Technology.HORSEBACK_RIDING, true),
    SWORDSMAN(80, 11, 2, ResourceType.IRON, Technology.IRON_WORKING, false),
    KNIGHT(150, 18, 3, ResourceType.HORSES, Technology.CHIVALRY, true),
    LSWORDSMAN(150, 18, 3, ResourceType.IRON, Technology.STEEL, false),
    PIKE_MAN(100, 10, 2, null, Technology.CIVIL_SERVICE, false),
    CAVALRY(260, 25, 3, ResourceType.HORSES, Technology.MILITARY_SCIENCE, true),
    LANCER(220, 22, 4, ResourceType.HORSES, Technology.METALLURGY, true),
    MUSKET_MAN(120, 16, 2, null, Technology.GUN_POWDER, false),
    RIFLEMAN(200, 25, 2, null, Technology.RIFLING, false),
    ANTI_TANK(300, 32, 2, null, Technology.REPLACEABLE_PARTS, false),
    INFANTRY(300, 36, 2, null, Technology.REPLACEABLE_PARTS, false),
    PANZER(450, 60, 5, null, Technology.COMBUSTION, false),
    TANK(450, 50, 4, null, Technology.COMBUSTION, false);

    public final int cost;
    public final int combatStrength;
    public final int movement;
    public final ResourceType requiredSource;
    public final Technology requiredTech;
    public final boolean isMounted;

    MidRangeType(int cost, int combatStrength, int movement, ResourceType requiredSource, Technology requiredTech, boolean isMounted) {
        this.cost = cost;
        this.combatStrength = combatStrength;
        this.movement = movement;
        this.requiredTech = requiredTech;
        this.requiredSource = requiredSource;
        this.isMounted = isMounted;
    }

    public int getCost() {
        return cost;
    }

    public int getCombatStrength() {
        return combatStrength;
    }

    public ResourceType getRequiredSource() {
        return requiredSource;
    }

    public Technology getRequiredTech() {
        return requiredTech;
    }
}
