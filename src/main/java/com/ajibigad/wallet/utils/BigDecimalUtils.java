package com.ajibigad.wallet.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by ajibigad on 09/10/2023 6:50 AM
 */

public final class BigDecimalUtils {

    public static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_EVEN;

    private BigDecimalUtils() {
    }

    public static BigDecimal multiply(double a, BigDecimal b) {
        return multiply(a, b, true);
    }

    public static BigDecimal multiply(double a, BigDecimal b, boolean round) {
        var result = BigDecimal.valueOf(a).multiply(b);

        if (round) {
            return result.setScale(2, ROUNDING_MODE);
        }

        return result;
    }
}
