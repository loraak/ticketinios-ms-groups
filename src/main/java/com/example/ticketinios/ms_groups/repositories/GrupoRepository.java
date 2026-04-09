package com.example.ticketinios.ms_groups.repositories;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketinios.ms_groups.models.Grupo;

public interface GrupoRepository extends JpaRepository<Grupo, UUID> {}