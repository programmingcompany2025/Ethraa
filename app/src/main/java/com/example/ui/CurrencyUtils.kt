package com.example.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale

object CurrencyUtils {
    /**
     * Formats financial amounts into the official Libyan Dinar format (e.g., 1,250 د.ل or 150,750.500 د.ل).
     */
    fun formatLibyanDinar(amount: Double): String {
        val symbols = DecimalFormatSymbols(Locale.US)
        val hasFraction = amount % 1.0 != 0.0
        val pattern = if (hasFraction) "#,##0.000" else "#,##0"
        val formatter = DecimalFormat(pattern, symbols)
        val formatted = formatter.format(amount)
        return "$formatted د.ل"
    }
}
