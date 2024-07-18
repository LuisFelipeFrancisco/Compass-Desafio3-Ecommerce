package com.compass.desafio3.adapters.out.persistence;

import com.compass.desafio3.domain.models.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {

    List<Produto> findAllByAtivoTrue();

}