package com.example.acceso.repository;

import com.example.acceso.model.Producto;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository  extends JpaRepository<Producto, Long> {
    Optional<Producto> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<Producto> findAllByEstadoNot(Integer estado);
    Long countByEstadoNot(Integer estado);
    @Query("SELECT p.nombre, SUM(dv.cantidad) as totalVendido FROM DetalleVenta dv JOIN dv.producto p GROUP BY p.nombre ORDER BY totalVendido DESC")
    List<Object[]> findTop5ProductosMasVendidos(Pageable pageable);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Producto p WHERE p.id = :id")
    Optional<Producto> findByIdWithLock(@Param("id") Long id);
}
