package com.nimbus.feedback.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.nimbus.feedback.service.FeedbackService;

import java.util.Map;

public class ReceberFeedbackLambda implements RequestHandler<Map<String, Object>, String> {

    private final FeedbackService service = new FeedbackService();

    @Override
    public String handleRequest(Map<String, Object> input, Context context) {
        String descricao = (String) input.get("descricao");
        int nota = (Integer) input.get("nota");

        service.processarNovoFeedback(descricao, nota);

        return "Feedback recebido com sucesso!";
    }
}