/*
 * Copyright © 2017 VMware, Inc. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */
package com.codepoetics.octarine.bson.serialisation;


import org.bson.*;
import org.bson.types.ObjectId;

import java.util.Date;

public interface BsonSerialisers {

    BsonSerialiser<Boolean> toBoolean = (v) -> new BsonBoolean(v);
    BsonSerialiser<Date> toDate = (v) -> new BsonDateTime(v.getTime());
    BsonSerialiser<Double> toDouble = (v) -> new BsonDouble(v);
    BsonSerialiser<Integer> toInteger = (v) -> new BsonInt32(v);
    BsonSerialiser<Long> toLong = (v) -> new BsonInt64(v);
    BsonSerialiser<ObjectId> toObjectId = (v) -> new BsonObjectId(v);
    BsonSerialiser<String> toString = (v) -> new BsonString(v);
}
