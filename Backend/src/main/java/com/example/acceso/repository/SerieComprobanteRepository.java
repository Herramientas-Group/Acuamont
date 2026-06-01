package com.example.acceso.repository;

import com.example.acceso.model.SerieComprobante;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SerieComprobanteRepository extends JpaRepository<SerieComprobante, Long> {
    Optional<SerieComprobante> findByNombre(String nombre);
    boolean existsByNombre(String nombre);
    List<SerieComprobante> findAllByEstadoNot(Integer estado);
    List<SerieComprobante> findAllByEstado(Integer estado);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT s FROM SerieComprobante s WHERE s.id = :id")
    Optional<SerieComprobante> findByIdWithLock(@Param("id") Long id);
}
