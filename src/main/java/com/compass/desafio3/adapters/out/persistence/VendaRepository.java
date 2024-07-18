package com.compass.desafio3.adapters.out.persistence;

import com.compass.desafio3.domain.models.Venda;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findAllByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

    @Query("SELECT CASE WHEN COUNT(iv) > 0 THEN TRUE ELSE FALSE END FROM ItemVenda iv WHERE iv.produto.id = :produtoId")
    boolean existsByProdutoId(Long produtoId);

}