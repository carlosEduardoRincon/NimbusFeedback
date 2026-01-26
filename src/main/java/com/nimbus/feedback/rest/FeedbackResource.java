package com.nimbus.feedback.rest;

import com.nimbus.feedback.model.AvaliacaoRequest;
import com.nimbus.feedback.model.AvaliacaoResponse;
import jakarta.validation.Valid;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.time.Instant;

@Path("/avaliacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class FeedbackResource {

    @POST
    public Response criar(@Valid AvaliacaoRequest request) {
        var urgencia = calcularUrgencia(request.getNota());
        var resp = AvaliacaoResponse.of(
                request.getDescricao(),
                request.getNota(),
                urgencia,
                Instant.now()
        );
        return Response.status(Response.Status.CREATED).entity(resp).build();
    }

    private String calcularUrgencia(int nota) {
        if (nota <= 3) return "CRITICA";
        if (nota <= 6) return "ALTA";
        if (nota <= 8) return "MEDIA";
        return "BAIXA";
        }
}
