# 📌 FinnC API (branch master)

API REST para gerenciamento financeiro desenvolvida com **Java** e **Spring Boot**, responsável pelo controle de períodos, categorias de despesas, despesas, autenticação de usuários e integração com IA.

---

## 🚀 Tecnologias

* Java 17
* Spring Boot
* Spring Security
* JWT
* Spring Data JPA
* PostgreSQL
* Docker
* Maven
* n8n
* Groq API

---

## 📖 Sobre o projeto

O **FinnC** é um sistema de gerenciamento financeiro que permite ao usuário organizar seus gastos por **períodos** (mês/ano).

Dentro de cada período é possível criar **containers**, que representam categorias de orçamento, como Alimentação, Transporte ou Lazer. Cada container possui um valor máximo disponível, uma data de término e pode ser configurado para se repetir automaticamente em períodos futuros.

Cada container pode conter diversas despesas, porém a soma dos seus valores não pode ultrapassar o orçamento definido para aquela categoria, garantindo um maior controle financeiro.

O sistema também possui uma lógica de recorrência, permitindo que containers e despesas sejam automaticamente replicados entre períodos até a data de término configurada.

---

## ✨ Funcionalidades

* Cadastro e autenticação de usuários
* Login utilizando JWT
* CRUD de períodos
* CRUD de containers
* CRUD de despesas
* Controle de orçamento por categoria
* Recorrência automática de containers e despesas
* Integração com IA
* Validação das regras de negócio
* Controle do saldo disponível do período

---

## 🔒 Segurança

* Spring Security
* Autenticação com JWT
* Criptografia de senhas utilizando BCrypt
* Rotas protegidas

---

## 🤖 Integração com IA

O projeto possui integração com um fluxo do **n8n**, responsável por intermediar a comunicação com um modelo de IA.

A API envia as informações do usuário para o workflow do n8n, que processa a solicitação e retorna uma resposta contextualizada ao usuário.

---

## ⚙️ Como executar

### 1. Clone o repositório

```bash
git clone https://github.com/CarlosAudre/Finnc-Spring.git
cd Finnc-Spring
```

### 2. Configure o ambiente

* Utilize o **JDK 17**.
* Instale o **PostgreSQL**.
* Crie um banco de dados chamado **finnc**.

### 3. Configure o `application.properties`

```properties
DB_HOST=
DB_PORT=
DB_NAME=finnc
DB_USERNAME=
DB_PASSWORD=
JWT_SECRET=
```

### 4. Execute a aplicação

Caso utilize o Maven Wrapper:

```bash
./mvnw spring-boot:run
```

Ou, caso utilize o Maven instalado na máquina:

```bash
mvn spring-boot:run
```

---

## 🚧 Desafios do projeto

Durante o desenvolvimento do FinnC, alguns dos principais desafios foram:

* Implementação da autenticação utilizando JWT.
* Modelagem das regras de recorrência entre períodos, containers e despesas.
* Garantia da consistência dos orçamentos através de validações de negócio.
* Integração entre Spring Boot, n8n e IA.
* Deploy da aplicação utilizando Docker.

---

## 📄 Licença

Projeto desenvolvido para fins de estudo e demonstração de conhecimentos em desenvolvimento Full Stack.
