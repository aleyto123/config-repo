package com.tecsup.services;

import com.tecsup.client.ProductoClient;
import com.tecsup.dto.ProductoDTO;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class ProductoCircuitService {

    private final ProductoClient productoClient;

    public ProductoCircuitService(ProductoClient productoClient) {
        this.productoClient = productoClient;
    }

    @Bulkhead(name = "productoBulkhead", fallbackMethod = "productoFallback")
    @CircuitBreaker(name = "productoService", fallbackMethod = "productoFallback")
    public ProductoDTO obtenerProducto(Long id) {
        System.out.println("Llamando a producto-service...");
        return productoClient.obtenerProducto(id);
    }

    public ProductoDTO productoFallback(Long id, Exception ex) {
        System.out.println("Fallback producto-service: " + ex.getMessage());
        ProductoDTO dto = new ProductoDTO();
        dto.setId(id);
        dto.setNombre("Producto temporal no disponible");
        dto.setPrecio(0.0);
        return dto;
    }
}
