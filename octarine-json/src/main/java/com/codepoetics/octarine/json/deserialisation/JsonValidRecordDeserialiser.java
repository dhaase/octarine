package com.codepoetics.octarine.json.deserialisation;

import com.codepoetics.octarine.records.Record;
import com.codepoetics.octarine.records.RecordValidationException;
import com.codepoetics.octarine.records.Schema;
import com.codepoetics.octarine.records.Validation;
import com.fasterxml.jackson.core.JsonParser;

import java.io.Reader;

class JsonValidRecordDeserialiser<S> implements JsonDeserialiser<Validation<S>> {

    private final Schema<S> schema;
    private final JsonRecordDeserialiser reader;

    JsonValidRecordDeserialiser(Schema<S> schema, JsonRecordDeserialiser reader) {
        this.schema = schema;
        this.reader = reader;
    }

    @Override
    public Validation<S> apply(JsonParser parser) {
        try {
            return validated(reader.apply(parser));
        } catch (RecordValidationException e) {
            return e.toValidation();
        }
    }

    @Override
    public Validation<S> fromReader(Reader reader) {
        try {
            return validated(this.reader.fromReader(reader));
        } catch (RecordValidationException e) {
            return e.toValidation();
        }
    }

    private Validation<S> validated(Record record) {
        return schema.validate(record);
    }
}
