package com.reactive.streams.data.changelogs;

import com.fasterxml.jackson.core.JsonProcessingException;
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
import static com.reactive.streams.data.utils.JsonPatchUtils.applyJsonPatch;

@ChangeUnit(id = "update-benef-change", order = "001", author = "dev")
@SuppressWarnings("unused")
public class UpdateBenefChange {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Execution
    public void changeSet(MongoCollection<Document> collection,
                          Executor executor) throws Throwable {
        /* when update many */
        /*doDataFlowUpdateMany(collection,
                () -> new Document("benef", null),
                () -> new Document("$set", new Document("benef", benefToDoc(Benef.builder()
                        .invest(0.0)
                        .diff(0.0)
                        .state("normal")
                        .build()))));*/

        // when update one by one
        doDataFlowAsyncChangeV2(collection, executor,
                this::updateBenefFielOneShot,
                () -> new Document("benef", null));
    }

    @RollbackExecution
    public void rollBackExecution(MongoCollection<Document> collection) {
        // think about it
    }

    private Document updateBenefFielOneShot(Document document) {
        Double diff = document.getDouble("close") - document.getDouble("open");
        Double invest = document.getDouble("volume") - document.getDouble("dividend");

        try {
            String patchOp = """
                        [
                         { "op": "replace",
                           "path": "/benef",
                           "value": $object }
                        ]
                    """.replace("$object",
                    mapper.writeValueAsString(Benef.builder()
                            .invest(invest)
                            .diff(diff)
                            .state("up")
                            .build()));
            return fromDocumentToDocmentUpdate(applyJsonPatch(mapper, document, patchOp));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Document fromDocumentToDocmentUpdate(Document document) {
        return new Document("$set",
                        new Document("benef", document.get("benef")));
    }

    private Document benefToDoc(Benef benef) {
        return new Document("invest", benef.getInvest())
                .append("diff", benef.getDiff())
                .append("state", benef.getState());
    }

}
