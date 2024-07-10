package com.compass.desafio3.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "vendas")
public class Venda {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToMany(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "venda_id")
    private List<ItemVenda> itens;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", shape = JsonFormat.Shape.STRING)
    private LocalDateTime dataVenda;

    @Transient
    private double total; // Atributo transient que não será persistido no banco de dados TODO

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<ItemVenda> getItens() {
        return itens;
    }

    public void setItens(List<ItemVenda> itens) {
        this.itens = itens;
    }

    public LocalDateTime getDataVenda() {
        return dataVenda;
    }

    public void setDataVenda(LocalDateTime dataVenda) {
        this.dataVenda = dataVenda;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    // Método para calcular o total da venda com base nos itens atuais
    public void calcularTotal() {
        double novoTotal = 0.0;
        for (ItemVenda item : itens) {
            novoTotal += item.getQuantidade() * item.getPrecoUnitario();
        }
        this.total = novoTotal;
    }

}