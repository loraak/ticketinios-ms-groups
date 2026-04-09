package com.example.ticketinios.ms_groups.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketinios.ms_groups.models.UsuarioGrupo;

public interface UsuarioGrupoRepository extends JpaRepository<UsuarioGrupo, UUID> {
    int countByGrupoId(UUID grupoId);
}