package com.compass.desafio3.application.exceptions;

public class ProdutoNotFoundException extends RuntimeException {

    public ProdutoNotFoundException(String message) {
        super(message);
    }

}
