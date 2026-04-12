package com.example.ticketinios.ms_groups.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.ticketinios.ms_groups.models.Grupo;
import com.example.ticketinios.ms_groups.models.GrupoPermiso;
import com.example.ticketinios.ms_groups.models.Permiso;
import com.example.ticketinios.ms_groups.repositories.GrupoPermisoRepository;
import com.example.ticketinios.ms_groups.repositories.GrupoRepository;
import com.example.ticketinios.ms_groups.repositories.PermisoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoPermisoService {

    private final GrupoPermisoRepository grupoPermisoRepository;
    private final GrupoRepository grupoRepository;
    private final PermisoRepository permisoRepository;

    private void validarCreador(UUID grupoId, UUID solicitanteId) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalStateException("Grupo no encontrado"));
        if (!grupo.getCreadorId().equals(solicitanteId))
            throw new IllegalStateException("Solo el creador puede gestionar permisos");
    }

    public void otorgarPermiso(UUID grupoId, UUID usuarioId, String permiso, UUID solicitanteId) {
        validarCreador(grupoId, solicitanteId);

        boolean yaExiste = grupoPermisoRepository
            .existsByGrupoIdAndUsuarioIdAndPermisoNombre(grupoId, usuarioId, permiso);

        if (!yaExiste) {
            Permiso permisoObj = permisoRepository.findByNombre(permiso)
                .orElseThrow(() -> new IllegalStateException("Permiso no encontrado: " + permiso));

            grupoPermisoRepository.save(GrupoPermiso.builder()
                .grupoId(grupoId)
                .usuarioId(usuarioId)
                .permiso(permisoObj)
                .build());
        }
    }

    public void revocarPermiso(UUID grupoId, UUID usuarioId, String permiso, UUID solicitanteId) {
        validarCreador(grupoId, solicitanteId);
        grupoPermisoRepository.deleteByGrupoIdAndUsuarioIdAndPermisoNombre(grupoId, usuarioId, permiso);
    }

    public List<String> obtenerPermisos(UUID grupoId, UUID usuarioId) {
        return grupoPermisoRepository.findByGrupoIdAndUsuarioId(grupoId, usuarioId)
            .stream()
            .map(gp -> gp.getPermiso().getNombre())
            .toList();
    }

    public boolean tienePermiso(UUID grupoId, UUID usuarioId, String permiso) {
        return grupoPermisoRepository
            .existsByGrupoIdAndUsuarioIdAndPermisoNombre(grupoId, usuarioId, permiso);
    }
}