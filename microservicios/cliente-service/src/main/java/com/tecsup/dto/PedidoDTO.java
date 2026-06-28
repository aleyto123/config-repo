package com.tecsup.dto;

import lombok.Data;

@Data
public class PedidoDTO {

    private Long id;
    private Long clienteId;
    private Long productoId;
    private int cantidad;
    private String estado;
}
