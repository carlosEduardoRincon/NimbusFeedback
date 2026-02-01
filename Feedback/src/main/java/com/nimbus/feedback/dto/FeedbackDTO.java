package com.nimbus.feedback.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class FeedbackDTO {
    @NotBlank(message = "descricao é obrigatória")
    private String descricao;

    @Min(value = 0, message = "nota mínima é 0")
    @Max(value = 10, message = "nota máxima é 10")
    private int nota;

    public FeedbackDTO() {
    }

    public FeedbackDTO(String descricao, int nota) {
        this.descricao = descricao;
        this.nota = nota;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public int getNota() {
        return nota;
    }

    public void setNota(int nota) {
        this.nota = nota;
    }
}
