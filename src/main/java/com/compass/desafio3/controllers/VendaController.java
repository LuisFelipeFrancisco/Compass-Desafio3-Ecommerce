package com.compass.desafio3.controllers;

import com.compass.desafio3.domain.Venda;
import com.compass.desafio3.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public Venda criarVenda(@RequestBody Venda venda) {
        return vendaService.criarVenda(venda);
    }

    @GetMapping
    public List<Venda> listarVendas() {
        return vendaService.listarVendas();
    }

    @GetMapping("/{id}")
    public Optional<Venda> obterVenda(@PathVariable Long id) {
        return vendaService.obterVenda(id);
    }

    @PutMapping("/{id}")
    public Venda atualizarVenda(@PathVariable Long id, @RequestBody Venda venda) {
        return vendaService.atualizarVenda(id, venda);
    }

    @DeleteMapping("/{id}")
    public void excluirVenda(@PathVariable Long id) {
        vendaService.excluirVenda(id);
    }

    @GetMapping("/filtrar")
    public List<Venda> filtrarVendasPorData(@RequestParam LocalDateTime inicio, @RequestParam LocalDateTime fim) {
        return vendaService.filtrarVendasPorData(inicio, fim);
    }

    @GetMapping("/relatorio/mensal")
    public List<Venda> relatorioMensal(@RequestParam int ano, @RequestParam int mes) {
        return vendaService.relatorioMensal(ano, mes);
    }

    @GetMapping("/relatorio/semanal")
    public List<Venda> relatorioSemanal(@RequestParam int ano, @RequestParam int semana) {
        return vendaService.relatorioSemanal(ano, semana);
    }

}