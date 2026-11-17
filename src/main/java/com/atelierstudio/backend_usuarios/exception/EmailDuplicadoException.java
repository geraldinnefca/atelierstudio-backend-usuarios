package com.atelierstudio.backend_usuarios.exception;

public class EmailDuplicadoException extends RuntimeException {
    public EmailDuplicadoException(String correo) {
        super("Ya existe un usuario con ese correo.");
    }
}
