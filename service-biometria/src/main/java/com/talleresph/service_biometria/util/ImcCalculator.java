package com.talleresph.service_biometria.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class ImcCalculator {

    private static final BigDecimal CIEN = BigDecimal.valueOf(100);

    private ImcCalculator() {
    }

    public static BigDecimal calcular(BigDecimal alturaCm, BigDecimal pesoKg) {
        if (alturaCm == null || pesoKg == null || alturaCm.signum() <= 0 || pesoKg.signum() <= 0) {
            throw new IllegalArgumentException("Altura y peso deben ser mayores a cero");
        }
        BigDecimal alturaM = alturaCm.divide(CIEN, 6, RoundingMode.HALF_UP);
        BigDecimal alturaCuadrada = alturaM.multiply(alturaM);
        return pesoKg.divide(alturaCuadrada, 1, RoundingMode.HALF_UP);
    }
}
