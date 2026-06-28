package com.tecsup.controller;

import com.tecsup.dto.PedidoResponseDTO;
import com.tecsup.model.EstadoPedido;
import com.tecsup.model.Pedido;
import com.tecsup.services.PedidoService;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pedidos")
public class PedidoController {

    private final PedidoService service;

    public PedidoController(PedidoService service) {
        this.service = service;
    }

    @GetMapping
    public List<PedidoResponseDTO> listar() {
        return service.listarCompleto();
    }

    @GetMapping("/cliente/{clienteId}")
    public List<Pedido> listarPorCliente(@PathVariable Long clienteId) {
        return service.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> obtener(@PathVariable Long id) {
        PedidoResponseDTO pedido = service.obtenerCompleto(id);
        return pedido == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(pedido);
    }

    @PostMapping
    public Pedido guardar(@Valid @RequestBody Pedido pedido) {
        return service.guardar(pedido);
    }

    @PatchMapping("/{id}/pendiente")
    public ResponseEntity<Pedido> marcarPendiente(@PathVariable Long id) {
        return responderEstado(id, EstadoPedido.PENDIENTE);
    }

    @PatchMapping("/{id}/atendido")
    public ResponseEntity<Pedido> marcarAtendido(@PathVariable Long id) {
        return responderEstado(id, EstadoPedido.ATENDIDO);
    }

    @PatchMapping("/{id}/anular")
    public ResponseEntity<Pedido> anular(@PathVariable Long id) {
        return responderEstado(id, EstadoPedido.ANULADO);
    }

    private ResponseEntity<Pedido> responderEstado(Long id, EstadoPedido estado) {
        Pedido pedido = service.actualizarEstado(id, estado);
        return pedido == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(pedido);
    }
}
