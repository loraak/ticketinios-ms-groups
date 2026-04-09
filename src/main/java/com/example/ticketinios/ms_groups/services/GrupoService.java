package com.example.ticketinios.ms_groups.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ticketinios.ms_groups.dto.CreateGrupoRequest;
import com.example.ticketinios.ms_groups.dto.GrupoDTO;
import com.example.ticketinios.ms_groups.dto.UpdateGrupoRequest;
import com.example.ticketinios.ms_groups.models.Grupo;
import com.example.ticketinios.ms_groups.models.UsuarioGrupo;
import com.example.ticketinios.ms_groups.repositories.GrupoRepository;
import com.example.ticketinios.ms_groups.repositories.UsuarioGrupoRepository;

@Service
public class GrupoService {

    @Autowired private GrupoRepository grupoRepository;
    @Autowired private UsuarioGrupoRepository usuarioGrupoRepository;

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

    public GrupoDTO crear(CreateGrupoRequest request) {
        Grupo grupo = new Grupo();
        grupo.setNombre(request.nombre());
        grupo.setDescripcion(request.descripcion());
        grupo.setCreadorId(UUID.fromString(request.creadorId()));
        grupo.setCreadorNombre(request.creadorNombre());
        grupo.setActivo(true);
        grupo.setCreadoEn(LocalDateTime.now());

        Grupo saved = grupoRepository.save(grupo);

        UsuarioGrupo ug = new UsuarioGrupo();
        ug.setUsuarioId(UUID.fromString(request.creadorId()));
        ug.setGrupoId(saved.getId());
        ug.setUnionEn(LocalDateTime.now());
        usuarioGrupoRepository.save(ug);

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
}