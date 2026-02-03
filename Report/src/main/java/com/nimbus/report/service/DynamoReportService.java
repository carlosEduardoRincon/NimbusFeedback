package com.nimbus.report.service;

import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DynamoReportService {

    private final DynamoDbClient dynamo;

    public DynamoReportService() {
        this(DynamoDbClient.create());
    }

    public DynamoReportService(DynamoDbClient dynamo) {
        this.dynamo = dynamo;
    }

    public List<Map<String, AttributeValue>> fetchAll(String tableName) {
        List<Map<String, AttributeValue>> items = new ArrayList<>();
        Map<String, AttributeValue> lastKey;
        Map<String, AttributeValue> startKey = null;

        do {
            ScanRequest.Builder builder = ScanRequest.builder().tableName(tableName);
            if (startKey != null && !startKey.isEmpty()) {
                builder = builder.exclusiveStartKey(startKey);
            }
            ScanResponse response = dynamo.scan(builder.build());
            items.addAll(response.items());
            lastKey = response.lastEvaluatedKey();
            startKey = lastKey;
        } while (lastKey != null && !lastKey.isEmpty());

        return items;
    }
}
