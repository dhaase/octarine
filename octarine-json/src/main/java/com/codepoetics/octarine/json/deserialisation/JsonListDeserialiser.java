package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.PVector;
import org.pcollections.TreePVector;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Function;

public final class JsonListDeserialiser<V> implements JsonSafeDeserialiser<PVector<V>> {

    public static <V> JsonListDeserialiser<V> readingItemsWith(Function<JsonParser, ? extends V> itemDeserialiser) {
        return new JsonListDeserialiser<>(itemDeserialiser);
    }

    public static JsonListDeserialiser<String> readingStrings() {
        return readingItemsWith(JsonDeserialisers.ofString);
    }

    public static JsonListDeserialiser<Integer> readingIntegers() {
        return readingItemsWith(JsonDeserialisers.ofInteger);
    }

    public static JsonListDeserialiser<Long> readingLongs() {
        return readingItemsWith(JsonDeserialisers.ofLong);
    }

    public static JsonListDeserialiser<Double> readingDoubles() {
        return readingItemsWith(JsonDeserialisers.ofDouble);
    }

    public static JsonListDeserialiser<Boolean> readingBooleans() {
        return readingItemsWith(JsonDeserialisers.ofBoolean);
    }

    private final Function<JsonParser, ? extends V> itemDeserialiser;

    private JsonListDeserialiser(Function<JsonParser, ? extends V> itemDeserialiser) {
        this.itemDeserialiser = itemDeserialiser;
    }

    @Override
    public PVector<V> applyUnsafe(JsonParser p) throws IOException {
        List<V> values = new LinkedList<>();

        /*
         * If we are in the root context (not inside an object or list) then we need to consume the next token
         * before attempting to call child deserialisers.
         */
        if (p.getParsingContext().inRoot()) {
            if (p.nextToken() == JsonToken.END_ARRAY) {
                return TreePVector.empty();
            }
        }

        if (JsonToken.VALUE_NULL != p.getCurrentToken()) {
            /*
             * When the parser has hit the end of input nextToken() will always return null.
             * So need to prevent infinite loops we check the parser closed flag.
             */
            while (!p.isClosed() && (p.nextToken() != JsonToken.END_ARRAY)) {
                values.add(itemDeserialiser.apply(p));
            }
        }
        return TreePVector.from(values);
    }
}
