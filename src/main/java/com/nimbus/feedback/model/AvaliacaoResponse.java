package com.nimbus.feedback.model;

import java.time.Instant;
import java.util.UUID;

public class AvaliacaoResponse {
    private String id;
    private String descricao;
    private int nota;
    private String urgencia;
    private Instant dataEnvio;

    public AvaliacaoResponse() {
    }

    public AvaliacaoResponse(String id, String descricao, int nota, String urgencia, Instant dataEnvio) {
        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
    }

    public static AvaliacaoResponse of(String descricao, int nota, String urgencia, Instant dataEnvio) {
        return new AvaliacaoResponse(UUID.randomUUID().toString(), descricao, nota, urgencia, dataEnvio);
    }

    public String getId() {
        return id;
    }

    public String getDescricao() {
        return descricao;
    }

    public int getNota() {
        return nota;
    }

    public String getUrgencia() {
        return urgencia;
    }

    public Instant getDataEnvio() {
        return dataEnvio;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }

    public void setUrgencia(String urgencia) {
        this.urgencia = urgencia;
    }

    public void setDataEnvio(Instant dataEnvio) {
        this.dataEnvio = dataEnvio;
    }
}
