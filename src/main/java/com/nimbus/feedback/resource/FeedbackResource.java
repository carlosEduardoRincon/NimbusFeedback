package com.nimbus.feedback.resource;

import com.nimbus.feedback.dto.FeedbackDTO;
import com.nimbus.feedback.service.FeedbackService;
import jakarta.inject.Inject;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/avaliacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @Inject
    FeedbackService service;

    @POST
    public Response receber(FeedbackDTO dto) {
        service.processarNovoFeedback(dto.getDescricao(), dto.getNota());
        return Response.ok("Feedback recebido com sucesso!").build();
    }
}