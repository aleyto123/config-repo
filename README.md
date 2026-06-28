# Laboratorio 14 de Desarrollo de Aplicaciones Web

- `config-server` en el puerto `8888`.
- Repositorio Git local `config-repo` con configuraciones remotas.
- Eureka Server en el puerto `8761`.
- `categoria-service` en el puerto `8082`.
- `producto-service` en el puerto `8083`.
- `cliente-service` en el puerto `8084`.
- `pedido-service` en el puerto `8085`.
- Circuit Breaker, Retry y Bulkhead con Resilience4j.

## Estructura

- `microservicios/config-server`: servidor Spring Cloud Config.
- `config-repo`: repositorio Git local usado por Config Server.
- `microservicios/eureka-server`: servidor Eureka.
- `microservicios/categoria-service`: CRUD de categorias.
- `microservicios/producto-service`: CRUD de productos y consumo de categorias con fallback.
- `microservicios/cliente-service`: CRUD de clientes y consumo de pedidos con fallback.
- `microservicios/pedido-service`: registro de pedidos y consumo de productos con fallback.

## Base de datos

Ejecutar en MySQL:

```sql
CREATE DATABASE IF NOT EXISTS bd_categoria_dev;
CREATE DATABASE IF NOT EXISTS bd_producto_dev;
CREATE DATABASE IF NOT EXISTS bd_cliente_dev;
CREATE DATABASE IF NOT EXISTS bd_pedido_dev;
```

Tambien esta disponible en `microservicios/crear-bases.sql`.

Empiezar levantando los servicios por el siguiente orden:

1. `config-server`
2. `eureka-server`
3. `categoria-service`
4. `producto-service`
5. `cliente-service`
6. `pedido-service`

- Config Server: `http://localhost:8888/producto-service/dev`
- Eureka: `http://localhost:8761`
- Circuit Breaker normal y con servicios apagados segun la guia de pruebas.
