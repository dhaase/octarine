package com.codepoetics.octarine.json.deserialisation;

import com.fasterxml.jackson.core.JsonParser;

import java.io.IOException;

public interface JsonSafeDeserialiser<S> extends JsonDeserialiser<S> {
    default S apply(JsonParser p) {
        try {
            return applyUnsafe(p);
        } catch (IOException e) {
            throw new JsonDeserialisationException(e);
        }
    }

    S applyUnsafe(JsonParser p) throws IOException;
}
