package org.example.bilderlings.validators

import java.util.*

object CurrencyValidator {
    fun isValid(currencyCode: String): Boolean {
        return Currency.getAvailableCurrencies().any { it.currencyCode == currencyCode }
    }
}
