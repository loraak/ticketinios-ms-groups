package com.example.ticketinios.ms_groups.controllers;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
import com.example.ticketinios.ms_groups.dto.MiembroDTO;
import com.example.ticketinios.ms_groups.dto.UpdateGrupoRequest;
import com.example.ticketinios.ms_groups.services.GrupoPermisoService;
import com.example.ticketinios.ms_groups.services.GrupoService;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired private GrupoService grupoService;
    @Autowired private GrupoPermisoService grupoPermisoService;

    @Value("${service.secret-key}")
    private String serviceSecretKey;

    
    @GetMapping("/health")
    public ResponseEntity<ApiResponse<Map<String, String>>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-HEALTH-OK")
            .data(List.of(Map.of("status", "ok", "service", "groups")))
            .build());
    }

    @GetMapping
    public ResponseEntity<ApiResponse<GrupoDTO>> listar(@RequestHeader("X-User-Id") String usuarioId) {
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
    @Parameter(name = "X-User-Id", description = "ID del usuario autenticado", required = true, in = ParameterIn.HEADER)
    public ResponseEntity<ApiResponse<GrupoDTO>> crear(
            @Valid @RequestBody CreateGrupoRequest request,
            @RequestHeader("X-User-Id") String usuarioId) {

        var grupo = grupoService.crear(request, usuarioId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<GrupoDTO>builder()
            .statusCode(201)
            .intOpCode("MS-GRUPOS-CREATE-OK")
            .data(List.of(grupo))
            .build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GrupoDTO>> actualizar(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGrupoRequest request,
            @RequestHeader("X-User-Id") String usuarioId) {
        try {
            grupoService.validarPermisoGrupo(id, UUID.fromString(usuarioId), "grupos:editar");
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
        @RequestHeader("X-User-Id") String usuarioId,
        @RequestHeader(value = "X-Service-Key", required = false) String serviceKey) {

        // Permite acceso si viene del gateway (X-Gateway-Token) o de otro MS (X-Service-Key)
        if (serviceKey != null && !serviceKey.equals(this.serviceSecretKey)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        var permisos = grupoPermisoService.obtenerPermisos(grupoId, UUID.fromString(usuarioId));
        return ResponseEntity.ok(ApiResponse.<String>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-PERMISOS-OK")
            .data(permisos)
            .build());
    }

    @PatchMapping("/estado/{id}") 
    public ResponseEntity<ApiResponse<Map<String, String>>> darDeBaja(
            @PathVariable UUID id,
            @RequestHeader("X-User-Id") String usuarioId) { 
        try { 
            grupoService.validarPermisoGrupo(id, UUID.fromString(usuarioId), "grupos:eliminar");
            boolean estaActivo = grupoService.darDeBaja(id);
            String accion = estaActivo ? "alta" : "baja";

            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .statusCode(200)
                .intOpCode("MS-GRUPOS-ESTADO-OK")
                .data(List.of(Map.of("message", "Grupo dado de " + accion + " exitosamente")))
                .build());

        } catch (IllegalStateException e) {
            boolean esForbidden = e.getMessage().contains("permiso");
            return ResponseEntity.status(esForbidden ? HttpStatus.FORBIDDEN : HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Map<String, String>>builder()
                    .statusCode(esForbidden ? 403 : 404)
                    .intOpCode(esForbidden ? "MS-GRUPOS-FORBIDDEN" : "MS-GRUPOS-NOT-FOUND")
                    .data(List.of(Map.of("message", e.getMessage())))
                    .build());
        }
    }

    @GetMapping("/{grupoId}/miembros")
    public ResponseEntity<ApiResponse<MiembroDTO>> obtenerMiembros(@PathVariable UUID grupoId) {
        var miembros = grupoService.obtenerMiembros(grupoId);
        return ResponseEntity.ok(ApiResponse.<MiembroDTO>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-MIEMBROS-OK")
            .data(miembros)
            .build());
    }


    // PARA GESTIÓN DE GRUPOS. 
    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<GrupoDTO>> listarTodos() {
        var data = grupoService.listarTodos();
        return ResponseEntity.ok(ApiResponse.<GrupoDTO>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-ADMIN-LIST-OK")
            .data(data)
            .build());
    }

    @GetMapping("/{grupoId}/permisos/{usuarioId}")
    public ResponseEntity<ApiResponse<String>> obtenerPermisosDeUsuario(
            @PathVariable UUID grupoId,
            @PathVariable UUID usuarioId) {
        var permisos = grupoPermisoService.obtenerPermisos(grupoId, usuarioId);
        return ResponseEntity.ok(ApiResponse.<String>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-PERMISOS-OK")
            .data(permisos)
            .build());
    }

    @PostMapping("/{grupoId}/miembros")
    public ResponseEntity<ApiResponse<Map<String, String>>> agregarMiembro(
            @PathVariable UUID grupoId,
            @RequestBody Map<String, String> body) {
        grupoService.agregarMiembro(grupoId, UUID.fromString(body.get("usuarioId")), body.get("nombreCompleto"));
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-MIEMBRO-OK")
            .data(List.of(Map.of("message", "Usuario agregado al grupo.")))
            .build());
    }

    @DeleteMapping("/{grupoId}/miembros/{usuarioId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> quitarMiembro(
            @PathVariable UUID grupoId,
            @PathVariable UUID usuarioId) {
        grupoService.quitarMiembro(grupoId, usuarioId);
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-MIEMBRO-REMOVED-OK")
            .data(List.of(Map.of("message", "Usuario removido del grupo.")))
            .build());
    }

    @PutMapping("/{grupoId}/permisos/{usuarioId}")
    public ResponseEntity<ApiResponse<Map<String, String>>> configurarPermisos(
            @PathVariable UUID grupoId,
            @PathVariable UUID usuarioId,
            @RequestBody Map<String, List<String>> body) {
        grupoService.configurarPermisos(grupoId, usuarioId, body.get("permisos"));
        return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-PERMISOS-CONFIG-OK")
            .data(List.of(Map.of("message", "Permisos configurados.")))
            .build());
    }

    @PatchMapping("/admin/estado/{id}")
    public ResponseEntity<ApiResponse<Map<String, String>>> darDeBajaAdmin(
            @PathVariable UUID id) {
        try {
            boolean estaActivo = grupoService.darDeBaja(id);
            String accion = estaActivo ? "alta" : "baja";

            return ResponseEntity.ok(ApiResponse.<Map<String, String>>builder()
                .statusCode(200)
                .intOpCode("MS-GRUPOS-ESTADO-OK")
                .data(List.of(Map.of("message", "Grupo dado de " + accion + " exitosamente")))
                .build());

        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ApiResponse.<Map<String, String>>builder()
                    .statusCode(404)
                    .intOpCode("MS-GRUPOS-NOT-FOUND")
                    .data(List.of(Map.of("message", e.getMessage())))
                    .build());
        }
    }

    @PutMapping("/admin/{id}")
    public ResponseEntity<ApiResponse<GrupoDTO>> actualizarAdmin(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateGrupoRequest request,
            @RequestHeader("X-User-Id") String usuarioId) {
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
}