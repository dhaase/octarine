package com.codepoetics.octarine.json.serialisation;

import com.codepoetics.octarine.records.Key;
import com.codepoetics.octarine.records.Record;
import com.fasterxml.jackson.core.JsonGenerator;
import org.pcollections.PVector;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;

public final class JsonRecordSerialiser implements JsonSafeSerialiser<Record> {

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder implements Supplier<JsonRecordSerialiser> {
        private final Map<Key<?>, BiConsumer<JsonGenerator, ?>> serialiserMap = new LinkedHashMap<>();

        public <V> Builder write(Key<? extends V> key, String fieldName, BiConsumer<JsonGenerator, ? super V> serialiser) {
            BiConsumer<JsonGenerator, ? extends V> keyValueWriter = (JsonGenerator generator, V value) -> {
                try {
                    generator.writeFieldName(fieldName);
                    serialiser.accept(generator, value);
                } catch (IOException e) {
                    throw new JsonSerialisationException(e);
                }
            };
            serialiserMap.put(key, keyValueWriter);
            return this;
        }

        public <V> Builder write(Key<? extends V> key, BiConsumer<JsonGenerator, ? super V> serialiser) {
            return write(key, key.name(), serialiser);
        }

        public <V> Builder write(Key<? extends V> key, Supplier<BiConsumer<JsonGenerator, ? super V>> serialiserSupplier) {
            return write(key, serialiserSupplier.get());
        }

        public <V> Builder write(Key<? extends V> key, String fieldName, Supplier<BiConsumer<JsonGenerator, ? super V>> serialiserSupplier) {
            return write(key, fieldName, serialiserSupplier.get());
        }

        public Builder writeString(Key<String> key) {
            return write(key, JsonSerialisers.toString);
        }

        public Builder writeString(Key<String> key, String fieldName) {
            return write(key, fieldName, JsonSerialisers.toString);
        }

        public <V> Builder writeToString(Key<V> key) {
            return writeToString(key, Object::toString);
        }

        public <V> Builder writeToString(Key<V> key, String fieldName) {
            return writeToString(key, fieldName, Object::toString);
        }

        public <V> Builder writeToString(Key<V> key, Function<V, String> converter) {
            return writeToString(key, key.name(), converter);
        }

        public <V> Builder writeToString(Key<V> key, String fieldName, Function<V, String> converter) {
            return write(key, fieldName, (g, v) -> JsonSerialisers.toString.accept(g, converter.apply(v)));
        }

        public Builder writeInteger(Key<Integer> key) {
            return write(key, JsonSerialisers.toInteger);
        }

        public Builder writeInteger(Key<Integer> key, String fieldName) {
            return write(key, fieldName, JsonSerialisers.toInteger);
        }

        public Builder writeDouble(Key<Double> key) {
            return write(key, JsonSerialisers.toDouble);
        }

        public Builder writeDouble(Key<Double> key, String fieldName) {
            return write(key, fieldName, JsonSerialisers.toDouble);
        }

        public Builder writeLong(Key<Long> key) {
            return write(key, JsonSerialisers.toLong);
        }

        public Builder writeLong(Key<Long> key, String fieldName) {
            return write(key, fieldName, JsonSerialisers.toLong);
        }


        public Builder writeBoolean(Key<Boolean> key) {
            return write(key, JsonSerialisers.toBoolean);
        }

        public Builder writeBoolean(Key<Boolean> key, String fieldName) {
            return write(key, fieldName, JsonSerialisers.toBoolean);
        }

        public <V> Builder writeList(Key<PVector<V>> key, JsonSerialiser<V> itemSerialiser) {
            return write(key, JsonListSerialiser.writingItemsWith(itemSerialiser));
        }

        public <V> Builder writeList(Key<PVector<V>> key, String fieldName, JsonSerialiser<V> itemSerialiser) {
            return write(key, fieldName, JsonListSerialiser.writingItemsWith(itemSerialiser));
        }

        public <V> Builder writeMap(Key<Map<String, V>> key, JsonSerialiser<V> valueSerialiser) {
            return write(key, JsonMapSerialiser.writingValuesWith(valueSerialiser));
        }

        public <V> Builder writeMap(Key<Map<String, V>> key, String fieldName, JsonSerialiser<V> valueSerialiser) {
            return write(key, fieldName, JsonMapSerialiser.writingValuesWith(valueSerialiser));
        }

        @Override
        public JsonRecordSerialiser get() {
            return new JsonRecordSerialiser(serialiserMap);
        }
    }

    private final Map<Key<?>, BiConsumer<JsonGenerator, ?>> serialiserMap;

    public JsonRecordSerialiser(Map<Key<?>, BiConsumer<JsonGenerator, ?>> serialiserMap) {
        this.serialiserMap = serialiserMap;
    }

    @Override
    public void unsafeAccept(JsonGenerator generator, Record record) throws IOException {
        generator.writeStartObject();
        serialiserMap.keySet().forEach(k ->
                k.get(record).ifPresent(value ->
                        ((BiConsumer<JsonGenerator, Object>)  serialiserMap.get(k)).accept(generator, value)
        ));
        generator.writeEndObject();
    }


}
