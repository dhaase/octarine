package com.codepoetics.octarine.json.serialisation;


import java.io.IOException;

public class JsonSerialisationException extends RuntimeException {

    public JsonSerialisationException(IOException cause) {
        super(cause);
    }

    public IOException getIOExceptionCause() {
        return (IOException) getCause();
    }
}
