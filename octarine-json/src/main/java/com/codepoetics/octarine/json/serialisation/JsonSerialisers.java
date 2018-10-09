package com.codepoetics.octarine.json.serialisation;

import com.fasterxml.jackson.core.JsonGenerator;

public final class JsonSerialisers {
    private JsonSerialisers() {
    }

    public static final JsonSafeSerialiser<String> toString = JsonGenerator::writeString;
    public static final JsonSafeSerialiser<Integer> toInteger = JsonGenerator::writeNumber;
    public static final JsonSafeSerialiser<Double> toDouble = JsonGenerator::writeNumber;
    public static final JsonSafeSerialiser<Long> toLong = JsonGenerator::writeNumber;
    public static final JsonSafeSerialiser<Boolean> toBoolean = JsonGenerator::writeBoolean;

}
