package com.compass.desafio3.adapters.in.controllers;

import com.compass.desafio3.domain.models.Venda;
import com.compass.desafio3.application.services.VendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/vendas")
public class VendaController {

    @Autowired
    private VendaService vendaService;

    @PostMapping
    public ResponseEntity<Venda> criarVenda(@RequestBody Venda venda) {
        Venda novaVenda = vendaService.criarVenda(venda);
        return ResponseEntity.status(HttpStatus.CREATED).body(novaVenda);
    }

    @GetMapping
    public ResponseEntity<List<Venda>> listarVendas() {
        List<Venda> vendas = vendaService.listarVendas();
        return ResponseEntity.ok(vendas);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Optional<Venda>> obterVenda(@PathVariable Long id) {
        Optional<Venda> venda = vendaService.obterVenda(id);
        return ResponseEntity.ok(venda);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Venda> atualizarVenda(@PathVariable Long id, @RequestBody Venda venda) {
        Venda vendaAtualizada = vendaService.atualizarVenda(id, venda);
        return ResponseEntity.ok(vendaAtualizada);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluirVenda(@PathVariable Long id) {
        vendaService.excluirVenda(id);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/filtrar")
    @ResponseStatus(HttpStatus.OK)
    public List<Venda> filtrarVendasPorData(@RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
                                            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return vendaService.filtrarVendasPorData(inicio, fim);
    }

    @GetMapping("/relatorio/mensal")
    @ResponseStatus(HttpStatus.OK)
    public List<Venda> relatorioMensal(@RequestParam int ano, @RequestParam int mes) {
        return vendaService.relatorioMensal(ano, mes);
    }

    @GetMapping("/relatorio/semanal")
    public ResponseEntity<List<Venda>> relatorioSemanal(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicial) {

        List<Venda> relatorio = vendaService.relatorioSemanal(dataInicial);

        return ResponseEntity.ok(relatorio);
    }

}