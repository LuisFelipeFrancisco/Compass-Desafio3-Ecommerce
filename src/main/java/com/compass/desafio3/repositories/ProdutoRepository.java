package com.compass.desafio3.repositories;

import com.compass.desafio3.domain.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByAtivoTrue();

}