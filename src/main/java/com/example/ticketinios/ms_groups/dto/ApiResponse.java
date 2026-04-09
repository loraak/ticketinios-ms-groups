package com.example.ticketinios.ms_groups.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

// DTO para las repuestas de la API 
public class ApiResponse<T> {
    private int statusCode;
    private String intOpCode;
    private List<T> data;
}
