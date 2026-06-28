package com.tecsup.client;

import com.tecsup.dto.PedidoDTO;
import java.util.List;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "pedido-service")
public interface PedidoClient {

    @GetMapping("/api/pedidos/cliente/{clienteId}")
    List<PedidoDTO> listarPorCliente(@PathVariable Long clienteId);
}
