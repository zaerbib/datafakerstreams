package com.reactive.streams.data.service;

import com.mongodb.client.result.InsertOneResult;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.mongodb.reactivestreams.client.MongoDatabase;
import lombok.extern.slf4j.Slf4j;
import org.bson.BsonValue;
import org.bson.Document;
import org.springframework.stereotype.Service;

import static com.reactive.streams.data.utils.DataFlowGenerate.*;
import static com.reactive.streams.data.utils.SubscriberHelpers.*;

@Service
@Slf4j
public class DataFlowService {

    private final MongoDatabase mongoDatabase;

    public DataFlowService(MongoDatabase mongoDatabase) {
        this.mongoDatabase = mongoDatabase;
    }

    public BsonValue generateOne() {
        MongoCollection<Document> collection = mongoDatabase.getCollection("dataFlowAsync");
        ObservableSubscriber<InsertOneResult> subscriber = new ObservableSubscriber<InsertOneResult>();
        collection.insertOne(fromDataFlowToDocument(generateOnDataFlow()))
                .subscribe(subscriber);
        return subscriber.getReceived().get(0).getInsertedId();
    }
}
