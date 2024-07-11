# Compass-Desafio3-Ecommerce-API

Este projeto é uma API para um sistema de e-commerce, desenvolvida utilizando Java 17 e Spring Boot 3.3.1. A API permite gerenciar produtos e vendas, oferecendo funcionalidades como criação, leitura, atualização e exclusão de produtos e vendas, além de relatórios e controle de estoque.

## Funcionalidades

### Produto

- Permitir que os usuários criem, leiam, atualizem e excluam produtos.
- Validar os dados de entrada na criação de um produto (e.g., preço positivo).
- Impedir a exclusão de produtos que já foram incluídos em uma venda, mas permitir a inativação.
- Controlar o estoque dos produtos para impedir vendas com quantidade insuficiente.

### Vendas

- Permitir que os usuários criem, leiam, atualizem e excluam vendas (uma venda deve conter no mínimo 1 produto).
- Filtrar vendas por data.
- Gerar relatórios de vendas mensais e semanais.

### Cache

- Utilizar o cache nativo do Spring para otimizar leituras.
- Gerenciar o cache de vendas para garantir a atualização dos dados.

### Tratamento de Exceções

- Tratar todas as exceções de forma padronizada, com respostas consistentes.

### Regras de Negócio Gerais

- Todas as datas seguem o padrão ISO 8601.
- Campos de data são definidos automaticamente.

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.3.1
- MySQL
- JPA-Hibernate
- Spring Cache

## Arquitetura

O projeto segue a Clean Architecture para garantir a separação de responsabilidades e facilitar a manutenção e escalabilidade do código.

## Diagrama Entidade-Relacionamento (DER)

![Diagrama ER](https://cdn-0.plantuml.com/plantuml/png/TKz1QWCn3Bpx5UhUzmCbX51o2VHGQDfhKTSY5TXIOik1aitVQxmXzQ7rQMRacP4ssJ1PYS4zs1T5WeWfEn9Z-uRXdSJZ09ZXJ1N1nK5zpoe7O7_34uOj6ksvcZqQDa-RUIsVWS3KCC1gjKyecKbilNPNT-_O6SNV7gxiuxyo5eDJmMhqwAdlE2QQz4tOCB7swIz9VJ5jsSS5_n7yQeah4utKSqzvIZoXDd7RLJLX9qO7Ia3Pz5GMvvDg81H0utDRPBbbdiTHB_rrZp1K9dbmdNWTntc-xrc_tE3WDdLUOlW1)
