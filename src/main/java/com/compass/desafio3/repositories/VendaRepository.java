package com.compass.desafio3.repositories;

import com.compass.desafio3.domain.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface VendaRepository extends JpaRepository<Venda, Long> {

    List<Venda> findAllByDataVendaBetween(LocalDateTime inicio, LocalDateTime fim);

}
