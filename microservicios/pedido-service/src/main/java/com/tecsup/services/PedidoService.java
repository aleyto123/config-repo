package com.tecsup.services;

import com.tecsup.dto.PedidoResponseDTO;
import com.tecsup.model.EstadoPedido;
import com.tecsup.model.Pedido;
import com.tecsup.repository.PedidoRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PedidoService {

    private final PedidoRepository repo;
    private final ClienteCircuitService clienteCircuitService;
    private final ProductoCircuitService productoCircuitService;

    public PedidoService(PedidoRepository repo, ClienteCircuitService clienteCircuitService, ProductoCircuitService productoCircuitService) {
        this.repo = repo;
        this.clienteCircuitService = clienteCircuitService;
        this.productoCircuitService = productoCircuitService;
    }

    public List<Pedido> listar() {
        return repo.findAll();
    }

    public List<PedidoResponseDTO> listarCompleto() {
        return repo.findAll()
                .stream()
                .map(this::convertirAResponse)
                .collect(Collectors.toList());
    }

    public List<Pedido> listarPorCliente(Long clienteId) {
        return repo.findByClienteId(clienteId);
    }

    public Pedido guardar(Pedido pedido) {
        if (pedido.getEstado() == null) {
            pedido.setEstado(EstadoPedido.PENDIENTE);
        }
        return repo.save(pedido);
    }

    public Pedido obtenerSimple(Long id) {
        return repo.findById(id).orElse(null);
    }

    public PedidoResponseDTO obtenerCompleto(Long id) {
        Pedido pedido = obtenerSimple(id);
        if (pedido == null) {
            return null;
        }

        return convertirAResponse(pedido);
    }

    public Pedido actualizarEstado(Long id, EstadoPedido estado) {
        Pedido pedido = obtenerSimple(id);
        if (pedido == null) {
            return null;
        }
        pedido.setEstado(estado);
        return repo.save(pedido);
    }

    private PedidoResponseDTO convertirAResponse(Pedido pedido) {
        PedidoResponseDTO response = new PedidoResponseDTO();
        response.setId(pedido.getId());
        response.setCantidad(pedido.getCantidad());
        response.setEstado(pedido.getEstado());
        response.setCliente(clienteCircuitService.obtenerCliente(pedido.getClienteId()));
        response.setProducto(productoCircuitService.obtenerProducto(pedido.getProductoId()));
        return response;
    }
}
