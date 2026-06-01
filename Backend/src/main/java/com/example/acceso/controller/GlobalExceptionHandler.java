package com.example.acceso.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
    @ExceptionHandler(TypeMismatchException.class)
    public ResponseEntity<Map<String, Object>> handleTypeMismatchException(TypeMismatchException ex) {
        logger.warn("Tipo de dato incorrecto: valor '{}', requerido '{}'", ex.getValue(), ex.getRequiredType());
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", "Parámetro inválido"));
    }
}
