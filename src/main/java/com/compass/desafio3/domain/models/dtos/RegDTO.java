package com.compass.desafio3.domain.models.dtos;

import com.compass.desafio3.domain.models.enums.Funcao;

public record RegDTO(String nome, String email, String senha, Funcao funcao) {
}
