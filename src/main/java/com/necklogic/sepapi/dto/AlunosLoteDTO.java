package com.necklogic.sepapi.dto;

import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import java.util.UUID;

public record AlunosLoteDTO(

        @NotEmpty List<UUID> alunoIds

) {}