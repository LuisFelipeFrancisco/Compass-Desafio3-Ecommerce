package com.compass.desafio3.services;

import com.compass.desafio3.domain.Produto;
import com.compass.desafio3.exceptions.ProdutoNotFoundException;
import com.compass.desafio3.repositories.ProdutoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProdutoService {

    @Autowired
    private ProdutoRepository produtoRepository;

    public List<Produto> listarProdutos() {
        return produtoRepository.findAllByAtivoTrue();
    }

    public Optional<Produto> obterProduto(Long id) {
        Optional<Produto> optionalProduto = produtoRepository.findById(id);
        if (optionalProduto.isEmpty()) {
            throw new ProdutoNotFoundException("Produto não encontrado com id: " + id);
        }
        return optionalProduto;
    }

    public Produto criarProduto(Produto produto) {
        return produtoRepository.save(produto);
    }

    public Produto atualizarProduto(Long id, Produto produto) {
        return produtoRepository.findById(id).map(p -> {
            p.setNome(produto.getNome());
            p.setDescricao(produto.getDescricao());
            p.setPreco(produto.getPreco());
            p.setEstoque(produto.getEstoque());
            return produtoRepository.save(p);
        }).orElseThrow(() -> new ProdutoNotFoundException("Produto não encontrado com id: " + id));
    }

    public void excluirProduto(Long id) {
        produtoRepository.findById(id).ifPresentOrElse(produto -> {
            if (produto.getEstoque() == 0) {
                produtoRepository.delete(produto);
            } else {
                produto.setAtivo(false);
                produtoRepository.save(produto);
            }
        }, () -> {
            throw new ProdutoNotFoundException("Produto não encontrado com id: " + id);
        });
    }

}