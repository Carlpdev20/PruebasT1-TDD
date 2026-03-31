package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.service.impl.MultaServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import org.mockito.Mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

@ExtendWith(MockitoExtension.class)
public class MultaServiceTest {
    @Mock
    private MultaRepository multaRepository;

    @InjectMocks
    private MultaServiceImpl multaService;

    @Test
    void testActualizarVencida() {
        Multa m = new Multa();
        m.setId(1L);
        m.setEstado(EstadoMulta.PENDIENTE);
        m.setFechaVencimiento(LocalDate.of(2026, 1, 1));

        when(multaRepository.findByEstado(EstadoMulta.PENDIENTE))
                .thenReturn(Collections.singletonList(m));

        multaService.actualizarEstados();

        assertEquals(EstadoMulta.VENCIDA, m.getEstado());
        verify(multaRepository, times(1)).save(m);
    }
}
