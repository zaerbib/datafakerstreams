package com.reactive.streams.data.controller;

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
    public BsonValue generateOne() {
        return dataFlowService.generateOne();
    }
}
