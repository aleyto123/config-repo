# Pruebas Lab 14

## 1. Crear bases de datos

Ejecuta este SQL en MySQL antes de levantar los servicios:

```sql
CREATE DATABASE IF NOT EXISTS bd_categoria_dev;
CREATE DATABASE IF NOT EXISTS bd_producto_dev;
CREATE DATABASE IF NOT EXISTS bd_cliente_dev;
CREATE DATABASE IF NOT EXISTS bd_pedido_dev;
```

Como el proyecto se publica en GitHub, la clave no queda escrita en los YAML. Antes de levantar los servicios, ejecuta en cada terminal:

```powershell
$env:MYSQL_PASSWORD="tu_clave_mysql"
```

## 2. Levantar servicios

Abre una terminal por servicio y ejecuta en este orden:

```powershell
cd C:\Users\Mauricio\Desktop\tecsupp\lab14web\microservicios\config-server
mvn spring-boot:run
```

```powershell
cd C:\Users\Mauricio\Desktop\tecsupp\lab14web\microservicios\eureka-server
mvn spring-boot:run
```

```powershell
cd C:\Users\Mauricio\Desktop\tecsupp\lab14web\microservicios\categoria-service
mvn spring-boot:run
```

```powershell
cd C:\Users\Mauricio\Desktop\tecsupp\lab14web\microservicios\producto-service
mvn spring-boot:run
```

```powershell
cd C:\Users\Mauricio\Desktop\tecsupp\lab14web\microservicios\cliente-service
mvn spring-boot:run
```

```powershell
cd C:\Users\Mauricio\Desktop\tecsupp\lab14web\microservicios\pedido-service
mvn spring-boot:run
```

## 3. Capturas de Config Server

Abre en navegador:

- http://localhost:8888/categoria-service/default
- http://localhost:8888/producto-service/dev
- http://localhost:8888/cliente-service/dev
- http://localhost:8888/pedido-service/dev

Captura donde se vea `propertySources` y los puertos `8082`, `8083`, `8084`, `8085`.

## 4. Crear datos para probar

Usa Postman en este orden.

POST http://localhost:8082/api/categorias

```json
{
  "nombre": "Tecnologia",
  "estado": "ACTIVO"
}
```

POST http://localhost:8083/api/productos

```json
{
  "nombre": "Laptop",
  "precio": 2500,
  "stock": 10,
  "categoriaId": 1
}
```

POST http://localhost:8084/api/clientes

```json
{
  "nombre": "Juan",
  "apellidos": "Perez",
  "direccion": "Lima",
  "estado": "ACTIVO",
  "nro_dni": "12345678"
}
```

POST http://localhost:8085/api/pedidos

```json
{
  "clienteId": 1,
  "productoId": 1,
  "cantidad": 2
}
```

## 5. Capturas antes de la falla

- Eureka: http://localhost:8761
- Producto con categoria: http://localhost:8083/api/productos/1
- Pedido con producto: http://localhost:8085/api/pedidos/1
- Cliente con pedidos: http://localhost:8084/api/clientes/1/pedidos

## 6. Capturas con Circuit Breaker

Caso producto-service -> categoria-service:

1. Deten `categoria-service`.
2. Abre http://localhost:8083/api/productos/1
3. Debe salir `Categoria temporal no disponible`.

Caso pedido-service -> producto-service:

1. Vuelve a iniciar `categoria-service`.
2. Deten `producto-service`.
3. Abre http://localhost:8085/api/pedidos/1
4. Debe salir `Producto temporal no disponible`.

Caso cliente-service -> pedido-service:

1. Vuelve a iniciar `producto-service`.
2. Deten `pedido-service`.
3. Abre http://localhost:8084/api/clientes/1/pedidos
4. Debe salir `PEDIDO_SERVICE_NO_DISPONIBLE`.

## 7. Captura de Bulkhead

1. Edita `C:\Users\Mauricio\Desktop\tecsupp\lab14web\config-repo\categoria-service.yml`.
2. Cambia `demo.categoria-delay-ms: 0` por `demo.categoria-delay-ms: 10000`.
3. En esa carpeta ejecuta:

```powershell
git add categoria-service.yml
git commit -m "Activar demora para bulkhead"
```

4. Reinicia `config-server`, `categoria-service` y `producto-service`.
5. Abre varias veces rápido http://localhost:8083/api/productos/1
6. Algunas llamadas esperarán y otras deben responder con fallback por Bulkhead.

