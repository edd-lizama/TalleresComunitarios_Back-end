package com.talleresph.service_biometria.util;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ImcCalculatorTest {

    @Test
    void calculaImcConUnDecimal() {
        // 70 kg y 175 cm -> 22.857... -> 22.9
        assertEquals(new BigDecimal("22.9"),
                ImcCalculator.calcular(new BigDecimal("175"), new BigDecimal("70")));
    }

    @Test
    void aceptaDecimales() {
        // 80.5 kg y 168.5 cm -> 28.35... -> 28.4
        assertEquals(new BigDecimal("28.4"),
                ImcCalculator.calcular(new BigDecimal("168.5"), new BigDecimal("80.5")));
    }

    @Test
    void rechazaValoresNoPositivos() {
        assertThrows(IllegalArgumentException.class,
                () -> ImcCalculator.calcular(BigDecimal.ZERO, new BigDecimal("70")));
        assertThrows(IllegalArgumentException.class,
                () -> ImcCalculator.calcular(new BigDecimal("170"), new BigDecimal("-1")));
    }
}
