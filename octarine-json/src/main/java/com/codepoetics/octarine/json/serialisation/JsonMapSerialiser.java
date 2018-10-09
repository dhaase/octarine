package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Map;

public final class JsonMapSerialiser<T> implements JsonSafeSerialiser<Map<String, ? extends T>> {

    public static <T> JsonMapSerialiser<T> writingValuesWith(JsonSerialiser<? super T> valueSerialiser) {
        return new JsonMapSerialiser<>(valueSerialiser);
    }

    private final JsonSerialiser<? super T> valueSerialiser;

    private JsonMapSerialiser(JsonSerialiser<? super T> valueSerialiser) {
        this.valueSerialiser = valueSerialiser;
    }

    @Override
    public void unsafeAccept(JsonGenerator j, Map<String, ? extends T> ts) throws IOException {
        j.writeStartObject();
        JsonSafeSerialiser<Map.Entry<String, ? extends T>> entrySafeSerialiser = (j2, e) -> {
            j2.writeFieldName(e.getKey());
            valueSerialiser.accept(j2, e.getValue());
        };
        ts.entrySet().forEach(e -> {
            entrySafeSerialiser.accept(j, e);
        });
        j.writeEndObject();
    }
}
