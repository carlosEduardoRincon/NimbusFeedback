package com.nimbus.feedback.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayV2HTTPResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbus.feedback.dto.FeedbackDTO;
import com.nimbus.feedback.service.FeedbackService;

public class LambdaHandler implements RequestHandler<APIGatewayV2HTTPEvent, APIGatewayV2HTTPResponse> {

    private final FeedbackService service;
    private final ObjectMapper mapper;

    public LambdaHandler() {
        this.service = new FeedbackService();
        this.mapper = new ObjectMapper();
    }

    @Override
    public APIGatewayV2HTTPResponse handleRequest(APIGatewayV2HTTPEvent event, Context context) {
        try {
            String body = event.getBody();
            context.getLogger().log("Received body: " + body);

            FeedbackDTO dto = mapper.readValue(body, FeedbackDTO.class);
            context.getLogger().log("Parsed DTO: " + dto.getDescricao() + ", nota: " + dto.getNota());

            service.processarNovoFeedback(dto.getDescricao(), dto.getNota());

            context.getLogger().log("Feedback processado com sucesso");

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(200)
                    .withHeaders(java.util.Map.of("Content-Type", "application/json"))
                    .withBody("{\"message\":\"Feedback recebido com sucesso!\"}")
                    .build();

        } catch (Exception e) {
            context.getLogger().log("Erro ao processar feedback: " + e.getMessage());
            e.printStackTrace();

            return APIGatewayV2HTTPResponse.builder()
                    .withStatusCode(500)
                    .withHeaders(java.util.Map.of("Content-Type", "application/json"))
                    .withBody("{\"error\":\"Erro interno do servidor: " + e.getMessage() + "\"}")
                    .build();
        }
    }
}

