📌 FinnC API
API REST para gerenciamento financeiro desenvolvida com Java e Spring Boot, responsável pelo controle de períodos, categorias (containers), despesas, autenticação de usuários e integração com IA.

🚀 Tecnologias
Java 17
Spring Boot
Spring Security
JWT
Spring Data JPA
PostgreSQL
Docker
Maven
n8n (integração)
Groq API (IA)
📖 Sobre o projeto

O FinnC é um sistema de gerenciamento financeiro onde o usuário organiza seus gastos por períodos (mês/ano).

Cada período pode possuir diversos containers, que representam categorias de orçamento (como Alimentação, Lazer ou Transporte).

Dentro de cada container é possível cadastrar despesas, respeitando o limite financeiro definido para aquela categoria.

O sistema também suporta recorrência automática de containers e despesas, permitindo o planejamento financeiro para vários meses.

✨ Funcionalidades
Cadastro e autenticação de usuários
Login com JWT
CRUD de períodos
CRUD de containers
CRUD de despesas
Controle de orçamento por categoria
Recorrência automática
Integração com IA
Validações de regras de negócio
Controle de saldo do período

🔒 Segurança
Spring Security
JWT Authentication
BCrypt
Rotas protegidas


🤖 Integração com IA

O projeto possui integração com um fluxo do n8n, responsável por encaminhar mensagens para um modelo de IA.

A API envia as informações do usuário para o workflow do n8n, que processa a solicitação e retorna uma resposta contextualizada.

