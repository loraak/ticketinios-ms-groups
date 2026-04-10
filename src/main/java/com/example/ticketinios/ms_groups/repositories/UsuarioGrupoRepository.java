package com.example.ticketinios.ms_groups.repositories;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.ticketinios.ms_groups.models.UsuarioGrupo;

public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, UUID> {
    @Query("SELECT COUNT(ug) FROM UsuarioGrupo ug WHERE ug.grupoId = :grupoId")
    int countByGrupoId(@Param("grupoId") UUID grupoId);

    @Query("SELECT ug.grupoId FROM UsuarioGrupo ug WHERE ug.usuarioId = :usuarioId")
    List<UUID> findGrupoIdsByUsuarioId(@Param("usuarioId") UUID usuarioId);

    List<UUID> findUsuarioIdsByGrupoId(@Param("grupoId") UUID grupoId); 

    List<UsuarioGrupo> findByGrupoId(UUID grupoId);
}