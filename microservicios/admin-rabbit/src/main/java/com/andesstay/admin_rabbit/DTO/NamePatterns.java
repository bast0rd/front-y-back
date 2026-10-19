package com.andesstay.admin_rabbit.DTO;

/** Reglas de nombres válidos para colas y exchanges (no vacíos, sin espacios, sin prefijo reservado amq.). */
public final class NamePatterns {

    public static final String RESOURCE_NAME = "^(?!amq\\.)[A-Za-z0-9._-]{1,255}$";
    public static final String RESOURCE_NAME_MESSAGE =
            "El nombre debe tener entre 1 y 255 caracteres (letras, números, '.', '_' o '-') y no puede empezar con 'amq.'";

    private NamePatterns() {
    }
}