Cuando termines la captura, vuelve a dejar `demo.categoria-delay-ms: 0`, haz commit y reinicia.

## 8. Actividad final: Retry cliente y Bulkhead producto desde pedido-service

Esta parte se prueba con:

GET http://localhost:8085/api/pedidos

El endpoint ahora devuelve pedidos completos, por eso `pedido-service` consulta a:

- `cliente-service`, usando Retry.
- `producto-service`, usando Bulkhead.

### Codigo de Retry

Captura este archivo:

`microservicios/pedido-service/src/main/java/com/tecsup/services/ClienteCircuitService.java`

Debe verse:

```java
@Retry(name = "clienteRetry", fallbackMethod = "clienteFallback")
@CircuitBreaker(name = "clienteService", fallbackMethod = "clienteFallback")
public ClienteDTO obtenerCliente(Long id) {
    System.out.println("Llamando a cliente-service...");
    return clienteClient.obtenerCliente(id);
}
```

### Codigo de Bulkhead

Captura este archivo:

`microservicios/pedido-service/src/main/java/com/tecsup/services/ProductoCircuitService.java`

Debe verse:

```java
@Bulkhead(name = "productoBulkhead", fallbackMethod = "productoFallback")
@CircuitBreaker(name = "productoService", fallbackMethod = "productoFallback")
public ProductoDTO obtenerProducto(Long id) {
    System.out.println("Llamando a producto-service...");
    return productoClient.obtenerProducto(id);
}
```

### Configuracion application.yml

Captura este archivo del Config Server:

`config-repo/pedido-service.yml`

Debe verse:

- `clienteRetry` con `maxAttempts: 3`.
- `productoBulkhead` con `maxConcurrentCalls: 2`.
- Timeout Feign de `cliente-service` en `readTimeout: 2000`.
- Timeout Feign de `producto-service` en `readTimeout: 15000`.

Tambien captura:

- `config-repo/cliente-service.yml`, donde esta `demo.cliente-delay-ms: 5000`.
- `config-repo/producto-service.yml`, donde esta `demo.producto-delay-ms: 10000`.

### Prueba Retry

1. Levanta todos los servicios.
2. Crea datos si aun no los tienes.
3. Ejecuta:

GET http://localhost:8085/api/pedidos

4. Mira la consola de `pedido-service`.

Como `cliente-service` demora 5 segundos y Feign espera 2 segundos, veras varios mensajes:

```text
Llamando a cliente-service...
Llamando a cliente-service...
Llamando a cliente-service...
Fallback cliente-service: ...
```

En la respuesta JSON debe aparecer algo parecido a:

```json
"cliente": {
  "id": 1,
  "nombre": "Cliente temporal no disponible",
  "estado": "CLIENTE_SERVICE_NO_DISPONIBLE"
}
```

### Prueba Bulkhead

1. Deja todos los servicios levantados.
2. Abre 3 o 4 pestañas del navegador con:

GET http://localhost:8085/api/pedidos

3. Presiona Enter rapido en todas.
4. Mira la consola de `pedido-service`.

Como `producto-service` demora 10 segundos y el Bulkhead solo permite 2 llamadas concurrentes, las solicitudes que exceden el limite responden con fallback:

```text
Llamando a producto-service...
Fallback producto-service: ...
```

En la respuesta JSON debe aparecer:

```json
"producto": {
  "id": 1,
  "nombre": "Producto temporal no disponible",
  "precio": 0.0
}
```

### Explicacion corta para la entrega

Retry vuelve a intentar una operacion cuando falla por un problema temporal, por ejemplo lentitud, timeout o una caida momentanea. En esta practica se aplica a la consulta del cliente porque `cliente-service` demora 5 segundos y `pedido-service` reintenta antes de ejecutar el fallback.

Bulkhead limita cuantas llamadas pueden entrar al mismo tiempo a un recurso lento o saturado. En esta practica se aplica a la consulta del producto porque `producto-service` demora 10 segundos; asi, solo algunas solicitudes entran y las demas reciben fallback sin bloquear todo `pedido-service`.

Retry conviene cuando el error puede solucionarse esperando un poco y reintentando. Bulkhead conviene cuando se necesita proteger el sistema para que un servicio lento no consuma todos los hilos o conexiones disponibles.
