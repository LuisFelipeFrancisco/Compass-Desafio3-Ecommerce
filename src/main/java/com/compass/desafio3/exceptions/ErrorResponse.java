package com.compass.desafio3.exceptions;

public class ErrorResponse {

    private int code;
    private String status;
    private String message;

    public ErrorResponse(int code, String status, String message) {
        this.code = code;
        this.status = status;
        this.message = message;
    }

}