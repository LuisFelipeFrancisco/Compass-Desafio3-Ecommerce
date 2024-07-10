package com.compass.desafio3.exceptions;

public class ProdutoNotFoundException extends RuntimeException {

    public ProdutoNotFoundException(String message) {
        super(message);
    }

}
