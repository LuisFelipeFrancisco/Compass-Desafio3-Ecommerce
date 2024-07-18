package com.compass.desafio3.domain.models.enums;

public enum Funcao {

    ADMIN("ADMIN"),
    USUARIO("USUARIO");

    private String funcao;

    Funcao(String funcao) {
        this.funcao = funcao;
    }

    public String getFuncao() {
        return funcao;
    }

    public void setFuncao(String funcao) {
        this.funcao = funcao;
    }

}
