package com.reactive.streams.data.utils;

import com.mongodb.bulk.BulkWriteResult;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.reactivestreams.client.MongoCollection;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

import static com.reactive.streams.data.utils.DataFlowGenerate.paritionList;
import static com.reactive.streams.data.utils.SubscriberHelpers.ListObservableSubscriber;
import static com.reactive.streams.data.utils.SubscriberHelpers.ObservableSubscriber;


@Slf4j
@UtilityClass
public class DataFlowAsyncChangeLogsUtils {

    private static final ListObservableSubscriber<Document> listSubscriber
            = new ListObservableSubscriber<Document>();
    private static final ObservableSubscriber<BulkWriteResult> observaleSubscriber
            = new ObservableSubscriber<>();

    public void doDataFlowAsyncChange(MongoCollection<Document> collection,
                                      Executor executor,
                                      Function<Document, UpdateOneModel<Document>> update) throws Throwable {
        changeUnitAsyncVersion(collection, executor, update);
    }

    public void changeUnitAsyncVersion(MongoCollection<Document> collection,
                                       Executor executor,
                                       Function<Document, UpdateOneModel<Document>> update) throws Throwable {
        collection.find(new Document()).subscribe(listSubscriber);

        List<UpdateOneModel<Document>> received = listSubscriber.get(300, TimeUnit.SECONDS)
                .stream()
                .filter(Objects::nonNull)
                .map(update)
                .toList();

        paritionList(received, 1000)
                .forEach(item -> {
                    collection.bulkWrite(item).subscribe(observaleSubscriber);
                    try {
                        log.info("Updated size : " + observaleSubscriber.get(300, TimeUnit.SECONDS).size());
                    } catch (Throwable e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
