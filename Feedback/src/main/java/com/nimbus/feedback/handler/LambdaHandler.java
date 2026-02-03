package com.nimbus.feedback.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbus.feedback.dto.FeedbackDTO;
import com.nimbus.feedback.service.FeedbackService;
import jakarta.inject.Inject;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    @Inject
    FeedbackService service;

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            String body = event.getBody();
            FeedbackDTO dto = mapper.readValue(body, FeedbackDTO.class);

            service.processarNovoFeedback(dto.getDescricao(), dto.getNota());

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(java.util.Map.of("Content-Type", "application/json"))
                    .withBody("{\"message\":\"Feedback recebido com sucesso!\"}")
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Erro ao processar feedback: " + e.getMessage());

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(java.util.Map.of("Content-Type", "application/json"))
                    .withBody("{\"error\":\"Erro interno do servidor\"}")
                    .build();
        }
    }
}
