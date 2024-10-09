package org.example.bilderlings.services

import org.example.bilderlings.AbstractIntegrationTest
import org.example.bilderlings.dto.FeeDto
import org.example.bilderlings.entities.Fee
import org.example.bilderlings.repositories.FeeRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*

@SpringBootTest
@AutoConfigureMockMvc
class FeeServiceTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var feeService: FeeService

    @MockBean
    private lateinit var feeRepository: FeeRepository

    @Test
    fun `test addFee`() {
        val fee = Fee(1, fromCurrency = "EUR", toCurrency = "USD", feeFraction = 0.1)
        val feeDto = FeeDto(fromCurrency = "EUR", toCurrency = "USD", feeFraction = 0.1)
        `when`(feeRepository.save(any(Fee::class.java))).thenReturn(fee)

        val result = feeService.addFee(feeDto)
        assertEquals(fee, result)
    }

    @Test
    fun `test editFee`() {
        val initialFee = Fee(1, fromCurrency = "EUR", toCurrency = "USD", feeFraction = 0.1)
        val resultFee = Fee(1, fromCurrency = "EUR", toCurrency = "USD", feeFraction = 0.3)
        val feeDto = FeeDto(fromCurrency = "EUR", toCurrency = "USD", feeFraction = 0.3)
        `when`(
            feeRepository.findByFromCurrencyAndToCurrency(
                initialFee.fromCurrency,
                initialFee.toCurrency
            )
        ).thenReturn(
            Optional.of(initialFee)
        )
        `when`(feeRepository.save(any(Fee::class.java))).thenReturn(resultFee)

        val result = feeService.editFee(feeDto)
        assertEquals(resultFee, result)
    }

    @Test
    fun `test editFee not found`() {
        val feeDto = FeeDto(fromCurrency = "USD", toCurrency = "XXX", feeFraction = 0.1)
        `when`(
            feeRepository.findByFromCurrencyAndToCurrency(
                feeDto.fromCurrency,
                feeDto.toCurrency
            )
        ).thenReturn(
            Optional.empty()
        )
        val exception = assertThrows(IllegalArgumentException::class.java) {
            feeService.editFee(feeDto)
        }
        assertEquals("Fee not found for currency pair: USD-XXX", exception.message)
    }

    @Test
    fun `test getFeeFraction`() {
        val fee = Fee(1L, "EUR", "USD", 0.1)
        `when`(feeRepository.findByFromCurrencyAndToCurrency(fee.fromCurrency, fee.toCurrency)).thenReturn(
            Optional.of(
                fee
            )
        )

        val result = feeService.getFeeFraction(fee.fromCurrency, fee.toCurrency)
        assertEquals(fee.feeFraction, result)
    }

    @Test
    fun `test getFeeFraction not found`() {
        `when`(feeRepository.findByFromCurrencyAndToCurrency("EUR", "XXX")).thenReturn(Optional.empty())

        val result = feeService.getFeeFraction("EUR", "XXX")

        assertNull(result)
    }
}
