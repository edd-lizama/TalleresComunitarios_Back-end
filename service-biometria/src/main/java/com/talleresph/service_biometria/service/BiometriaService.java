package com.talleresph.service_biometria.service;

import com.talleresph.service_biometria.dto.RegistroBiometricoRequestDTO;
import com.talleresph.service_biometria.dto.RegistroBiometricoResponseDTO;
import com.talleresph.service_biometria.exception.AccesoDenegadoException;
import com.talleresph.service_biometria.exception.RecursoNoEncontradoException;
import com.talleresph.service_biometria.model.RegistroBiometrico;
import com.talleresph.service_biometria.repository.PerfilLookupRepository;
import com.talleresph.service_biometria.repository.RegistroBiometricoRepository;
import com.talleresph.service_biometria.util.ImcCalculator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

@Service
public class BiometriaService {

    private static final ZoneId ZONA_CHILE = ZoneId.of("America/Santiago");

    private final RegistroBiometricoRepository registroRepository;
    private final PerfilLookupRepository perfilLookupRepository;

    public BiometriaService(RegistroBiometricoRepository registroRepository,
                            PerfilLookupRepository perfilLookupRepository) {
        this.registroRepository = registroRepository;
        this.perfilLookupRepository = perfilLookupRepository;
    }

    @Transactional
    public RegistroBiometricoResponseDTO registrar(Integer idUsuario, RegistroBiometricoRequestDTO request) {
        List<Integer> idsPerfil = perfilLookupRepository.findIdsPerfilByIdUsuario(idUsuario);
        if (idsPerfil.isEmpty()) {
            throw new RecursoNoEncontradoException("El usuario no tiene un perfil asociado");
        }

        LocalDate hoy = LocalDate.now(ZONA_CHILE);
        LocalDate fecha = request.getFechaMedicion() != null ? request.getFechaMedicion() : hoy;
        if (fecha.isAfter(hoy)) {
            throw new IllegalArgumentException("La fecha de medición no puede ser futura");
        }

        RegistroBiometrico registro = new RegistroBiometrico();
        registro.setIdPerfil(idsPerfil.get(0));
        registro.setFechaMedicion(fecha);
        registro.setAltura(request.getAltura());
        registro.setPeso(request.getPeso());
        registro.setIndiceMasaCorporal(ImcCalculator.calcular(request.getAltura(), request.getPeso()));

        return toResponse(registroRepository.save(registro));
    }
    
    @Transactional(readOnly = true)
    public List<RegistroBiometricoResponseDTO> listarPropios(Integer idUsuario) {
        List<Integer> idsPerfil = perfilLookupRepository.findIdsPerfilByIdUsuario(idUsuario);
        if (idsPerfil.isEmpty()) {
            return List.of();
        }
        return registroRepository.findByIdPerfilInOrderByFechaMedicionDescIdBiometricoDesc(idsPerfil)
                .stream().map(this::toResponse).toList();
    }

    /** Historial de un perfil cualquiera (el acceso se restringe a MONITOR/ADMIN en SecurityConfig). */
    @Transactional(readOnly = true)
    public List<RegistroBiometricoResponseDTO> listarPorPerfil(Integer idPerfil) {
        return registroRepository.findByIdPerfilInOrderByFechaMedicionDescIdBiometricoDesc(List.of(idPerfil))
                .stream().map(this::toResponse).toList();
    }

    /** Elimina una medición: solo su dueño o un ADMIN. */
    @Transactional
    public void eliminar(Integer idUsuario, boolean esAdmin, Integer idBiometrico) {
        RegistroBiometrico registro = registroRepository.findById(idBiometrico)
                .orElseThrow(() -> new RecursoNoEncontradoException("Registro biométrico no encontrado"));

        if (!esAdmin) {
            List<Integer> idsPerfil = perfilLookupRepository.findIdsPerfilByIdUsuario(idUsuario);
            if (!idsPerfil.contains(registro.getIdPerfil())) {
                throw new AccesoDenegadoException("No tienes permiso para eliminar este registro");
            }
        }
        registroRepository.delete(registro);
    }

    private RegistroBiometricoResponseDTO toResponse(RegistroBiometrico r) {
        return RegistroBiometricoResponseDTO.builder()
                .id(r.getIdBiometrico())
                .idPerfil(r.getIdPerfil())
                .fechaMedicion(r.getFechaMedicion())
                .altura(r.getAltura())
                .peso(r.getPeso())
                .indiceMasaCorporal(r.getIndiceMasaCorporal())
                .build();
    }
}
