package com.nimbus.report.service;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.nio.charset.StandardCharsets;

public class S3ReportService {

    private final S3Client s3;

    public S3ReportService() {
        this(S3Client.create());
    }

    public S3ReportService(S3Client s3) {
        this.s3 = s3;
    }

    public void uploadCsv(String bucket, String key, String csvContent) {
        PutObjectRequest req = PutObjectRequest.builder()
                .bucket(bucket)
                .key(key)
                .contentType("text/csv; charset=utf-8")
                .build();

        s3.putObject(req, RequestBody.fromString(csvContent, StandardCharsets.UTF_8));
    }
}
