package com.example.acceso.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(TypeMismatchException.class)
    public String handleTypeMismatchException(TypeMismatchException ex) {
        logger.warn("Se detectó un intento de acceder a una URL con un tipo de dato incorrecto. " +
                "Valor: '{}', Tipo requerido: '{}'. Redirigiendo a la página de inicio.",
                ex.getValue(), ex.getRequiredType());
        return "redirect:/";
    }
}
