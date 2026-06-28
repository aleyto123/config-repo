package com.tecsup.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
@Entity
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nombre;

    @NotBlank
    private String apellidos;

    @NotBlank
    private String direccion;

    @NotBlank
    private String estado;

    @NotBlank
    @Pattern(regexp = "\\d{8}", message = "El DNI debe contener 8 digitos")
    @JsonProperty("nro_dni")
    @Column(name = "nro_dni", length = 8, nullable = false, unique = true)
    private String nroDni;
}
