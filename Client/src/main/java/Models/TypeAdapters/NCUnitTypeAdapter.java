package Models.TypeAdapters;

import Models.Units.NonCombatUnits.NonCombatUnit;
import com.google.gson.*;

import java.lang.reflect.Type;

public class NCUnitTypeAdapter implements JsonSerializer<NonCombatUnit>, JsonDeserializer<NonCombatUnit> {
    @Override
    public JsonElement serialize(NonCombatUnit resource, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(resource.getClass().getCanonicalName()));
        result.add("properties", context.serialize(resource, resource.getClass()));

        return result;
    }

    @Override
    public NonCombatUnit deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
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
