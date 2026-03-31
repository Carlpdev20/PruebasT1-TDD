package edu.pe.cibertec.infracciones;

import edu.pe.cibertec.infracciones.model.EstadoMulta;
import edu.pe.cibertec.infracciones.model.Multa;
import edu.pe.cibertec.infracciones.model.Pago;
import edu.pe.cibertec.infracciones.repository.MultaRepository;
import edu.pe.cibertec.infracciones.repository.PagoRepository;
import edu.pe.cibertec.infracciones.service.impl.PagoServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class PagoServiceTest {
    @Mock
    private MultaRepository multaRepository;

    @Mock
    private PagoRepository pagoRepository;

    @InjectMocks
    private PagoServiceImpl pagoService;

    @Test
    void testProcesarPagoConDescuento() {
        Long id = 1L;
        Multa m = new Multa();
        m.setId(id);
        m.setMonto(500.0);
        m.setFechaEmision(LocalDate.now());
        m.setFechaVencimiento(LocalDate.now().plusDays(10));
        m.setEstado(EstadoMulta.PENDIENTE);

        when(multaRepository.findById(id)).thenReturn(Optional.of(m));

        pagoService.procesarPago(id);

        assertEquals(EstadoMulta.PAGADA, m.getEstado());
        verify(multaRepository, times(1)).save(m);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository, times(1)).save(captor.capture());

        Pago pagoGuardado = captor.getValue();
        assertEquals(400.0, pagoGuardado.getMontoPagado());
        assertEquals(100.0, pagoGuardado.getDescuentoAplicado());
        assertEquals(0.0, pagoGuardado.getRecargo());
    }

    @Test
    void testProcesarPagoConRecargoAvanzado() {
        Long id = 2L;
        Multa m = new Multa();
        m.setId(id);
        m.setMonto(500.0);
        m.setFechaEmision(LocalDate.now().minusDays(12));
        m.setFechaVencimiento(LocalDate.now().minusDays(2));
        m.setEstado(EstadoMulta.PENDIENTE);

        Pago pagoSimulado = new Pago();
        when(multaRepository.findById(id)).thenReturn(Optional.of(m));
        when(pagoRepository.save(any(Pago.class))).thenReturn(pagoSimulado);

        pagoService.procesarPago(id);

        ArgumentCaptor<Pago> captor = ArgumentCaptor.forClass(Pago.class);
        verify(pagoRepository, times(1)).save(captor.capture());

        Pago pagoCapturado = captor.getValue();
        assertEquals(75.00, pagoCapturado.getRecargo());
        assertEquals(0.00, pagoCapturado.getDescuentoAplicado());
        assertEquals(575.00, pagoCapturado.getMontoPagado());
    }
}
