package com.compass.desafio3.application.services;

import com.compass.desafio3.adapters.out.persistence.ProdutoRepository;
import com.compass.desafio3.domain.models.ItemVenda;
import com.compass.desafio3.domain.models.Produto;
import com.compass.desafio3.domain.models.Usuario;
import com.compass.desafio3.domain.models.Venda;
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
import java.util.*;

@Service
public class VendaService {

    @Autowired
    private VendaRepository vendaRepository;

    @Autowired
    private ProdutoService produtoService;

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private ProdutoRepository produtoRepository;

    @Transactional
    public Venda criarVenda(Venda venda) {
        if (venda.getItens().isEmpty()) {
            throw new IllegalArgumentException("Uma venda deve ter pelo menos um produto.");
        }

        venda.getItens().forEach(item -> {
            Produto produto = produtoService.obterProduto(item.getProduto().getId())
                    .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado com id: " + item.getProduto().getId()));

            if (produto.getEstoque() < item.getQuantidade()) {
                throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
            }

            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());

            produtoService.atualizarEstoque(produto.getId(), produto.getEstoque() - item.getQuantidade());
        });

        Usuario usuario = usuarioService.obterUsuario(venda.getUsuario().getId());
        venda.setUsuario(usuario);
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

            if (venda.getUsuario() == null) {
                throw new IllegalArgumentException("Usuário da venda é obrigatório.");
            }

            for (ItemVenda item : v.getItens()) {
                Produto produto = item.getProduto();
                produtoService.atualizarEstoque(produto.getId(), produto.getEstoque() + item.getQuantidade());
            }

            Set<ItemVenda> itensExistentes = new HashSet<>(v.getItens());
            Set<ItemVenda> itensNovos = new HashSet<>(venda.getItens());

            itensExistentes.removeAll(itensNovos);
            v.getItens().removeAll(itensExistentes);

            for (ItemVenda item : itensNovos) {
                if (!v.getItens().contains(item)) {
                    Produto produto = produtoService.obterProduto(item.getProduto().getId())
                            .orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado com id: " + item.getProduto().getId()));

                    if (produto.getEstoque() < item.getQuantidade()) {
                        throw new IllegalArgumentException("Estoque insuficiente para o produto: " + produto.getNome());
                    }

                    item.setProduto(produto);
                    item.setPrecoUnitario(produto.getPreco());
                    v.getItens().add(item);

                    produtoService.atualizarEstoque(produto.getId(), produto.getEstoque() - item.getQuantidade());
                }
            }

            v.setUsuario(venda.getUsuario());

            v.calcularTotal();

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