package com.nimbus.report;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nimbus.report.service.DynamoReportService;
import com.nimbus.report.service.S3ReportService;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class ReportHandler implements RequestHandler<Map<String, Object>, String> {

    private final DynamoReportService dynamoService = new DynamoReportService();
    private final S3ReportService s3Service = new S3ReportService();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        String table = Optional.ofNullable(System.getenv("DYNAMODB_TABLE")).orElse("feedbacks");
        String bucket = System.getenv("REPORTS_BUCKET");
        if (bucket == null || bucket.isBlank()) {
            throw new IllegalStateException("REPORTS_BUCKET não definido");
        }

        List<Map<String, AttributeValue>> items = dynamoService.fetchAll(table);
        String csv = buildCsv(items);
        String key = buildObjectKey();

        s3Service.uploadCsv(bucket, key, csv);

        return String.format("Relatório gerado: s3://%s/%s (%d bytes)", bucket, key, csv.length());
    }

    private String buildCsv(List<Map<String, AttributeValue>> items) {
        StringBuilder sb = new StringBuilder();
        sb.append("descricao,urgencia,dataEnvio\n");

        java.util.Map<String, Integer> porDia = new java.util.HashMap<>();
        java.util.Map<String, Integer> porUrgencia = new java.util.HashMap<>();

        for (Map<String, AttributeValue> item : items) {
            String descricao = item.containsKey("descricao") && item.get("descricao").s() != null ? item.get("descricao").s() : "";
            String urgencia = item.containsKey("urgencia") && item.get("urgencia").s() != null ? item.get("urgencia").s() : "";
            String dataEnvio = item.containsKey("dataEnvio") && item.get("dataEnvio").s() != null ? item.get("dataEnvio").s() : "";

            sb.append(descricao).append(',').append(urgencia).append(',').append(dataEnvio).append('\n');

            String dia = (dataEnvio != null && dataEnvio.length() >= 10) ? dataEnvio.substring(0, 10) : dataEnvio;
            porDia.put(dia, porDia.getOrDefault(dia, 0) + 1);
            porUrgencia.put(urgencia, porUrgencia.getOrDefault(urgencia, 0) + 1);
        }

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
