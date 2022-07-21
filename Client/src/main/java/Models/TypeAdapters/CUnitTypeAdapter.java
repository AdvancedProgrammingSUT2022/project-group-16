package Models.TypeAdapters;

import Models.Units.CombatUnits.CombatUnit;
import Models.Units.NonCombatUnits.NonCombatUnit;
import com.google.gson.*;

import java.lang.reflect.Type;

public class CUnitTypeAdapter implements JsonSerializer<CombatUnit>, JsonDeserializer<CombatUnit> {
    @Override
    public JsonElement serialize(CombatUnit resource, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(resource.getClass().getCanonicalName()));
        result.add("properties", context.serialize(resource, resource.getClass()));

        return result;
    }

    @Override
    public CombatUnit deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject jsonObject = jsonElement.getAsJsonObject();
        String t = jsonObject.get("type").getAsString();
        JsonElement element = jsonObject.get("properties");

        try {
            return context.deserialize(element, Class.forName(t));
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
