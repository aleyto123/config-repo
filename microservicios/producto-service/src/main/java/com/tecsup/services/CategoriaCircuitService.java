package com.tecsup.services;

import com.tecsup.client.CategoriaClient;
import com.tecsup.dto.CategoriaDTO;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.springframework.stereotype.Service;

@Service
public class CategoriaCircuitService {

    private final CategoriaClient categoriaClient;

    public CategoriaCircuitService(CategoriaClient categoriaClient) {
        this.categoriaClient = categoriaClient;
    }

    @Bulkhead(name = "categoriaBulkhead", fallbackMethod = "categoriaFallback")
    @Retry(name = "categoriaRetry", fallbackMethod = "categoriaFallback")
    @CircuitBreaker(name = "categoriaService", fallbackMethod = "categoriaFallback")
    public CategoriaDTO obtenerCategoria(Long id) {
        System.out.println("Llamando a categoria-service...");
        return categoriaClient.obtenerCategoria(id);
    }

    public CategoriaDTO categoriaFallback(Long id, Exception ex) {
        System.out.println("Fallback categoria-service: " + ex.getMessage());
        CategoriaDTO dto = new CategoriaDTO();
        dto.setId(id);
        dto.setNombre("Categoria temporal no disponible");
        return dto;
    }
}
