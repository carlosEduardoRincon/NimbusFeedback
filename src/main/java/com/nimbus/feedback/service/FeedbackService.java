package com.nimbus.feedback.service;

import com.nimbus.feedback.model.Feedback;

public class FeedbackService {
    private final DynamoService dynamo = new DynamoService();
    private final SnsService sns = new SnsService();

    public void processarNovoFeedback(String descricao, int nota) {
        Feedback feedback = new Feedback(descricao, nota);

        dynamo.salvar(feedback);

        if ("URGENTE".equals(feedback.urgencia)) {
            sns.enviarAlerta(feedback);
        }
    }
}
