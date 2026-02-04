package com.nimbus.feedback.model;

import java.time.Instant;
import java.util.UUID;

public class Feedback {
    public String id;
    public String descricao;
    public int nota;
    public String urgencia;
    public String dataEnvio;

    public Feedback() {}

    public Feedback(String descricao, int nota) {
        this.id = UUID.randomUUID().toString();
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = calcularUrgencia(nota);
        this.dataEnvio = Instant.now().toString();
    }

    private String calcularUrgencia(int nota) {
        if (nota <= 4) return "URGENTE";
        if (nota <= 6) return "ATENCAO";
        return "NORMAL";
    }
}
