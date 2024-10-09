package org.example.bilderlings.dto

import jakarta.validation.constraints.NotBlank

data class FeeDeleteDto(
    @NotBlank
    val fromCurrency: String,
    @NotBlank
    val toCurrency: String,
)
