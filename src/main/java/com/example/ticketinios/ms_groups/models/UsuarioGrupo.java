package com.example.ticketinios.ms_groups.models;

import java.time.LocalDateTime;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "grupo_miembros")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioGrupo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(name = "usuario_id", nullable = false, columnDefinition = "uuid")
    private UUID usuarioId;

    @Column(name = "nombre_completo", length = 200)
    private String nombreCompleto;

    @Column(name = "grupo_id", nullable = false, columnDefinition = "uuid")
    private UUID grupoId;

    @Column(name = "union_en")
    private LocalDateTime unionEn;
}