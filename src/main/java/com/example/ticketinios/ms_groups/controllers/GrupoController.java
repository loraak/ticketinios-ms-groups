package com.example.ticketinios.ms_groups.controllers;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketinios.ms_groups.dto.ApiResponse;
import com.example.ticketinios.ms_groups.dto.CreateGrupoRequest;
import com.example.ticketinios.ms_groups.dto.GrupoDTO;
import com.example.ticketinios.ms_groups.dto.UpdateGrupoRequest;
import com.example.ticketinios.ms_groups.services.GrupoPermisoService;
import com.example.ticketinios.ms_groups.services.GrupoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired private GrupoService grupoService;
    @Autowired private GrupoPermisoService grupoPermisoService;

    @GetMapping
    public ResponseEntity<ApiResponse<GrupoDTO>> listar(@RequestHeader("X-User-Id") String usuarioId) {
        System.out.println("X-User-Id recibido: " + usuarioId);
        
        var data = grupoService.listar(UUID.fromString(usuarioId));
        return ResponseEntity.ok(ApiResponse.<GrupoDTO>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-LIST-OK")
            .data(data)
            .build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoDTO>> obtener(@PathVariable UUID id) {
        GrupoDTO grupo = grupoService.obtenerPorId(id);

        return ResponseEntity.ok(ApiResponse.<GrupoDTO>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-GET-OK")
            .data(List.of(grupo))
            .build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<GrupoDTO>> crear(@Valid @RequestBody CreateGrupoRequest request) {
        var grupo = grupoService.crear(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<GrupoDTO>builder()
            .statusCode(201)
            .intOpCode("MS-GRUPOS-CREATE-OK")
            .data(List.of(grupo))
            .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoDTO>> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGrupoRequest request) {
        try {
            var grupo = grupoService.actualizar(id, request);
            return ResponseEntity.ok(ApiResponse.<GrupoDTO>builder()
                .statusCode(200)
                .intOpCode("MS-GRUPOS-UPDATE-OK")
                .data(List.of(grupo))
                .build());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ApiResponse.<GrupoDTO>builder()
                .statusCode(404)
                .intOpCode("MS-GRUPOS-UPDATE-NOT-FOUND")
                .data(List.of())
                .build());
        }
    }

    @GetMapping("/{grupoId}/permisos")
    public ResponseEntity<ApiResponse<String>> obtenerPermisos(
            @PathVariable UUID grupoId,
            @RequestHeader("X-User-Id") String usuarioId) {
        System.out.println("grupoId: " + grupoId);
        System.out.println("usuarioId: " + usuarioId);
        
        var permisos = grupoPermisoService.obtenerPermisos(grupoId, UUID.fromString(usuarioId));
        System.out.println("permisos encontrados: " + permisos);
        
        return ResponseEntity.ok(ApiResponse.<String>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-PERMISOS-OK")
            .data(permisos)
            .build());
    }
}