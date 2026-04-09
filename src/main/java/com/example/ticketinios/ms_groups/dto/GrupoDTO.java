package com.example.ticketinios.ms_groups.dto;

import java.time.LocalDateTime;
import java.util.UUID;

import lombok.Builder;

@Builder
public record GrupoDTO(
    UUID id,
    String nombre,
    String descripcion,
    String creador,       
    LocalDateTime creado_en, 
    int integrantes,     
    boolean activo
) {}