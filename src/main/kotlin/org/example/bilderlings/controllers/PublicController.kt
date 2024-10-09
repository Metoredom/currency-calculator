package org.example.bilderlings.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.bilderlings.dto.ConversionDto
import org.example.bilderlings.dto.ConversionResultDto
import org.example.bilderlings.services.ConversionService
import org.example.bilderlings.services.RateService
import org.example.bilderlings.validators.ConversionDtoValidator.validate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Tag(name = "Public", description = "Currency conversion and rates refresh operations")
@RequestMapping("/v1/")
class PublicController(
    private val conversionService: ConversionService,
    private val rateService: RateService,
) {

    @PostMapping("/convert")
    @Operation(summary = "Calculate currency conversion")
    fun convertCurrency(@Valid @RequestBody conversionDto: ConversionDto): ResponseEntity<ConversionResultDto> {
        validate(conversionDto)
        val result = conversionService.convert(conversionDto)
        return ResponseEntity.ok(result)
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh exchange rates")
    fun refreshRates(): ResponseEntity<Void> {
        rateService.refreshRates()
        return ResponseEntity.noContent().build()
    }
}
