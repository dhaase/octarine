package com.codepoetics.octarine.json.example;

import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.json.deserialisation.JsonRecordDeserialiser;
import com.codepoetics.octarine.json.serialisation.JsonRecordSerialiser;
import com.codepoetics.octarine.json.serialisation.JsonSerialisers;
import com.codepoetics.octarine.records.KeySet;
import com.codepoetics.octarine.records.Schema;

import static com.codepoetics.octarine.Octarine.$L;
import static com.codepoetics.octarine.json.deserialisation.JsonDeserialisers.ofString;

public interface Address {

    public static final KeySet mandatoryKeys = new KeySet();
    public static final ListKey<String> addressLines = mandatoryKeys.add($L("addressLines"));

    public static final JsonRecordSerialiser serialiser = JsonRecordSerialiser.builder()
            .writeList(addressLines, JsonSerialisers.toString)
            .get();

    public static final JsonRecordDeserialiser deserialiser = JsonRecordDeserialiser.builder()
            .readList(addressLines, ofString)
            .get();

    public static final Schema<Address> schema = (record, validationErrors) ->
            mandatoryKeys.accept(record, validationErrors);
}
