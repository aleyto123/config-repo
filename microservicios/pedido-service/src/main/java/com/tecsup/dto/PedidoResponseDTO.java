package com.tecsup.dto;

import com.tecsup.model.EstadoPedido;
import lombok.Data;

@Data
public class PedidoResponseDTO {

    private Long id;
    private int cantidad;
    private EstadoPedido estado;
    private ClienteDTO cliente;
    private ProductoDTO producto;
}
