# 📦 Order Service !

[![Java](https://img.shields.io/badge/Java-17-red?logo=java&logoColor=white)](https://www.oracle.com/java/)  
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen?logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)  
[![Postgres](https://img.shields.io/badge/Postgres-16-blue?logo=postgresql&logoColor=white)](https://www.postgresql.org/)  
[![RabbitMQ](https://img.shields.io/badge/RabbitMQ-3.x-orange?logo=rabbitmq&logoColor=white)](https://www.rabbitmq.com/)  
[![Docker](https://img.shields.io/badge/Docker-🐳-blue?logo=docker&logoColor=white)](https://www.docker.com/)

---

## 📖 Sobre o projeto
O **Order Service** é um microsserviço desenvolvido em **Spring Boot** para gerenciamento de pedidos.  

Fluxo do desafio:
- O **Sistema A** envia pedidos via **RabbitMQ**.  
- O **Order Service** processa, calcula o total e persiste no banco.  
- O **Sistema B** consulta os pedidos via **API REST**.  

---

## 🏗 Arquitetura do desafio

```
sequenceDiagram
    A->>MQ: Envia Pedido (JSON)
    MQ->>Order: Entrega Mensagem
    Order->>Order: Valida e Calcula Total
    Order->>DB: Salva Pedido com Status
    B->>Order: GET /api/orders
    Order-->>B: Retorna Pedido (Total + Status)

```

---

## 🚀 Tecnologias principais
- [Java 17](https://www.oracle.com/java/)  
- [Spring Boot](https://spring.io/projects/spring-boot)  
- [PostgreSQL](https://www.postgresql.org/)  
- [RabbitMQ](https://www.rabbitmq.com/)  
- [Maven](https://maven.apache.org/)  
- [Docker](https://www.docker.com/)  
- [JWT](https://jwt.io/)  

---

## 🐳 Rodando com Docker Compose

O arquivo `docker-compose.yml` está configurado para subir o **Postgres** e o **RabbitMQ**.

```bash
docker-compose up -d
```

Isso disponibiliza:
- Postgres → `localhost:5432` (db: `orderdb`, user: `admin`, pass: `admin`)  
- RabbitMQ → `localhost:5672` (painel de admin em `http://localhost:15672`)  

---

## ▶️ Rodando a aplicação

```bash
mvn clean install
mvn spring-boot:run
```

A aplicação ficará disponível em:  
👉 `http://localhost:9091`

---

## 🔗 Endpoints principais

- `GET /api/orders` → lista todos os pedidos  
- `GET /api/orders/{id}` → retorna pedido específico  

Exemplo no Insomnia/Postman:
```http
GET http://localhost:9091/api/orders
```

---

## 📦 Exemplo de pedido JSON (enviado por A)

```json
{
  "orderId": "123",
  "items": [
    { "itemId": "p1", "name": "Produto 1", "quantity": 2, "price": 50.0 },
    { "itemId": "p2", "name": "Produto 2", "quantity": 1, "price": 100.0 }
  ]
}
```

---

## ✅ Como o desafio foi atendido

| Requisito do desafio              | Onde foi atendido |
|-----------------------------------|------------------|
| Receber pedidos de A via mensageria | `OrderService` com `@RabbitListener` |
| Processar pedido e calcular total  | `OrderProcessingService` |
| Evitar duplicação de pedidos       | `existsByOrderId` no `OrderRepository` |
| Disponibilizar para B via REST     | Endpoints em `OrderController` |

---

## 🛠️ Configuração

O projeto já está preparado para rodar com **Docker Compose**.  
Você pode usar o `docker-compose.yml` da raiz do projeto ou o que está na pasta `info/`.  

### ▶️ application.properties

```properties
server.port=9091

spring.datasource.url=jdbc:postgresql://localhost:5432/orderdb
spring.datasource.username=admin
spring.datasource.password=admin

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect

rabbitmq.queuename=ORDERS_QUEUE
rabbitmq.host=localhost
rabbitmq.username=admin
rabbitmq.password=admin
rabbitmq.port=5672
```

### ▶️ Como rodar

```bash
# Opção 1 - Executar o docker-compose da raiz do projeto
docker-compose up -d
# RabbitMQ em localhost:5672 (painel em http://localhost:15672)

# Opção 2 - Executar o docker-compose da pasta info
cd info
docker-compose up -d
# RabbitMQ em localhost:8090 (painel em http://localhost:8080)
```

### 📂 Conteúdo do `info/docker-compose.yml`

```yaml
version: "3.9"

services:
  postgres:
    image: postgres:16
    container_name: postgres
    restart: always
    environment:
      POSTGRES_USER: admin
      POSTGRES_PASSWORD: admin
      POSTGRES_DB: orderdb
    ports:
      - "5432:5432"
    volumes:
      - postgres_data:/var/lib/postgresql/data

  rabbitmq:
    image: rabbitmq:3-management
    container_name: rabbitmq
    restart: always
    ports:
      - "8090:5672"    
      - "8080:15672"  
    environment:
      RABBITMQ_DEFAULT_USER: admin
      RABBITMQ_DEFAULT_PASS: admin
    volumes:
      - rabbitmq_data:/var/lib/rabbitmq

volumes:
  postgres_data:
  rabbitmq_data:
```
📌 **Importante:** escolha apenas uma das opções (raiz ou pasta `info/`) e mantenha a porta configurada corretamente no `application.properties`.
