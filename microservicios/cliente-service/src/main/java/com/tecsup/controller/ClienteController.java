package com.tecsup.controller;

import com.tecsup.dto.PedidoDTO;
import com.tecsup.model.Cliente;
import com.tecsup.services.ClienteService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService service;
    private final long clienteDelayMs;

    public ClienteController(ClienteService service, @Value("${demo.cliente-delay-ms:0}") long clienteDelayMs) {
        this.service = service;
        this.clienteDelayMs = clienteDelayMs;
    }

    @GetMapping
    public List<Cliente> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Cliente> obtener(@PathVariable Long id) throws InterruptedException {
        if (clienteDelayMs > 0) {
            System.out.println("Simulando demora cliente-service: " + clienteDelayMs + " ms");
            Thread.sleep(clienteDelayMs);
        }
        Cliente cliente = service.obtener(id);
        return cliente == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(cliente);
    }

    @GetMapping("/{id}/pedidos")
    public ResponseEntity<List<PedidoDTO>> listarPedidos(@PathVariable Long id) {
        if (service.obtener(id) == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(service.listarPedidos(id));
    }

    @PostMapping
    public Cliente guardar(@Valid @RequestBody Cliente cliente) {
        return service.guardar(cliente);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Cliente> actualizar(@PathVariable Long id, @Valid @RequestBody Cliente cliente) {
        if (service.obtener(id) == null) {
            return ResponseEntity.notFound().build();
        }
        cliente.setId(id);
        return ResponseEntity.ok(service.guardar(cliente));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (service.obtener(id) == null) {
            return ResponseEntity.notFound().build();
        }
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}
