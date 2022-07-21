package Models.City;

import Models.Player.Technology;

public enum BuildingType {
    BARRACKS(80, 1, Technology.BRONZE_WORKING, null),
    GRANARY(100, 1, Technology.POTTERY, null),
    LIBRARY(80, 1, Technology.WRITING, null),
    MONUMENT(60, 1, null, null),
    WALLS(100, 1, Technology.MASONRY, null),
    WATER_MILL(120, 2, Technology.THE_WHEEL, null),
    ARMORY(130, 3, Technology.IRON_WORKING, BARRACKS),
    BURIAL_TOMB(120, 0, Technology.PHILOSOPHY, null),
    CIRCUS(150, 3, Technology.HORSEBACK_RIDING, null),
    COLOSSEUM(150, 3, Technology.CONSTRUCTION, null),
    COURTHOUSE(200, 5, Technology.MATHEMATICS, null),
    STABLE(100, 1, Technology.HORSEBACK_RIDING, null),
    TEMPLE(120, 2, Technology.PHILOSOPHY, MONUMENT),
    CASTLE(200, 3, Technology.CHIVALRY, WALLS),
    FORGE(150, 2, Technology.METAL_CASTING, null),
    GARDEN(120, 2, Technology.THEOLOGY, null),
    MARKET(120, 0, Technology.CURRENCY, null),
    MINT(120, 0, Technology.CURRENCY, null),
    MONASTERY(120, 2, Technology.THEOLOGY, null),
    UNIVERSITY(200, 3, Technology.EDUCATION, LIBRARY),
    WORKSHOP(100, 2, Technology.METAL_CASTING, null),
    BANK(220, 0, Technology.BANKING, MARKET),
    MILITARY_ACADEMY(350, 3, Technology.MILITARY_SCIENCE, BARRACKS),
    OPERA_HOUSE(220, 3, Technology.ACOUSTICS, TEMPLE),
    MUSEUM(350, 3, Technology.ARCHAEOLOGY, OPERA_HOUSE),
    PUBLIC_SCHOOL(350, 3, Technology.SCIENTIFIC_THEORY, UNIVERSITY),
    SATRAPS_COURT(220, 0, Technology.BANKING, MARKET),
    THEATER(300, 5, Technology.PRINTING_PRESS, COLOSSEUM),
    WINDMILL(180, 2, Technology.ECONOMICS, null),
    ARSENAL(350, 3, Technology.RAILROAD, MILITARY_ACADEMY),
    BROADCAST_TOWER(600, 3, Technology.RADIO, MUSEUM),
    FACTORY(300, 3, Technology.STEAM_POWER, null),
    HOSPITAL(400, 2, Technology.BIOLOGY, null),
    MILITARY_BASE(450, 4, Technology.TELEGRAPH, CASTLE),
    STOCK_EXCHANGE(650, 0, Technology.ELECTRICITY, BANK),
    PALACE(0, 0, null, null);

    public final int cost;
    public final int maintenanceCost;
    public final Technology requiredTechnology;
    public final BuildingType requiredBuilding;

    BuildingType(int cost, int maintenanceCost, Technology requiredTechnology, BuildingType requiredBuilding) {
        this.cost = cost;
        this.maintenanceCost = maintenanceCost;
        this.requiredTechnology = requiredTechnology;
        this.requiredBuilding = requiredBuilding;
    }

}



















