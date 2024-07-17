package com.compass.desafio3.application.exceptions;

public class EstoqueInsuficienteException extends RuntimeException {

    public EstoqueInsuficienteException(String message) {
        super(message);
    }

}