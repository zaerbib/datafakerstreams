package com.reactive.streams.data.utils;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.UpdateManyModel;
import com.mongodb.client.result.UpdateResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;


@Slf4j
@UtilityClass
public class DataFlowAsyncChangeLogsUtils {

    private static final SubscriberHelpers.ListObservableSubscriber<Document> listSubscriber
            = new SubscriberHelpers.ListObservableSubscriber<>();
    private static final SubscriberHelpers.ObservableSubscriber<BulkWriteResult> observaleSubscriber
            = new SubscriberHelpers.ObservableSubscriber<>();
    private static final SubscriberHelpers.ObservableSubscriber<UpdateResult> observaleSubscriberUpdateMany
            = new SubscriberHelpers.ObservableSubscriber<>();

    public void doDataFlowAsyncChangeV2(MongoCollection<Document> collection,
                                        Executor executor,
                                        UnaryOperator<Document> update,
                                        Supplier<Document> filter) throws Throwable {
        changeUnitAsyncVersion2(collection, executor, update, filter);
    }

    @SuppressWarnings("unused")
    public void doDataFlowUpdateMany(MongoCollection<Document> collection,
                                     Supplier<Document> filterField,
                                     Supplier<Document> update) throws Throwable {
        changeUnitUpdateMany(collection, filterField, update);
    }

    private void changeUnitAsyncVersion2(MongoCollection<Document> collection,
                                         @SuppressWarnings("unused") Executor executor,
                                         UnaryOperator<Document> update,
                                         Supplier<Document> filter) throws Throwable {
        collection.find(new Document()).subscribe(listSubscriber);

        List<Document> received = listSubscriber.get(300, TimeUnit.SECONDS)
                .stream()
                .filter(Objects::nonNull)
                .map(update)
                .toList();

        DataFlowGenerate.paritionList(received, 1000)
                .forEach(item -> {
                    collection.bulkWrite(Collections.singletonList(new UpdateManyModel<>(filter.get(), item)))
                            .subscribe(observaleSubscriber);
                    try {
                        observaleSubscriber.get(600, TimeUnit.SECONDS);
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });

        log.info("Mongock competed !!!");
    }

    private void changeUnitUpdateMany(MongoCollection<Document> collection,
                                      Supplier<Document> filterField,
                                      Supplier<Document> update) throws Throwable {
        collection.updateMany(filterField.get(), update.get()).subscribe(observaleSubscriberUpdateMany);
        log.info("Update result : " + observaleSubscriberUpdateMany.get(300, TimeUnit.SECONDS));
    }
}
