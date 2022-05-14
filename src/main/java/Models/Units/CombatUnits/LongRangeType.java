package Models.Units.CombatUnits;

import Models.Player.Technology;
import Models.Resources.ResourceType;

public enum LongRangeType {
    ARCHER(70,4,6,2,2,Technology.ARCHERY,null, false),
    CHARIOT_A(60,3,6,2,4,Technology.THE_WHEEL, ResourceType.HORSES, true),
    CATAPULT(100, 4, 14, 2,2,Technology.MATHEMATICS, ResourceType.IRON, false),
    CROSSMAN(120, 6, 12, 2, 2, Technology.MACHINERY, null, false),
    TREBUCHET(170,6, 20, 2, 2, Technology.PHYSICS, ResourceType.IRON, false),
    CANON(250, 10, 26, 2, 2, Technology.CHEMISTRY, null, false),
    ARTILLERY(420, 16, 32, 3, 2, Technology.DYNAMITE, null, false);

    public final int cost;
    public final int combatStrength;
    public final int rangedCombatStrength;
    public final int range;
    public final int movement;
    public final Technology requiredTech;
    public final ResourceType requiredSource;
    public final boolean isMounted;


    LongRangeType(int cost, int combatStrength, int rangedCombatStrength, int range, int movement, Technology requiredTech, ResourceType requiredSource, boolean isMounted) {
        this.cost = cost;
        this.combatStrength = combatStrength;
        this.rangedCombatStrength = rangedCombatStrength;
        this.range = range;
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

    public int getRange() {
        return range;
    }
}
