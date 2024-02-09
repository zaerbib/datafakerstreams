package com.reactive.streams.data.controller;

import com.mongodb.client.result.InsertOneResult;
import com.reactive.streams.data.service.DataFlowService;
import org.bson.BsonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/flow")

public class DataFlowController {

    private final DataFlowService dataFlowService;

    public DataFlowController(DataFlowService dataFlowService) {
        this.dataFlowService = dataFlowService;
    }

    @GetMapping("/one")
    public InsertOneResult generateOne() throws Throwable {
        return dataFlowService.generateOne();
    }

    @GetMapping("/10K")
    public Integer generate10K() throws Throwable {
        return dataFlowService.generate10K();
    }

    @GetMapping("/100K")
    public Integer generate100K() throws Throwable {
        return dataFlowService.generate100K();
    }

    @GetMapping("/1M")
    public Integer generate1M() throws Throwable {
        return dataFlowService.generate1M();
    }
}
