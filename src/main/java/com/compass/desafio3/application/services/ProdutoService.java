package com.compass.desafio3.application.services;

import com.compass.desafio3.domain.models.Produto;
import com.compass.desafio3.application.exceptions.ProdutoNotFoundException;
import com.compass.desafio3.adapters.out.persistence.ProdutoRepository;
import com.compass.desafio3.adapters.out.persistence.VendaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    @Autowired
    private VendaRepository vendaRepository;

    @Cacheable("produtos")
    public List<Produto> listarProdutos() {
        return produtoRepository.findAllByAtivoTrue();
    }

    @Cacheable(value = "produto", key = "#id")
    public Optional<Produto> obterProduto(Long id) {
        Optional<Produto> optionalProduto = produtoRepository.findById(id);
        if (optionalProduto.isEmpty()) {
            throw new ProdutoNotFoundException("Produto não encontrado com id: " + id);
        }
        return optionalProduto;
    }

    @CachePut(value = "produto", key = "#produto.id")
    public Produto criarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    @CachePut(value = "produto", key = "#id")
    public Produto atualizarProduto(Long id, Produto produto) {
        return produtoRepository.findById(id).map(p -> {
            p.setNome(produto.getNome());
            p.setDescricao(produto.getDescricao());
            p.setPreco(produto.getPreco());
            p.setEstoque(produto.getEstoque());
            return produtoRepository.save(p);
        }).orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado com id: " + id));
    }

    @CacheEvict(value = "produto", key = "#id")
    public void excluirProduto(Long id) {
        produtoRepository.findById(id).ifPresentOrElse(produto -> {
            if (vendaRepository.existsByProdutoId(id)) {
                produto.setAtivo(false);
                produtoRepository.save(produto);
            } else {
                produtoRepository.delete(produto);
            }
        }, () -> {
            throw new ProdutoNotFoundException("Produto não encontrado com id: " + id);
        });
    }

    @Transactional
    public void atualizarEstoque(Long produtoId, Integer novoEstoque) {
        produtoRepository.findById(produtoId).map(produto -> {
            if (novoEstoque < 0) {
                throw new IllegalArgumentException("O estoque não pode ser negativo.");
            }
            produto.setEstoque(novoEstoque);
            return produtoRepository.save(produto);
        }).orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado com id: " + produtoId));
    }

}