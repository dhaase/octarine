package com.codepoetics.octarine.joins;

import com.codepoetics.joink.Fetcher;
import com.codepoetics.joink.Index;
import com.codepoetics.joink.JoinKey;
import com.codepoetics.joink.Tuple2;
import com.codepoetics.octarine.records.ListKey;
import com.codepoetics.octarine.records.Record;

import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Stream;

import static com.codepoetics.octarine.joins.RecordMerges.recordIntoRecord;
import static com.codepoetics.octarine.joins.RecordMerges.recordsIntoRecord;

public class RecordJoiner<K extends Comparable<K>> {

    private final Index<K, Record> leftIndex;
    private final JoinKey<Record, K> primaryKey;

    public RecordJoiner(Index<K, Record> leftIndex, JoinKey<Record, K> primaryKey) {
        this.leftIndex = leftIndex;
        this.primaryKey = primaryKey;
    }

    public Stream<Record> manyToOne(Collection<? extends Record> rights) {
        return manyToOne(rights.stream());
    }

    public Stream<Record> manyToOne(Stream<? extends Record> rights) {
        return merge(rights, leftIndex::manyToOne).map(recordIntoRecord);
    }

    public Stream<Record> manyToOne(Fetcher<K, Record> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::manyToOne).map(recordIntoRecord);
    }


    public Stream<Record> strictManyToOne(Collection<? extends Record> rights) {
        return strictManyToOne(rights.stream());
    }

    public Stream<Record> strictManyToOne(Stream<? extends Record> rights) {
        return merge(rights, leftIndex::strictManyToOne).map(recordIntoRecord);
    }

    public Stream<Record> strictManyToOne(Fetcher<K, Record> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictManyToOne).map(recordIntoRecord);
    }

    public Stream<Record> oneToMany(Collection<? extends Record> rights, ListKey<Record> manyKey) {
        return oneToMany(rights.stream(), manyKey);
    }

    public Stream<Record> oneToMany(Stream<? extends Record> rights, ListKey<Record> manyKey) {
        return merge(rights, leftIndex::oneToMany).map(recordsIntoRecord(manyKey));
    }

    public Stream<Record> oneToMany(Fetcher<K, Record> fetcher, ListKey<Record> manyKey) {
        return fetchAndMerge(fetcher, leftIndex::oneToMany).map(recordsIntoRecord(manyKey));
    }

    public Stream<Record> strictOneToMany(Collection<? extends Record> rights, ListKey<Record> manyKey) {
        return strictOneToMany(rights.stream(), manyKey);
    }

    public Stream<Record> strictOneToMany(Stream<? extends Record> rights, ListKey<Record> manyKey) {
        return merge(rights, leftIndex::strictOneToMany).map(recordsIntoRecord(manyKey));
    }

    public Stream<Record> strictOneToMany(Fetcher<K, Record> fetcher, ListKey<Record> manyKey) {
        return fetchAndMerge(fetcher, leftIndex::strictOneToMany).map(recordsIntoRecord(manyKey));
    }

    public Stream<Record> strictOneToOne(Collection<? extends Record> rights) {
        return strictOneToOne(rights.stream());
    }

    public Stream<Record> strictOneToOne(Stream<? extends Record> rights) {
        return merge(rights, leftIndex::strictOneToOne).map(recordIntoRecord);
    }

    public Stream<Record> strictOneToOne(Fetcher<K, Record> fetcher) {
        return fetchAndMerge(fetcher, leftIndex::strictOneToOne).map(recordIntoRecord);
    }

    private <RS> Stream<Tuple2<Record, RS>> merge(Stream<? extends Record> rights, Function<Index<K, Record>, Stream<Tuple2<Record, RS>>> merger) {
        return merger.apply(primaryKey.index(rights));
    }

    private <RS> Stream<Tuple2<Record, RS>> fetchAndMerge(Fetcher<K, Record> fetcher, Function<Index<K, Record>, Stream<Tuple2<Record, RS>>> merger) {
        Collection<? extends Record> rights = fetcher.fetch(leftIndex.keys());
        return merge(rights.stream(), merger);
    }
}