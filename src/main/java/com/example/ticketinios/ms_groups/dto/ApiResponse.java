package com.example.ticketinios.ms_groups.dto;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import lombok.Builder;
import lombok.Data;

@Data
@Builder

// DTO para las repuestas de la API 
@JsonPropertyOrder({ "statusCode", "intOpCode", "data" })
public class ApiResponse<T> {
    private int statusCode;
    private String intOpCode;
    private List<T> data;
}
