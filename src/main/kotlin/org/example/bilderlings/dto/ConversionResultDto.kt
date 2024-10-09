package org.example.bilderlings.dto

import jakarta.validation.constraints.NotBlank

data class ConversionResultDto(
    @NotBlank
    val fromCurrency: String,

    @NotBlank
    val toCurrency: String,

    val result: Double
)
