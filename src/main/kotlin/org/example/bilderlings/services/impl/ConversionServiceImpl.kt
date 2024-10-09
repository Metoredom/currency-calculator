package org.example.bilderlings.services.impl

import org.example.bilderlings.dto.ConversionDto
import org.example.bilderlings.dto.ConversionResultDto
import org.example.bilderlings.repositories.RateRepository
import org.example.bilderlings.services.ConversionService
import org.example.bilderlings.services.FeeService
import org.example.bilderlings.services.RateService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service

@Service
class ConversionServiceImpl(
    private val rateRepository: RateRepository,
    private val rateService: RateService,
    private val feeService: FeeService
) : ConversionService {

    companion object {
        @Value("\${application.fees.default_fraction}")
        private val DEFAULT_FEE: Double = 0.01
        private val LOGGER = LoggerFactory.getLogger(ConversionServiceImpl::class.java)
    }

    override fun convert(dto: ConversionDto): ConversionResultDto {
        if (!rateRepository.existsByCode(dto.fromCurrency) || !rateRepository.existsByCode(dto.toCurrency)) {
            LOGGER.error("Unsupported currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
            throw IllegalArgumentException("Unsupported currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
        }

        val fee = feeService.getFeeFraction(dto.fromCurrency, dto.toCurrency)
        val rate = rateService.getRate(dto.fromCurrency, dto.toCurrency)

        val feeFraction = fee ?: DEFAULT_FEE
        val amountAfterFee = dto.amount - (dto.amount * feeFraction)
        val convertedAmount = amountAfterFee * rate

        LOGGER.debug(
            "Conversion result: for pair {}-{} with original amount: {}, fee fraction: {}, amount after fee: {}, converted amount: {}",
            dto.fromCurrency, dto.toCurrency, dto.amount, feeFraction, amountAfterFee, convertedAmount
        )

        return ConversionResultDto(dto.fromCurrency, dto.toCurrency, convertedAmount)
    }
}
