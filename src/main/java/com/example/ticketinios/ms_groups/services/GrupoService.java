package com.example.ticketinios.ms_groups.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ticketinios.ms_groups.dto.CreateGrupoRequest;
import com.example.ticketinios.ms_groups.dto.GrupoDTO;
import com.example.ticketinios.ms_groups.dto.MiembroDTO;
import com.example.ticketinios.ms_groups.dto.UpdateGrupoRequest;
import com.example.ticketinios.ms_groups.models.Grupo;
import com.example.ticketinios.ms_groups.models.GrupoPermiso;
import com.example.ticketinios.ms_groups.models.Permiso;
import com.example.ticketinios.ms_groups.models.UsuarioGrupo;
import com.example.ticketinios.ms_groups.repositories.GrupoPermisoRepository;
import com.example.ticketinios.ms_groups.repositories.GrupoRepository;
import com.example.ticketinios.ms_groups.repositories.PermisoRepository;
import com.example.ticketinios.ms_groups.repositories.UsuarioGrupoRepository;

import jakarta.transaction.Transactional;

@Service
public class GrupoService {

    @Autowired private GrupoRepository grupoRepository;
    @Autowired private UsuarioGrupoRepository usuarioGrupoRepository;
    @Autowired private GrupoPermisoRepository grupoPermisoRepository;
    @Autowired private PermisoRepository permisoRepository; 
    @Autowired private GrupoPermisoService grupoPermisoService;

    /*  Solo permisos de grupos */
        private static final List<String> PERMISOS_CREADOR_GRUPO = List.of(
        "grupos:ver_especifico",
        "grupos:editar",
        "grupos:eliminar",
        "tickets:crear",
        "tickets:editar",
        "tickets:eliminar",
        "tickets:comentario"
    );

    public List<GrupoDTO> listar(UUID usuarioId) {
        List<UUID> grupoIds = usuarioGrupoRepository.findGrupoIdsByUsuarioId(usuarioId);
        System.out.println("grupoIds encontrados: " + grupoIds);
        if (grupoIds.isEmpty()) return List.of();

        return grupoRepository.findAllById(grupoIds).stream()
        .map(g -> GrupoDTO.builder()
            .id(g.getId())
            .nombre(g.getNombre())
            .descripcion(g.getDescripcion())
            .creador(g.getCreadorNombre())
            .creado_en(g.getCreadoEn())
            .integrantes(usuarioGrupoRepository.countByGrupoId(g.getId()))
            .activo(g.isActivo())
            .build()
        )
        .collect(Collectors.toList());
    }

    public List<MiembroDTO> obtenerMiembros(UUID grupoId) {
        return usuarioGrupoRepository.findByGrupoId(grupoId).stream()
            .map(ug -> new MiembroDTO(ug.getUsuarioId(), ug.getNombreCompleto()))
            .toList();
    }

    public GrupoDTO obtenerPorId(UUID grupoId) {
        Grupo grupo = grupoRepository.findById(grupoId)
            .orElseThrow(() -> new IllegalStateException("Grupo no encontrado"));
    
        return GrupoDTO.builder()
            .id(grupo.getId())
            .nombre(grupo.getNombre())
            .descripcion(grupo.getDescripcion())
            .creador(grupo.getCreadorNombre())
            .creado_en(grupo.getCreadoEn())
            .activo(grupo.isActivo())
            .build();
    }

    public GrupoDTO crear(CreateGrupoRequest request, String usuarioId) {
        Grupo grupo = new Grupo();
        grupo.setNombre(request.nombre());
        grupo.setDescripcion(request.descripcion());
        grupo.setCreadorId(UUID.fromString(usuarioId));        
        grupo.setCreadorNombre(request.creadorNombre());
        grupo.setActivo(true);
        grupo.setCreadoEn(LocalDateTime.now());

        Grupo saved = grupoRepository.save(grupo);

        UsuarioGrupo ug = new UsuarioGrupo();
        ug.setUsuarioId(UUID.fromString(usuarioId));           
        ug.setGrupoId(saved.getId());
        ug.setNombreCompleto(request.creadorNombre());
        ug.setUnionEn(LocalDateTime.now());
        usuarioGrupoRepository.save(ug);

        List<Permiso> permisosGrupo = permisoRepository.findByNombreIn(PERMISOS_CREADOR_GRUPO);
        List<GrupoPermiso> permisosCreador = permisosGrupo.stream()
        .map(permiso -> GrupoPermiso.builder()
            .grupoId(saved.getId())
            .usuarioId(UUID.fromString(usuarioId))
            .permiso(permiso)
            .build())
        .toList();

        grupoPermisoRepository.saveAll(permisosCreador);

        return GrupoDTO.builder()
            .id(saved.getId())
            .nombre(saved.getNombre())
            .descripcion(saved.getDescripcion())
            .creador(saved.getCreadorNombre())
            .integrantes(1)
            .activo(saved.isActivo())
            .build();
    }

