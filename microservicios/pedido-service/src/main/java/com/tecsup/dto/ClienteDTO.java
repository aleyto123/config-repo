package com.tecsup.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class ClienteDTO {

    private Long id;
    private String nombre;

    @JsonAlias("apellidos")
    private String apellido;

    private String direccion;
    private String estado;

    @JsonProperty("nro_dni")
    private String nroDni;
}
