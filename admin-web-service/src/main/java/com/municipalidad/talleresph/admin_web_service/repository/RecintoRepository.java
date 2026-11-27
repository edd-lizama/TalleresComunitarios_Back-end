package com.municipalidad.talleresph.admin_web_service.repository;

import com.municipalidad.talleresph.admin_web_service.model.Recinto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RecintoRepository extends JpaRepository<Recinto, Long> {
    List<Recinto> findByActivoTrueOrderByIdAsc();
}
