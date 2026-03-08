package com.necklogic.sepapi.dto;

import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.UUID;

public record TurmaAtualizarDTO(
        @NotBlank String nome,
        List<UUID> alunoIds
) {}