package com.example.ticketinios.ms_groups.dto;

import java.util.UUID;

import lombok.Builder;

@Builder
public record GrupoDTO(
    UUID id,
    String nombre,
    String descripcion,
    String creador,       
    int integrantes,     
    boolean activo
) {}