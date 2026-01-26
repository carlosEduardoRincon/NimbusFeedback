package com.nimbus.feedback.service;

import com.nimbus.feedback.model.Feedback;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.PutItemRequest;

import java.util.Map;

public class DynamoService {
    private final DynamoDbClient client = DynamoDbClient.create();

    private final String tabela = resolveTableName();

    private static String resolveTableName() {
        String fromEnv = System.getenv("DYNAMODB_TABLE");
        if (fromEnv == null || fromEnv.isBlank()) {
            return "feedbacks";
        }
        return fromEnv.trim();
    }

    public void salvar(Feedback feedback) {
        client.putItem(PutItemRequest.builder()
                .tableName(tabela)
                .item(Map.of(
                        "id", AttributeValue.fromS(feedback.id),
                        "descricao", AttributeValue.fromS(feedback.descricao),
                        "nota", AttributeValue.fromN(String.valueOf(feedback.nota)),
                        "urgencia", AttributeValue.fromS(feedback.urgencia),
                        "dataEnvio", AttributeValue.fromS(feedback.dataEnvio)
                ))
                .build());
    }
}