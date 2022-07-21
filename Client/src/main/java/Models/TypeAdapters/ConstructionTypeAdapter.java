package Models.TypeAdapters;

import Models.City.Construction;
import Models.Resources.Resource;
import com.google.gson.*;

import java.lang.reflect.Type;

public class ConstructionTypeAdapter implements JsonSerializer<Construction>, JsonDeserializer<Construction> {
    @Override
    public JsonElement serialize(Construction construction, Type type, JsonSerializationContext context) {
        JsonObject result = new JsonObject();
        result.add("type", new JsonPrimitive(construction.getClass().getCanonicalName()));
        result.add("properties", context.serialize(construction, construction.getClass()));

        return result;
    }

    @Override
    public Construction deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
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
