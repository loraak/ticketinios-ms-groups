package com.example.ticketinios.ms_groups.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.ticketinios.ms_groups.dto.ApiResponse;
import com.example.ticketinios.ms_groups.dto.CreateGrupoRequest;
import com.example.ticketinios.ms_groups.dto.GrupoDTO;
import com.example.ticketinios.ms_groups.services.GrupoService;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/grupos")
@CrossOrigin(origins = "*")
public class GrupoController {

    @Autowired private GrupoService grupoService;

    @GetMapping
    public ResponseEntity<ApiResponse<GrupoDTO>> listar() {
        var data = grupoService.listar();
        return ResponseEntity.ok(ApiResponse.<GrupoDTO>builder()
            .statusCode(200)
            .intOpCode("MS-GRUPOS-LIST-OK")
            .data(data)
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
}