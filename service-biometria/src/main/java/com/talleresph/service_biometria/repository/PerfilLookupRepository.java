package com.talleresph.service_biometria.repository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class PerfilLookupRepository {

    @PersistenceContext
    private EntityManager entityManager;

    @SuppressWarnings("unchecked")
    public List<Integer> findIdsPerfilByIdUsuario(Integer idUsuario) {
        List<Number> ids = entityManager
                .createNativeQuery("SELECT id_perfil FROM perfil WHERE id_usuario = :idUsuario ORDER BY id_perfil")
                .setParameter("idUsuario", idUsuario)
                .getResultList();
        return ids.stream().map(Number::intValue).toList();
    }
}
