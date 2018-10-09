package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.util.Collection;

public final class JsonListSerialiser<T> implements JsonSafeSerialiser<Collection<? extends T>> {
    public static <T> JsonListSerialiser<T> writingItemsWith(JsonSerialiser<? super T> itemSerialiser) {
        return new JsonListSerialiser<>(itemSerialiser);
    }

    private final JsonSerialiser<? super T> itemSerialiser;

    private JsonListSerialiser(JsonSerialiser<? super T> itemSerialiser) {
        this.itemSerialiser = itemSerialiser;
    }

    @Override
    public void unsafeAccept(JsonGenerator j, Collection<? extends T> ts) throws IOException {
        j.writeStartArray();
        ts.forEach(t -> itemSerialiser.accept(j, t));
        j.writeEndArray();
    }
}
