package com.reactive.streams.data.changelogs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.reactive.streams.data.data.Benef;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

import java.util.concurrent.Executor;

import static com.reactive.streams.data.utils.DataFlowAsyncChangeLogsUtils.*;

@ChangeUnit(id = "init-benef-change", order = "001", author = "dev")
@SuppressWarnings("unused")
public class InitBenefChange {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @Execution
    public void changeSet(MongoCollection<Document> collection,
                          Executor executor) throws Throwable {
        doDataFlowUpdateMany(collection,
                () -> new Document("benef", null),
                () -> new Document("$set", new Document("benef", benefToDoc(Benef.builder()
                        .invest(0.0)
                        .diff(0.0)
                        .state("normal")
                        .build()))));
    }

    @RollbackExecution
    public void rollBackExecution(MongoCollection<Document> collection) {
        // think about it
    }

    private Document benefToDoc(Benef benef) {
        return new Document("invest", benef.getInvest())
                .append("diff", benef.getDiff())
                .append("state", benef.getState());
    }
}
