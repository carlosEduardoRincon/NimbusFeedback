package com.nimbus.feedback.service;

import com.nimbus.feedback.model.Feedback;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;

public class SnsService {
    private final SnsClient client = SnsClient.create();
    private final String topicArn = System.getenv("SNS_TOPIC");

    public void enviarAlerta(Feedback feedback) {
        String msg = """
        Feedback crítico recebido:
        Descrição: %s
        Nota: %d
        Data: %s
        """.formatted(feedback.descricao, feedback.nota, feedback.dataEnvio);

        client.publish(PublishRequest.builder()
                .topicArn(topicArn)
                .subject("ALERTA CRÍTICO - Feedback")
                .message(msg)
                .build());
    }
}
