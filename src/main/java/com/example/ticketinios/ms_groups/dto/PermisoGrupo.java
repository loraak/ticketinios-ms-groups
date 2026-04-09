package com.example.ticketinios.ms_groups.dto;

public enum PermisoGrupo {
    GRUPOS_EDITAR("grupos:editar"),
    GRUPOS_BAJA("grupos:baja"),
    GRUPOS_AGREGAR("grupos:agregar"); 

    private final String valor;

    PermisoGrupo(String valor) { this.valor = valor; }
    public String getValor() { return valor; }
}