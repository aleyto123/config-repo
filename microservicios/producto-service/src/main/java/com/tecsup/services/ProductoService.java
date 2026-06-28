package com.tecsup.services;

import com.tecsup.model.Producto;
import com.tecsup.repository.ProductoRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ProductoService {

    private final ProductoRepository repo;
    private final CategoriaCircuitService categoriaCircuitService;

    public ProductoService(ProductoRepository repo, CategoriaCircuitService categoriaCircuitService) {
        this.repo = repo;
        this.categoriaCircuitService = categoriaCircuitService;
    }

    public List<Producto> listar() {
        List<Producto> productos = repo.findAll();
        productos.forEach(this::agregarCategoria);
        return productos;
    }

    public Producto guardar(Producto producto) {
        Producto productoGuardado = repo.save(producto);
        agregarCategoria(productoGuardado);
        return productoGuardado;
    }

    public Producto obtener(Long id) {
        Producto producto = repo.findById(id).orElse(null);
        if (producto != null) {
            agregarCategoria(producto);
        }
        return producto;
    }

    public void eliminar(Long id) {
        repo.deleteById(id);
    }

    private void agregarCategoria(Producto producto) {
        if (producto.getCategoriaId() == null) {
            return;
        }
        producto.setCategoria(categoriaCircuitService.obtenerCategoria(producto.getCategoriaId()));
    }
}
