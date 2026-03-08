package com.necklogic.sepapi.dto;

import jakarta.validation.constraints.NotBlank;

public record TurmaRequestDTO(

        @NotBlank String nome

) {}