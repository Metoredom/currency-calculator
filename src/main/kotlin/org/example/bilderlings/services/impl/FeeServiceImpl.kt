package org.example.bilderlings.services.impl

import org.example.bilderlings.dto.FeeDeleteDto
import org.example.bilderlings.dto.FeeDto
import org.example.bilderlings.dto.toEntity
import org.example.bilderlings.entities.Fee
import org.example.bilderlings.repositories.FeeRepository
import org.example.bilderlings.services.FeeService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Service

@Service
class FeeServiceImpl(
    private val feeRepository: FeeRepository
) : FeeService {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(FeeServiceImpl::class.java)
    }

    override fun getFees(): List<Fee> = feeRepository.findAll()

    @Cacheable(value = ["fees"], key = "#fromCurrency + ':' + #toCurrency", unless = "#result == null")
    override fun getFeeFraction(fromCurrency: String, toCurrency: String): Double? {
        LOGGER.debug("Fetching fee fraction from $fromCurrency to $toCurrency")
        return feeRepository.findByFromCurrencyAndToCurrency(fromCurrency, toCurrency).map { it.feeFraction }
            .orElse(null)
    }

    @CacheEvict(value = ["fees"], key = "#dto.fromCurrency + ':' + #dto.toCurrency")
    override fun addFee(dto: FeeDto): Fee {
        try {
            val fee = feeRepository.save(dto.toEntity())
            LOGGER.debug("Successfully added fee (${dto.feeFraction}) for currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
            return fee
        } catch (e: Exception) {
            LOGGER.error("Fee already exists for pair: ${dto.fromCurrency}-${dto.toCurrency}")
            throw IllegalArgumentException("Fee for the specified pair already exists")
        }
    }

    @CacheEvict(value = ["fees"], key = "#dto.fromCurrency + ':' + #dto.toCurrency")
    override fun editFee(dto: FeeDto): Fee {
        val existingFee = feeRepository.findByFromCurrencyAndToCurrency(dto.fromCurrency, dto.toCurrency)
            .orElseThrow {
                LOGGER.error("Fee not found for currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
                IllegalArgumentException("Fee not found for currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
            }
        val updatedFee = existingFee.copy(
            fromCurrency = dto.fromCurrency,
            toCurrency = dto.toCurrency,
            feeFraction = dto.feeFraction
        )
        val savedFee = feeRepository.save(updatedFee)
        LOGGER.debug("Successfully edited fee (${dto.feeFraction}) for currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
        return savedFee
    }

    @CacheEvict(value = ["fees"], key = "#dto.fromCurrency + ':' + #dto.toCurrency")
    override fun deleteFee(dto: FeeDeleteDto) {
        val existingFee = feeRepository.findByFromCurrencyAndToCurrency(dto.fromCurrency, dto.toCurrency)
            .orElseThrow {
                LOGGER.error("Fee not found for currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
                IllegalArgumentException("Fee not found")
            }
        feeRepository.delete(existingFee)
        LOGGER.debug("Successfully deleted fee for currency pair: ${dto.fromCurrency}-${dto.toCurrency}")
    }
}
