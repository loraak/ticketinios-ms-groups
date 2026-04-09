package com.example.ticketinios.ms_groups.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateGrupoRequest(
    @NotBlank String nombre,
    @NotBlank String descripcion
) {}