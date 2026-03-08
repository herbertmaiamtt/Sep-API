package com.necklogic.sepapi.dto;

import java.util.List;
import java.util.UUID;

public record TurmaResponseDTO(
        UUID id,
        String nome,
        int totalAlunos,
        List<AlunoResponseDTO> alunos
) {}