package com.compass.desafio3.services;

import com.compass.desafio3.domain.ItemVenda;
import com.compass.desafio3.domain.Venda;
import com.compass.desafio3.repositories.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
            throw new RuntimeException("Uma venda deve ter pelo menos um produto.");
        }

        venda.getItens().forEach(item -> {
            produtoService.obterProduto(item.getProduto().getId())
                    .ifPresent(produto -> {
                        if (produto.getEstoque() < item.getQuantidade()) {
                            throw new RuntimeException("Estoque insuficiente para o produto: " + produto.getNome());
                        }
                        produto.setEstoque(produto.getEstoque() - item.getQuantidade());
                        produtoService.atualizarProduto(produto.getId(), produto);
                        item.setPrecoUnitario(produto.getPreco());
                    });
        });
        venda.setDataVenda(LocalDateTime.now());
        return vendaRepository.save(venda);
    }

    public List<Venda> listarVendas() {
        List<Venda> vendas = vendaRepository.findAll();
        vendas.forEach(Venda::calcularTotal);
        return vendas;
    }

    public Optional<Venda> obterVenda(Long id) {
        Optional<Venda> optionalVenda = vendaRepository.findById(id);
        optionalVenda.ifPresent(Venda::calcularTotal);
        return optionalVenda;
    }

    @Transactional
    public Venda atualizarVenda(Long id, Venda venda) {
        return vendaRepository.findById(id).map(v -> {
            if (venda.getItens().isEmpty()) {
                throw new RuntimeException("Uma venda deve ter pelo menos um produto.");
            }
            v.setItens(venda.getItens());
            return vendaRepository.save(v);
        }).orElseThrow(() -> new RuntimeException("Venda não encontrada"));
    }

    @Transactional
    public void excluirVenda(Long id) {
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
        int ano = dataInicial.getYear();
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