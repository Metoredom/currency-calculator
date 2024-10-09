package org.example.bilderlings.dto

import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

data class ConversionDto(
    @Min(0)
    val amount: Double,

    @NotBlank
    val fromCurrency: String,

    @NotBlank
    val toCurrency: String
)