    public GrupoDTO actualizar(UUID id, UpdateGrupoRequest request) {
        Grupo grupo = grupoRepository.findById(id)
            .orElseThrow(() -> new IllegalStateException("Grupo no encontrado."));

        grupo.setNombre(request.nombre());
        grupo.setDescripcion(request.descripcion());
        grupoRepository.save(grupo);

        return GrupoDTO.builder()
            .id(grupo.getId())
            .nombre(grupo.getNombre())
            .descripcion(grupo.getDescripcion())
            .creador(grupo.getCreadorNombre())
            .integrantes(usuarioGrupoRepository.countByGrupoId(grupo.getId()))
            .activo(grupo.isActivo())
            .build();
    }

    @Transactional
    public boolean darDeBaja(UUID id) { 
            Grupo grupo = grupoRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Grupo no encontrado.")); 
                
            boolean nuevoEstado = !grupo.isActivo();
            grupo.setActivo(nuevoEstado);
            grupoRepository.save(grupo);
            return nuevoEstado; 
        }

        public List<GrupoDTO> listarTodos() {
        return grupoRepository.findAll().stream()
            .map(g -> GrupoDTO.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .creador(g.getCreadorNombre())
                .integrantes(usuarioGrupoRepository.countByGrupoId(g.getId()))
                .activo(g.isActivo())
                .build()
            ).toList();
    }

    @Transactional
    public void agregarMiembro(UUID grupoId, UUID usuarioId, String nombreCompleto) {
        if (!usuarioGrupoRepository.existsByGrupoIdAndUsuarioId(grupoId, usuarioId)) {
            UsuarioGrupo ug = new UsuarioGrupo();
            ug.setGrupoId(grupoId);
            ug.setUsuarioId(usuarioId);
            ug.setNombreCompleto(nombreCompleto);
            ug.setUnionEn(LocalDateTime.now());
            usuarioGrupoRepository.save(ug);

            // ← Asigna automáticamente grupos:ver_especifico
            permisoRepository.findByNombre("grupos:ver_especifico").ifPresent(permiso -> {
                GrupoPermiso gp = GrupoPermiso.builder()
                    .grupoId(grupoId)
                    .usuarioId(usuarioId)
                    .permiso(permiso)
                    .build();
                grupoPermisoRepository.save(gp);
            });
        }
    }

    @Transactional
    public void quitarMiembro(UUID grupoId, UUID usuarioId) {
        usuarioGrupoRepository.deleteByGrupoIdAndUsuarioId(grupoId, usuarioId);
        grupoPermisoRepository.deleteByGrupoIdAndUsuarioId(grupoId, usuarioId);
    }

    @Transactional
    public void configurarPermisos(UUID grupoId, UUID usuarioId, List<String> nombresPermisos) {
        grupoPermisoRepository.deleteByGrupoIdAndUsuarioId(grupoId, usuarioId);
        List<Permiso> permisos = permisoRepository.findByNombreIn(nombresPermisos);
        List<GrupoPermiso> nuevos = permisos.stream()
            .map(p -> GrupoPermiso.builder()
                .grupoId(grupoId)
                .usuarioId(usuarioId)
                .permiso(p)
                .build())
            .toList();
        grupoPermisoRepository.saveAll(nuevos);
    }

    public void validarPermisoGrupo(UUID grupoId, UUID usuarioId, String permiso) {
        boolean tiene = grupoPermisoService.tienePermiso(grupoId, usuarioId, permiso);
        if (!tiene) throw new IllegalStateException("No tienes permiso para realizar esta acción en este grupo.");
    }
}