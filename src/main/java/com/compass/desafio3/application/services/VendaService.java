package com.compass.desafio3.application.services;

import com.compass.desafio3.domain.models.ItemVenda;
import com.compass.desafio3.domain.models.Venda;
import com.compass.desafio3.application.exceptions.EstoqueInsuficienteException;
import com.compass.desafio3.application.exceptions.ProdutoNotFoundException;
import com.compass.desafio3.application.exceptions.VendaNotFoundException;
import com.compass.desafio3.adapters.out.persistence.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.WeekFields;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoService produtoService;

    @Transactional
    public Venda criarVenda(Venda venda) {
        if (venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("Uma venda deve ter pelo menos um produto.");
        }

        venda.getItens().forEach(item -> {
            produtoService.obterProduto(item.getProduto().getId())
                    .ifPresentOrElse(produto -> {
                        if (produto.getEstoque() < item.getQuantidade()) {
                            throw new EstoqueInsuficienteException("Estoque insuficiente para o produto: " + produto.getNome());
                        }
                        produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                        produtoService.atualizarProduto(produto.getId(), produto);
                        item.setPrecoUnitario(produto.getPreco());
                    }, () -> {
                        throw new ProdutoNotFoundException("Produto não encontrado com id: " + item.getProduto().getId());
                    });
        });

        venda.setDataVenda(LocalDateTime.now());
        return vendaRepository.save(venda);
    }

    @Cacheable("vendas")
    public List<Venda> listarVendas() {
        List<Venda> vendas = vendaRepository.findAll();
        vendas.forEach(Venda::calcularTotal);
        return vendas;
    }

    @Cacheable(value = "venda", key = "#id")
    public Optional<Venda> obterVenda(Long id) {
        Optional<Venda> optionalVenda = vendaRepository.findById(id);
        if (optionalVenda.isEmpty()) {
            throw new VendaNotFoundException("Venda não encontrada com id: " + id);
        }
        optionalVenda.ifPresent(Venda::calcularTotal);
        return optionalVenda;
    }

    @Transactional
    @CacheEvict(value = "venda", key = "#id")
    public Venda atualizarVenda(Long id, Venda venda) {
        return vendaRepository.findById(id).map(v -> {
            if (venda.getItens().isEmpty()) {
                throw new IllegalArgumentException("Uma venda deve ter pelo menos um produto.");
            }
            v.setItens(venda.getItens());
            return vendaRepository.save(v);
        }).orElseThrow(() -> new VendaNotFoundException("Venda não encontrada com id: " + id));
    }

    @Transactional
    @CacheEvict(value = "venda", key = "#id")
    public void excluirVenda(Long id) {
        vendaRepository.findById(id).orElseThrow(() -> new VendaNotFoundException("Venda não encontrada com id: " + id));
        vendaRepository.deleteById(id);
    }

    public List<Venda> filtrarVendasPorData(LocalDateTime inicio, LocalDateTime fim) {
        List<Venda> vendas = vendaRepository.findAllByDataVendaBetween(inicio, fim);
        calcularTotais(vendas);
        return vendas;
    }

    public List<Venda> relatorioMensal(int ano, int mes) {
        LocalDateTime inicio = LocalDateTime.of(ano, mes, 1, 0, 0);
        LocalDateTime fim = inicio.plusMonths(1).minusSeconds(1);

        List<Venda> vendas = vendaRepository.findAllByDataVendaBetween(inicio, fim);
        calcularTotais(vendas);
        return vendas;
    }

    public List<Venda> relatorioSemanal(LocalDate dataInicial) {
        WeekFields weekFields = WeekFields.of(Locale.getDefault());
        int semana = dataInicial.get(weekFields.weekOfWeekBasedYear());

        LocalDateTime inicio = dataInicial.atStartOfDay()
                .with(weekFields.weekOfWeekBasedYear(), semana)
                .with(weekFields.dayOfWeek(), 1);

        LocalDateTime fim = inicio.plusDays(6)
                .withHour(23)
                .withMinute(59)
                .withSecond(59);

        List<Venda> vendas = vendaRepository.findAllByDataVendaBetween(inicio, fim);
        calcularTotais(vendas);
        return vendas;
    }

    private void calcularTotais(List<Venda> vendas) {
        for (Venda venda : vendas) {
            double novoTotal = 0.0;
            for (ItemVenda item : venda.getItens()) {
                novoTotal += item.getQuantidade() * item.getPrecoUnitario();
            }
            venda.setTotal(novoTotal);
        }
    }

}