package com.example.acceso.repository;

import com.example.acceso.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;

@Repository
public interface VentaRepository extends JpaRepository<Venta, Long> {
    @Query("SELECT v FROM Venta v JOIN FETCH v.detalleVentas d WHERE d.producto.id = :productoId ORDER BY v.fecha DESC")
    List<Venta> findVentasByProductoId(@Param("productoId") Long productoId);
    List<Venta> findAllByEstadoNot(Integer estado);
    Long countByEstado(Integer estado);
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado <> 2 AND FUNCTION('DATE', v.fecha) = CURRENT_DATE")
    BigDecimal sumTotalVentasDelDia();
    @Query("SELECT COALESCE(SUM(v.total), 0) FROM Venta v WHERE v.estado <> 2 AND YEAR(v.fecha) = YEAR(CURRENT_DATE) AND MONTH(v.fecha) = MONTH(CURRENT_DATE)")
    BigDecimal sumTotalVentasDelMes();
    @Query("SELECT COALESCE(SUM(v.deuda), 0) FROM Venta v WHERE v.estado <> 2")
    BigDecimal sumTotalDeuda();
    List<Venta> findAllByEstado(Integer estado);
}
