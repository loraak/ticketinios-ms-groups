package com.example.ticketinios.ms_groups.config;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.ticketinios.ms_groups.dto.ApiResponse;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleIllegalState(
            IllegalStateException ex) {

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
            ApiResponse.<Map<String, String>>builder()
                .statusCode(400)
                .intOpCode("MS-GROUPS-ERROR")
                .data(List.of(Map.of("message", ex.getMessage())))
                .build()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        List<Map<String, String>> errores = ex.getBindingResult()
            .getFieldErrors()
            .stream()
            .map(e -> Map.of(
                "campo", e.getField(),
                "message", e.getDefaultMessage() != null ? e.getDefaultMessage() : "Campo inválido"
            ))
            .toList();

        return ResponseEntity.badRequest().body(
            ApiResponse.<Map<String, String>>builder()
                .statusCode(400)
                .intOpCode("MS-GROUPS-VALIDATION-ERROR")
                .data(errores)
                .build()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleGeneral(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
            ApiResponse.<Map<String, String>>builder()
                .statusCode(500)
                .intOpCode("MS-GROUPS-INTERNAL-ERROR")
                .data(List.of(Map.of("message", "Error interno del servidor")))
                .build()
        );
    }
}