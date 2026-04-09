package com.example.ticketinios.ms_groups.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketinios.ms_groups.models.GrupoPermiso;

public interface GrupoPermisoRepository extends JpaRepository<GrupoPermiso, UUID> {

    List<GrupoPermiso> findByGrupoIdAndUsuarioId(UUID grupoId, UUID usuarioId);

    boolean existsByGrupoIdAndUsuarioIdAndPermisoId(UUID grupoId, UUID usuarioId, String permisoId);

    void deleteByGrupoIdAndUsuarioIdAndPermisoId(UUID grupoId, UUID usuarioId, String permisoId);

    List<GrupoPermiso> findByGrupoId(UUID grupoId);
}