package com.andesstay.admin_rabbit.DTO;

/** Respuesta simple para operaciones de creación/eliminación. */
public record OperationResultDTO(String resource, String name, String message) {
}
