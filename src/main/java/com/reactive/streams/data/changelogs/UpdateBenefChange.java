package com.reactive.streams.data.changelogs;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.mongodb.client.model.UpdateOneModel;
import com.mongodb.reactivestreams.client.MongoCollection;
import com.reactive.streams.data.data.Benef;
import io.mongock.api.annotations.BeforeExecution;
import io.mongock.api.annotations.ChangeUnit;
import io.mongock.api.annotations.Execution;
import io.mongock.api.annotations.RollbackExecution;
import org.bson.Document;
import org.bson.types.ObjectId;

import java.util.concurrent.Executor;

import static com.reactive.streams.data.utils.DataFlowAsyncChangeLogsUtils.*;
import static com.reactive.streams.data.utils.JsonPatchUtils.*;
import static com.mongodb.client.model.Filters.*;

@ChangeUnit(id = "update-benef-change", order = "001", author = "dev")
public class UpdateBenefChange {

    private final static ObjectMapper mapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @BeforeExecution
    private void beforeExcution(MongoCollection<Document> collection,
                                Executor executor) throws Throwable {
        // think about it
    }
    @Execution
    public void changeSet(MongoCollection<Document> collection,
                          Executor executor) throws Throwable {
        doDataFlowAsyncChange(collection, executor, this::updateBenefField);
    }

    @RollbackExecution
    public void rollBackExecution(MongoCollection<Document> collection) {
        // think about it
    }

    private UpdateOneModel<Document> updateBenefField(Document document) {
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
            return fromDocumentToUpdateOneModel(applyJsonPatch(mapper, document, patchOp));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private UpdateOneModel<Document> fromDocumentToUpdateOneModel(Document document) {
        return new UpdateOneModel<>(eq("uniqueId", document.get("uniqueId")),
                new Document("$set",
                        new Document("benef", document.get("benef"))));

    }
}
