package com.nimbus.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import software.amazon.awssdk.services.dynamodb.model.ScanRequest;
import software.amazon.awssdk.services.dynamodb.model.ScanResponse;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

public class ReportHandler implements RequestHandler<Map<String, Object>, String> {

    private final DynamoDbClient dynamo = DynamoDbClient.create();
    private final S3Client s3 = S3Client.create();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        String table = Optional.ofNullable(System.getenv("DYNAMODB_TABLE")).orElse("feedbacks");
        String bucket = System.getenv("REPORTS_BUCKET");
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("REPORTS_BUCKET não definido");
        }

        String csv = buildCsv(table);
        String key = buildObjectKey();

        PutObjectRequest put = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/csv")
                .build();

        s3.putObject(put, RequestBody.fromBytes(csv.getBytes(StandardCharsets.UTF_8)));

        return String.format("Relatório gerado: s3://%s/%s (%d bytes)", bucket, key, csv.length());
    }

    private String buildCsv(String table) {
        StringBuilder sb = new StringBuilder();
        sb.append("descricao,urgencia,dataEnvio\n");

        java.util.Map<String, Integer> porDia = new java.util.HashMap<>();
        java.util.Map<String, Integer> porUrgencia = new java.util.HashMap<>();

        Map<String, AttributeValue> lastKey = null;
        do {
            ScanRequest.Builder scanBuilder = ScanRequest.builder().tableName(table);
            if (lastKey != null && !lastKey.isEmpty()) {
                scanBuilder = scanBuilder.exclusiveStartKey(lastKey);
            }
            ScanResponse resp = dynamo.scan(scanBuilder.build());

            for (java.util.Map<String, AttributeValue> item : resp.items()) {
                String descricao = item.containsKey("descricao") && item.get("descricao").s() != null ? item.get("descricao").s() : "";
                String urgencia = item.containsKey("urgencia") && item.get("urgencia").s() != null ? item.get("urgencia").s() : "";
                String dataEnvio = item.containsKey("dataEnvio") && item.get("dataEnvio").s() != null ? item.get("dataEnvio").s() : "";

                sb.append(descricao).append(',').append(urgencia).append(',').append(dataEnvio).append('\n');

                String dia = (dataEnvio != null && dataEnvio.length() >= 10) ? dataEnvio.substring(0, 10) : dataEnvio;
                porDia.put(dia, porDia.getOrDefault(dia, 0) + 1);
                porUrgencia.put(urgencia, porUrgencia.getOrDefault(urgencia, 0) + 1);
            }
            lastKey = resp.lastEvaluatedKey();
        } while (lastKey != null && !lastKey.isEmpty());

        sb.append('\n');
        sb.append("quantidade_por_dia,dia,quantidade\n");
        for (java.util.Map.Entry<String, Integer> e : porDia.entrySet()) {
            sb.append("por-dia,").append(e.getKey()).append(',').append(e.getValue()).append('\n');
        }
        sb.append('\n');
        sb.append("quantidade_por_urgencia,urgencia,quantidade\n");
        for (java.util.Map.Entry<String, Integer> e : porUrgencia.entrySet()) {
            sb.append("por-urgencia,").append(e.getKey()).append(',').append(e.getValue()).append('\n');
        }

        return sb.toString();
    }

    private static String buildObjectKey() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
        String date = fmt.format(ZonedDateTime.now(ZoneOffset.UTC));
        return "reports/feedbacks-" + date + ".csv";
    }
}
