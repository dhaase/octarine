package com.codepoetics.octarine.json;

import com.codepoetics.octarine.json.deserialisation.JsonListDeserialiser;
import com.codepoetics.octarine.json.example.Address;
import com.codepoetics.octarine.json.example.Person;
import com.codepoetics.octarine.json.serialisation.JsonListSerialiser;
import com.codepoetics.octarine.json.serialisation.JsonMapSerialiser;
import com.codepoetics.octarine.json.serialisation.JsonReflectiveRecordSerialiser;
import com.codepoetics.octarine.records.Record;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.junit.Test;

import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static com.codepoetics.octarine.Octarine.$$;
import static com.codepoetics.octarine.json.serialisation.JsonSerialisers.toInteger;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

public class SerialisationTest {

    @Test
    public void
    writes_person_as_json() {
        String json = Person.serialiser.toString($$(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))));

        assertThat(json, equalTo("{\"name\":\"Dominic\",\"age\":39,\"favourite colour\":\"0xFF0000\",\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}"));
    }

    @Test
    public void
    tolerates_missing_values() {
        String json = Person.serialiser.toString($$(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))));

        assertThat(json, equalTo("{\"name\":\"Dominic\",\"age\":39,\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}"));
    }

    @Test public void
    writes_list_of_people_as_json() {
        String json = JsonListSerialiser.writingItemsWith(Person.serialiser).toString(asList(
                $$(
                        Person.name.of("Dominic"),
                        Person.age.of(39),
                        Person.favouriteColour.of(Color.RED),
                        Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP"))),
                $$(
                        Person.name.of("Oliver"),
                        Person.age.of(14),
                        Person.favouriteColour.of(Color.BLACK),
                        Person.address.of(Address.addressLines.of("22 Acacia Avenue", "VB6 5UX"))
                )));

        assertThat(json, equalTo("[{\"name\":\"Dominic\",\"age\":39,\"favourite colour\":\"0xFF0000\",\"address\":{\"addressLines\":[\"13 Rue Morgue\",\"PO3 1TP\"]}}," +
                                  "{\"name\":\"Oliver\",\"age\":14,\"favourite colour\":\"0x000000\",\"address\":{\"addressLines\":[\"22 Acacia Avenue\",\"VB6 5UX\"]}}]"));
    }

    @Test public void
    writes_list_of_maps_of_lists_of_ints_as_json() {
        Map<String, List<Integer>> map1 = new LinkedHashMap<>();
        map1.put("primes", asList(1,2,3,5,7));
        map1.put("evens", asList(2,4,6,8,10));

        Map<String, List<Integer>> map2 = new LinkedHashMap<>();
        map2.put("odds", asList(1,3,5,7,9));
        map2.put("powers", asList(2,4,8,16,32));

        List<Map<String, List<Integer>>> data = asList(map1, map2);

        String json = JsonListSerialiser.writingItemsWith(
                JsonMapSerialiser.writingValuesWith(
                        JsonListSerialiser.writingItemsWith(toInteger)))
                .toString(data);

        assertThat(json, equalTo("[{\"primes\":[1,2,3,5,7],\"evens\":[2,4,6,8,10]},{\"odds\":[1,3,5,7,9],\"powers\":[2,4,8,16,32]}]"));
    }

    @Test public void
    reflective_serialisation_works_too() throws JsonProcessingException {
        Record me = $$(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP")));

        ObjectMapper mapper = JsonReflectiveRecordSerialiser.mapperWith(new ColorJsonSerializer());

        String json = mapper.writeValueAsString(me);

        Record meToo = Person.deserialiser.fromString(json);

        assertThat(meToo, equalTo(me));
    }

    @Test public void
    reflectively_serialise_multiple_records() throws JsonProcessingException {
        Record me = $$(
                Person.name.of("Dominic"),
                Person.age.of(39),
                Person.favouriteColour.of(Color.RED),
                Person.address.of(Address.addressLines.of("13 Rue Morgue", "PO3 1TP")));

        Record you = $$(
                Person.name.of("Doppelganger"),
                Person.age.of(40),
                Person.favouriteColour.of(Color.BLUE),
                Person.address.of(Address.addressLines.of("23 Acacia Avenue", "VB6 5UX")));

        String json = JsonReflectiveRecordSerialiser.toJson(Stream.of(me, you), new ColorJsonSerializer());

        List<Record> people = JsonListDeserialiser.readingItemsWith(Person.deserialiser).fromString(json);

        assertThat(people, equalTo(Arrays.asList(me, you)));
    }

    private static class ColorJsonSerializer extends JsonSerializer<Color> {
        @Override
        public Class handledType() { return Color.class; }

        @Override
        public void serialize(Color o, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
            jsonGenerator.writeString(Person.colourToString.apply(o));
        }
    }
}
