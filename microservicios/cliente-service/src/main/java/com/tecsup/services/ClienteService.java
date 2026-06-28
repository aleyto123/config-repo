package com.tecsup.services;

import com.tecsup.dto.PedidoDTO;
import com.tecsup.model.Cliente;
import com.tecsup.repository.ClienteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ClienteService {

    private final ClienteRepository repo;
    private final PedidoCircuitService pedidoCircuitService;

    public ClienteService(ClienteRepository repo, PedidoCircuitService pedidoCircuitService) {
        this.repo = repo;
        this.pedidoCircuitService = pedidoCircuitService;
    }

    public List<Cliente> listar() {
        return repo.findAll();
    }

    public Cliente guardar(Cliente cliente) {
        return repo.save(cliente);
    }

    public Cliente obtener(Long id) {
        return repo.findById(id).orElse(null);
    }

    public List<PedidoDTO> listarPedidos(Long clienteId) {
        return pedidoCircuitService.listarPorCliente(clienteId);
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }
}
