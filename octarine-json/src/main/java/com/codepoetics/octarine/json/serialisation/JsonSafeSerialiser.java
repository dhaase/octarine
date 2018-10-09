package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;

public interface JsonSafeSerialiser<T> extends JsonSerialiser<T> {
    default void accept(JsonGenerator j, T value) {
        try {
            unsafeAccept(j, value);
        } catch (IOException e) {
            throw new JsonSerialisationException(e);
        }
    }

    void unsafeAccept(JsonGenerator j, T value) throws IOException;
}
