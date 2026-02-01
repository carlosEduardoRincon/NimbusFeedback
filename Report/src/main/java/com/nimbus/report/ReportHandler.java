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
        sb.append("id,descricao,nota,urgencia,dataEnvio\n");

        Map<String, AttributeValue> lastKey = null;
        do {
            ScanRequest.Builder scanBuilder = ScanRequest.builder().tableName(table);
            if (lastKey != null && !lastKey.isEmpty()) {
                scanBuilder = scanBuilder.exclusiveStartKey(lastKey);
            }
            ScanResponse resp = dynamo.scan(scanBuilder.build());

            resp.items().forEach(item -> sb.append(formatLine(item)).append('\n'));
            lastKey = resp.lastEvaluatedKey();
        } while (lastKey != null && !lastKey.isEmpty());

        return sb.toString();
    }

    private String formatLine(Map<String, AttributeValue> item) {
        String id = getS(item, "id");
        String desc = getS(item, "descricao");
        String nota = getN(item, "nota");
        String urg = getS(item, "urgencia");
        String data = getS(item, "dataEnvio");

        return String.join(",",
                csvQuote(id),
                csvQuote(desc),
                csvQuote(nota),
                csvQuote(urg),
                csvQuote(data)
        );
    }

    private static String getS(Map<String, AttributeValue> item, String key) {
        AttributeValue v = item.get(key);
        return v == null ? "" : v.s();
    }

    private static String getN(Map<String, AttributeValue> item, String key) {
        AttributeValue v = item.get(key);
        return v == null ? "" : v.n();
    }

    private static String csvQuote(String v) {
        if (v == null) return "\"\"";
        String escaped = v.replace("\"", "\"\"");
        return "\"" + escaped + "\"";
    }

    private static String buildObjectKey() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
        String date = fmt.format(ZonedDateTime.now(ZoneOffset.UTC));
        return "reports/feedbacks-" + date + ".csv";
    }
}
