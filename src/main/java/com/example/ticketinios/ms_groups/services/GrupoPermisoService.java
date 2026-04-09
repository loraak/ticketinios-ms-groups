package com.example.ticketinios.ms_groups.services;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.ticketinios.ms_groups.models.Grupo;
import com.example.ticketinios.ms_groups.models.GrupoPermiso;
import com.example.ticketinios.ms_groups.repositories.GrupoPermisoRepository;
import com.example.ticketinios.ms_groups.repositories.GrupoRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class GrupoPermisoService {

    private final GrupoPermisoRepository grupoPermisoRepository;
    private final GrupoRepository grupoRepository;

    // Verifica si el solicitante es creador del grupo
    private void validarCreador(UUID grupoId, UUID solicitanteId) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalStateException("Grupo no encontrado"));
        if (!grupo.getCreadorId().equals(solicitanteId))
            throw new IllegalStateException("Solo el creador puede gestionar permisos");
    }

    /* Otorgar permiso a un usuario sobre un grupo
    public void otorgarPermiso(UUID grupoId, UUID usuarioId, String permiso, UUID solicitanteId) {
        validarCreador(grupoId, solicitanteId);

        boolean yaExiste = grupoPermisoRepository
            .existsByGrupoIdAndUsuarioIdAndPermisoId(grupoId, usuarioId, permiso);

        if (!yaExiste) {
            grupoPermisoRepository.save(GrupoPermiso.builder()
                .grupoId(grupoId)
                .usuarioId(usuarioId)
                .permisoId(permiso)
                .build());
        }
    }
    */

    // Revocar permiso
    public void revocarPermiso(UUID grupoId, UUID usuarioId, String permiso, UUID solicitanteId) {
        validarCreador(grupoId, solicitanteId);
        grupoPermisoRepository.deleteByGrupoIdAndUsuarioIdAndPermisoId(grupoId, usuarioId, permiso);
    }

    // Obtener permisos de un usuario en un grupo
    public List<String> obtenerPermisos(UUID grupoId, UUID usuarioId) {
        return grupoPermisoRepository.findByGrupoIdAndUsuarioId(grupoId, usuarioId)
            .stream()
            .map(gp -> gp.getPermiso().getNombre())
            .toList();
    }

    // Verificar si un usuario tiene un permiso específico en un grupo
    public boolean tienePermiso(UUID grupoId, UUID usuarioId, String permiso) {
        return grupoPermisoRepository
            .existsByGrupoIdAndUsuarioIdAndPermisoId(grupoId, usuarioId, permiso);
    }
}
