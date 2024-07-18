# Compass-Desafio3-Ecommerce-API

Este projeto é uma API para um sistema de e-commerce desenvolvido com Java 17 e Spring Boot 3.3.1. A aplicação oferece funcionalidades avançadas para gerenciar produtos, vendas, autenticação JWT e integração com banco de dados MySQL via JPA-Hibernate. A arquitetura segue os princípios da Clean Architecture, visando alta coesão e baixo acoplamento.

---

## Funcionalidades

### Produto

- Operações CRUD para produtos.
- Validação de dados na criação de produtos (e.g., preço positivo).
- Inativação de produtos ao invés de exclusão para produtos vinculados a vendas.
- Controle de estoque para evitar vendas com estoque insuficiente.

### Vendas

- Operações CRUD para vendas, com pelo menos um produto por venda.
- Filtro de vendas por data.
- Relatórios de vendas mensais e semanais.

### Cache

- Utilização de cache para otimização de leituras.
- Gerenciamento de cache para manter os dados atualizados.

### Tratamento de Exceções

- Tratamento padronizado de exceções com respostas consistentes.

---

## Regras de Negócio - Geral

- Campos de data no formato ISO 8601 (`yyyy-MM-dd'T'HH:mm:ss'Z'`).
- Campos de data definidos automaticamente.

---

## Autenticação e Autorização

- Autenticação via Token JWT.
- Autorização baseada em roles.

---

## Reset de Senha

- Implementação de método para resetar senha, com geração de token único enviado por e-mail.

---

## Tecnologias Utilizadas

- Java 17
- Spring Boot 3.3.1
- MySQL
- JPA-Hibernate
- Caffeine Cache
- JWT (JSON Web Token)

---

## Arquitetura

O projeto segue os princípios da Clean Architecture, promovendo separação clara de responsabilidades e facilitando manutenção e escalabilidade.

---

## Diagrama ER

![Diagrama ER](https://www.plantuml.com/plantuml/png/dPB1RXGn38RlynJMdA1e3xqZQA4BMed3YhBSJMAcKHDxcZYb2FNT2Pd9xenM0Wbf94y-vxT_9xbAYb5pyDs8vXu7WdwxCH82HKDmlT_ryAi3A1-nElq9-oWsgrGAy0e-2G_WR5rjDZEyrUWAPWcq9fQIYSwWpFX6yZTFC4Oo3UrOQQ08b5GUSjE_4_643AZkKPRD7iLPSFo7Otpnih3dxkJsa396zxzkAQ3pQvI8xt167p8R5FXUmtgRoZtnPqgajqrGknlqDwZqnGMQzcoVHXTn6vQEteltPDG9ltuBzAJ4Di77YAnahxFUIIoZlO61jAqSfJewjFubjyUKVaYqPub3eiWOwEomlX9R_FjHhIUmg5X5SzPEjwcYw9Uw2zWfXQcyK-9KgHnl9qznRQTf_cN9iS93ngAnQ6dXfBsh0pkjvHSoERxZSOgjTyFzPL_sGd_HGvkmna9QucImWzF_Kha_rcwJDlLbAzizTrVb9mV_6m00)

---