package com.municipalidad.talleresph.service_catalogo_sucursales.service;

import com.municipalidad.talleresph.service_catalogo_sucursales.dto.TallerResponseDTO;
import com.municipalidad.talleresph.service_catalogo_sucursales.model.Taller;
import com.municipalidad.talleresph.service_catalogo_sucursales.repository.TallerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TallerService 
{

    private final TallerRepository tallerRepository;

    @Transactional(readOnly = true)
    public List<TallerResponseDTO> obtenerTodosLosTalleres() 
    {
        return tallerRepository.findByActivoTrueOrderByIdAsc()
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<TallerResponseDTO> obtenerTalleresPorMonitor(Long monitorId) 
    {
        return tallerRepository.findByMonitorIdAndActivoTrueOrderByIdAsc(monitorId)
                .stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
    }

    private TallerResponseDTO mapToDTO(Taller taller) 
    {
        String nombreProfesor = "Sin monitor asignado";
        Long idMonitor = null;

        if (taller.getMonitor() != null) 
        {
            nombreProfesor = taller.getMonitor().getNombre() + " " + taller.getMonitor().getApellido();
            idMonitor = taller.getMonitor().getId();
        }

        return TallerResponseDTO.builder()
                .id(taller.getId())
                .nombre(taller.getNombre())
                .categoria(taller.getCategoria())
                .descripcion(taller.getDescripcion())
                .imagen(taller.getImagen())
                .lat(taller.getLat())
                .lng(taller.getLng())
                .lugar(taller.getLugar())
                .dias(taller.getDias())
                .horario(taller.getHorario())
                .horarioLargo(taller.getHorarioLargo())
                .cuposDisponibles(taller.getCuposDisponibles())
                .profesor(nombreProfesor)
                .monitorId(idMonitor)
                .build();
    }
}
