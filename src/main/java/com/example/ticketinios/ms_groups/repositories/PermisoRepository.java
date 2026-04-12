package com.example.ticketinios.ms_groups.repositories;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.ticketinios.ms_groups.models.Permiso;

public interface PermisoRepository extends JpaRepository<Permiso, UUID> {
    Optional<Permiso> findByNombre(String nombre);

    List<Permiso> findByNombreIn(List<String> nombres);

}