package org.example.bilderlings.services

import org.example.bilderlings.dto.ConversionDto
import org.example.bilderlings.dto.ConversionResultDto

interface ConversionService {
    fun convert(dto: ConversionDto): ConversionResultDto
}
