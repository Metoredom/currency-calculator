package org.example.bilderlings.validators

import org.example.bilderlings.dto.ConversionDto
import org.example.bilderlings.validators.CurrencyValidator.isValid

object ConversionDtoValidator {
    fun validate(dto: ConversionDto){
        if (!isValid(dto.fromCurrency) || !isValid(dto.toCurrency))
            throw IllegalArgumentException("Invalid currency pair")
        if(dto.amount < 0)
            throw IllegalArgumentException("Amount must be greater than zero")
    }
}