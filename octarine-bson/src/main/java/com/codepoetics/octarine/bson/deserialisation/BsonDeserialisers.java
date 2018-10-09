/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.deserialisation;


import com.codepoetics.octarine.records.Valid;
import com.codepoetics.octarine.records.Validation;
import org.bson.BsonValue;
import org.bson.types.ObjectId;

import java.util.Date;
import java.util.function.Function;

public interface BsonDeserialisers {

    BsonSafeDeserialiser<Boolean> ofBoolean = (v) -> v.asBoolean().getValue();

    BsonSafeDeserialiser<Date> ofDate = (v) -> new Date(v.asDateTime().getValue());

    BsonSafeDeserialiser<Double> ofDouble = (v) -> v.asDouble().getValue();

    BsonSafeDeserialiser<Integer> ofInteger = (v) -> v.asInt32().getValue();

    BsonSafeDeserialiser<Long> ofLong = (v) -> v.asInt64().getValue();

    BsonSafeDeserialiser<ObjectId> ofObjectId = (v) -> v.asObjectId().getValue();

    BsonSafeDeserialiser<String> ofString = (v) -> v.asString().getValue();

    static <V> BsonSafeDeserialiser<Valid<V>> ofValid(Function<BsonValue, ? extends Validation<V>> deserialiser) {
        return parser -> deserialiser.apply(parser).get();
    }
}
