package com.reactive.streams.data.changelogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.reactivestreams.client.MongoCollection;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;

import java.util.concurrent.Executor;

import static com.reactive.streams.data.utils.DataFlowAsyncChangeLogsUtils.doDataFlowAsyncChangeV2;
import static com.reactive.streams.data.utils.JsonPatchUtils.applyJsonPatch;

@ChangeUnit(id = "update-benef-change", order = "002", author = "dev")
@SuppressWarnings("unused")
public class UpdateBenefChange {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());


    @Execution
    public void changeSet(MongoCollection<Document> collection,
                          Executor executor) throws Throwable {

        // when update one by one
        doDataFlowAsyncChangeV2(collection, executor,
                this::updateBenefFielOneShot,
                Document::new);
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
                           "path": "/benef/invest",
                           "value": $invest },
                         { "op": "replace",
                           "path": "/benef/diff",
                           "value": $diff}
                        ]
                    """.replace("$invest", mapper.writeValueAsString(invest))
                    .replace("$diff", mapper.writeValueAsString(diff));
            return fromDocumentToDocmentUpdate(applyJsonPatch(mapper, document, patchOp));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private Document fromDocumentToDocmentUpdate(Document document) {
        return new Document("$set",
                new Document("benef", document.get("benef")));
    }

}
