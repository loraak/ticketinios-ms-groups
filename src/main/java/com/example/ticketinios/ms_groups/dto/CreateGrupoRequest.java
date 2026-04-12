package com.example.ticketinios.ms_groups.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateGrupoRequest(
    @NotBlank String nombre,
    @NotBlank String descripcion,
    @NotBlank String creadorNombre
) {}