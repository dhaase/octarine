package com.codepoetics.octarine.json.deserialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.Value;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import org.pcollections.PMap;
import org.pcollections.PVector;

import java.io.IOException;
import java.util.*;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;

public final class JsonRecordDeserialiser implements JsonSafeDeserialiser<Record> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Supplier<JsonRecordDeserialiser> {

        private final Map<String, Function<JsonParser, Value>> valueReaders = new HashMap<>();

        Builder() {
        }

        @Override
        public JsonRecordDeserialiser get() {
            return new JsonRecordDeserialiser((fieldName, parser) ->
                    Optional.ofNullable(valueReaders.get(fieldName)).map(r -> r.apply(parser)));
        }

        public <V> Builder read(Key<? super V> key, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, key.name(), deserialiser);
        }

        public <V> Builder read(Key<? super V> key, String fieldName, Function<JsonParser, ? extends V> deserialiser) {
            valueReaders.put(fieldName, p -> key.of(deserialiser.apply(p)));
            return this;
        }

        public <V> Builder read(Key<V> key, Supplier<Function<JsonParser, V>> deserialiserSupplier) {
            return read(key, deserialiserSupplier.get());
        }

        public <V> Builder read(Key<V> key, String fieldName, Supplier<Function<JsonParser, V>> deserialiserSupplier) {
            return read(key, fieldName, deserialiserSupplier.get());
        }

        public Builder readString(Key<String> key) {
            return read(key, JsonDeserialisers.ofString);
        }

        public <V> Builder readFromString(Key<? super V> key, Function<String, V> converter) {
            return read(key, JsonDeserialisers.ofString.andThen(converter));
        }

        public Builder readInteger(Key<Integer> key) {
            return read(key, JsonDeserialisers.ofInteger);
        }

        public Builder readDouble(Key<Double> key) {
            return read(key, JsonDeserialisers.ofDouble);
        }

        public Builder readBoolean(Key<Boolean> key) {
            return read(key, JsonDeserialisers.ofBoolean);
        }

        public Builder readLong(Key<Long> key) {
            return read(key, JsonDeserialisers.ofLong);
        }

        public <V> Builder readList(Key<PVector<V>> key, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, JsonListDeserialiser.readingItemsWith(deserialiser));
        }

        public <V> Builder readMap(Key<PMap<String, V>> key, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, JsonMapDeserialiser.readingValuesWith(deserialiser));
        }

        public <V> Builder readValidRecord(Key<Valid<V>> key, Function<JsonParser, Validation<V>> deserialiser) {
            return read(key, JsonDeserialisers.ofValid(deserialiser));
        }

        public Builder readString(Key<String> key, String fieldName) {
            return read(key, fieldName, JsonDeserialisers.ofString);
        }

        public <V> Builder readFromString(Key<? super V> key, String fieldName, Function<String, V> converter) {
            return read(key, fieldName, JsonDeserialisers.ofString.andThen(converter));
        }

        public Builder readInteger(Key<Integer> key, String fieldName) {
            return read(key, fieldName, JsonDeserialisers.ofInteger);
        }

        public Builder readDouble(Key<Double> key, String fieldName) {
            return read(key, fieldName, JsonDeserialisers.ofDouble);
        }

        public Builder readBoolean(Key<Boolean> key, String fieldName) {
            return read(key, fieldName, JsonDeserialisers.ofBoolean);
        }

        public Builder readLong(Key<Long> key, String fieldName) {
            return read(key, fieldName, JsonDeserialisers.ofLong);
        }

        public <V> Builder readList(Key<PVector<V>> key, String fieldName, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, fieldName, JsonListDeserialiser.readingItemsWith(deserialiser));
        }

        public <V> Builder readMap(Key<PMap<String, V>> key, String fieldName, Function<JsonParser, ? extends V> deserialiser) {
            return read(key, fieldName, JsonMapDeserialiser.readingValuesWith(deserialiser));
        }

        public <V> Builder readValidRecord(Key<Valid<V>> key, String fieldName, Function<JsonParser, Validation<V>> deserialiser) {
            return read(key, fieldName, JsonDeserialisers.ofValid(deserialiser));
        }
    }

    private final BiFunction<String, JsonParser, Optional<Value>> parserMapper;

    private JsonRecordDeserialiser(BiFunction<String, JsonParser, Optional<Value>> parserMapper) {
        this.parserMapper = parserMapper;
    }

    @Override
    public Record applyUnsafe(JsonParser parser) throws IOException {
        List<Value> values = new ArrayList<>();

        if (parser.nextToken() == JsonToken.END_OBJECT) {
            return Record.empty();
        }

        /*
         * When the parser has hit the end of input nextToken() will always return null.
         * So need to prevent infinite loops we check the parser closed flag.
         */
        while (!parser.isClosed() && (parser.nextValue() != JsonToken.END_OBJECT)) {
            String fieldName = parser.getCurrentName();
            if (JsonToken.VALUE_NULL != parser.getCurrentToken()) {
                Optional<Value> result = parserMapper.apply(fieldName, parser);
                if (result.isPresent()) {
                    values.add(result.get());
                } else {
                    consumeUnwanted(parser);
                }
            }
        }

        return Record.of(values);
    }

    private void consumeUnwanted(JsonParser parser) throws IOException {
        if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
            while (parser.nextValue() != JsonToken.END_OBJECT) {
                if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                    consumeUnwanted(parser);
                }
            }
        }
    }

    public <S> JsonDeserialiser<Validation<S>> validAgainst(Schema<S> schema) {
        return new JsonValidRecordDeserialiser<>(schema, this);
    }

}
