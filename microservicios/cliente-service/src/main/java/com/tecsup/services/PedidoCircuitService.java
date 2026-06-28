package com.tecsup.services;

import com.tecsup.client.PedidoClient;
import com.tecsup.dto.PedidoDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class PedidoCircuitService {

    private final PedidoClient pedidoClient;

    public PedidoCircuitService(PedidoClient pedidoClient) {
        this.pedidoClient = pedidoClient;
    }

    @Retry(name = "pedidoRetry", fallbackMethod = "pedidoFallback")
    @CircuitBreaker(name = "pedidoService", fallbackMethod = "pedidoFallback")
    public List<PedidoDTO> listarPorCliente(Long clienteId) {
        System.out.println("Llamando a pedido-service...");
        return pedidoClient.listarPorCliente(clienteId);
    }

    public List<PedidoDTO> pedidoFallback(Long clienteId, Exception ex) {
        System.out.println("Fallback pedido-service: " + ex.getMessage());
        PedidoDTO dto = new PedidoDTO();
        dto.setClienteId(clienteId);
        dto.setEstado("PEDIDO_SERVICE_NO_DISPONIBLE");

        List<PedidoDTO> pedidos = new ArrayList<>();
        pedidos.add(dto);
        return pedidos;
    }
}
