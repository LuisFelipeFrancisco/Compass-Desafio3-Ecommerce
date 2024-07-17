package com.compass.desafio3.application.exceptions;

public class VendaNotFoundException extends RuntimeException {

    public VendaNotFoundException(String message) {
        super(message);
    }

}