package com.reactive.streams.data.service;

import com.mongodb.client.result.InsertManyResult;
import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.reactive.streams.data.utils.DataFlowGenerate;
import lombok.extern.slf4j.Slf4j;
import org.bson.Document;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import static com.reactive.streams.data.utils.DataFlowGenerate.fromDataFlowToDocument;
import static com.reactive.streams.data.utils.DataFlowGenerate.generateOnDataFlow;
import static com.reactive.streams.data.utils.SubscriberHelpers.ObservableSubscriber;

@Service
@Slf4j
public class DataFlowService {

    private final MongoCollection<Document> collection;
    private final Executor executor;

    public DataFlowService(MongoCollection<Document> collection, Executor executor) {
        this.collection = collection;
        this.executor = executor;
    }

    public InsertOneResult generateOne() throws Throwable {
        ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
        collection.insertOne(fromDataFlowToDocument(generateOnDataFlow()))
                .subscribe(subscriber);
        return subscriber.get(300, TimeUnit.MILLISECONDS).get(0);
    }

    public Integer generate10K() throws Throwable {
        return generateNDataFlow(10_000);
    }

    public Integer generate100K() throws Throwable {
        return generateNDataFlow(100_000);
    }

    public Integer generate1M() throws Throwable {
        return generateNDataFlow(1_000_000);
    }

    private Integer generateNDataFlow(Integer number) throws Throwable {
        AtomicInteger atomicInt = new AtomicInteger(0);
        DataFlowGenerate.paritionList(DataFlowGenerate.generateNDataFlow(number), 5000)
                .forEach(item -> {
                    CompletableFuture.runAsync(() -> {
                        ObservableSubscriber<InsertManyResult> subscriber = new ObservableSubscriber<>();
                        collection.insertMany(item).subscribe(subscriber);
                        try {
                            atomicInt.addAndGet(subscriber.get(5000, TimeUnit.MILLISECONDS).size());
                        } catch (Throwable e) {
                            throw new RuntimeException(e);
                        }
                    });
                });

        return number;
    }
}
