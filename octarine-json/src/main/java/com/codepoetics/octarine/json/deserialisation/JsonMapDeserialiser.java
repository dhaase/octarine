package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.HashTreePMap;
import org.pcollections.PMap;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public final class JsonMapDeserialiser<V> implements JsonSafeDeserialiser<PMap<String, V>> {

    public static <V> JsonMapDeserialiser<V> readingValuesWith(Function<JsonParser, ? extends V> valueDeserialiser) {
        return new JsonMapDeserialiser<>(valueDeserialiser);
    }

    public static JsonMapDeserialiser<String> readingStrings() {
        return readingValuesWith(JsonDeserialisers.ofString);
    }

    public static JsonMapDeserialiser<Integer> readingIntegers() {
        return readingValuesWith(JsonDeserialisers.ofInteger);
    }

    public static JsonMapDeserialiser<Long> readingLongs() {
        return readingValuesWith(JsonDeserialisers.ofLong);
    }

    public static JsonMapDeserialiser<Double> readingDoubles() {
        return readingValuesWith(JsonDeserialisers.ofDouble);
    }

    public static JsonMapDeserialiser<Boolean> readingBooleans() {
        return readingValuesWith(JsonDeserialisers.ofBoolean);
    }
    
    private final Function<JsonParser, ? extends V> valueDeserialiser;

    private JsonMapDeserialiser(Function<JsonParser, ? extends V> valueDeserialiser) {
        this.valueDeserialiser = valueDeserialiser;
    }

    @Override
    public PMap<String, V> applyUnsafe(JsonParser p) throws IOException {
        Map<String, V> values = new HashMap<>();

        if (p.nextToken() == JsonToken.END_OBJECT) {
            return HashTreePMap.empty();
        }
        while (p.nextValue() != JsonToken.END_OBJECT) {
            String fieldName = p.getCurrentName();
            values.put(fieldName, valueDeserialiser.apply(p));
        }

        return HashTreePMap.from(values);
    }
}
