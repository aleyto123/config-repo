package com.tecsup.controller;

import com.tecsup.model.Producto;
import com.tecsup.services.ProductoService;
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
@RequestMapping("/api/productos")
public class ProductoController {

    private final ProductoService service;
    private final long productoDelayMs;

    public ProductoController(ProductoService service, @Value("${demo.producto-delay-ms:0}") long productoDelayMs) {
        this.service = service;
        this.productoDelayMs = productoDelayMs;
    }

    @GetMapping
    public List<Producto> listar() {
        return service.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtener(@PathVariable Long id) throws InterruptedException {
        if (productoDelayMs > 0) {
            System.out.println("Simulando demora producto-service: " + productoDelayMs + " ms");
            Thread.sleep(productoDelayMs);
        }
        Producto producto = service.obtener(id);
        return producto == null ? ResponseEntity.notFound().build() : ResponseEntity.ok(producto);
    }

    @PostMapping
    public Producto guardar(@Valid @RequestBody Producto producto) {
        return service.guardar(producto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Producto> actualizar(@PathVariable Long id, @Valid @RequestBody Producto producto) {
        if (service.obtener(id) == null) {
            return ResponseEntity.notFound().build();
        }
        producto.setId(id);
        return ResponseEntity.ok(service.guardar(producto));
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
