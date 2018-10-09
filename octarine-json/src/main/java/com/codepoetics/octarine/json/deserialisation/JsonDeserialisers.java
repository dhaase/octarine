package com.codepoetics.octarine.json.deserialisation;

import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import com.fasterxml.jackson.core.JsonParser;

import java.util.function.Function;

public final class JsonDeserialisers {

    private JsonDeserialisers() {
    }

    public static final JsonSafeDeserialiser<String> ofString = JsonParser::getValueAsString;
    public static final JsonSafeDeserialiser<Integer> ofInteger = JsonParser::getIntValue;
    public static final JsonSafeDeserialiser<Boolean> ofBoolean = JsonParser::getBooleanValue;
    public static final JsonSafeDeserialiser<Long> ofLong = JsonParser::getLongValue;
    public static final JsonSafeDeserialiser<Double> ofDouble = JsonParser::getDoubleValue;

    public static <S> JsonSafeDeserialiser<Valid<S>> ofValid(Function<JsonParser, ? extends Validation<S>> extractor) {
        return parser -> extractor.apply(parser).get();
    }

}
