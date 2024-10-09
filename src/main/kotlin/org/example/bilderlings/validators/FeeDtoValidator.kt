package org.example.bilderlings.validators

import org.example.bilderlings.dto.FeeDeleteDto
import org.example.bilderlings.dto.FeeDto

object FeeDtoValidator {
    fun validate(dto: FeeDto) {
        if (!CurrencyValidator.isValid(dto.fromCurrency) || !CurrencyValidator.isValid(dto.toCurrency)) {
            throw IllegalArgumentException("Invalid currency code")
        }
        if (dto.feeFraction < 0 || dto.feeFraction > 1) {
            throw IllegalArgumentException("Fee fraction must be between 0 and 1")
        }
    }

    fun validate(dto: FeeDeleteDto) {
        if (!CurrencyValidator.isValid(dto.fromCurrency) || !CurrencyValidator.isValid(dto.toCurrency)) {
            throw IllegalArgumentException("Invalid currency code")
        }
    }
}
