package com.example.ticketinios.ms_groups.services;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.ticketinios.ms_groups.dto.CreateGrupoRequest;
import com.example.ticketinios.ms_groups.dto.GrupoDTO;
import com.example.ticketinios.ms_groups.models.Grupo;
import com.example.ticketinios.ms_groups.models.UsuarioGrupo;
import com.example.ticketinios.ms_groups.repositories.GrupoRepository;
import com.example.ticketinios.ms_groups.repositories.UsuarioGrupoRepository;

@Service
public class GrupoService {

    @Autowired private GrupoRepository grupoRepository;
    @Autowired private UsuarioGrupoRepository usuarioGrupoRepository;

    public List<GrupoDTO> listar() {
        return grupoRepository.findAll().stream()
            .map(g -> GrupoDTO.builder()
                .id(g.getId())
                .nombre(g.getNombre())
                .descripcion(g.getDescripcion())
                .autor(g.getCreadorNombre())       
                .integrantes(usuarioGrupoRepository.countByGrupoId(g.getId()))
                .activo(g.isActivo())
                .build()
            )
            .collect(Collectors.toList());
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
            .autor(saved.getCreadorNombre())
            .integrantes(1)
            .activo(saved.isActivo())
            .build();
    }
}