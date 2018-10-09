package com.codepoetics.octarine.json.deserialisation;

import java.io.IOException;

public class JsonDeserialisationException extends RuntimeException {
    public JsonDeserialisationException(IOException e) {
        super(e);
    }
}
