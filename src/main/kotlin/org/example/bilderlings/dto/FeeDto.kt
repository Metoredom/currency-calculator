package org.example.bilderlings.dto

import jakarta.validation.constraints.NotBlank
import org.example.bilderlings.entities.Fee

data class FeeDto(
    @NotBlank
    val fromCurrency: String,
    @NotBlank
    val toCurrency: String,
    @NotBlank
    val feeFraction: Double
)

fun FeeDto.toEntity(): Fee {
    return let {
        Fee(0, it.fromCurrency, it.toCurrency, it.feeFraction)
    }
}
