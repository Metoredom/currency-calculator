package org.example.bilderlings.services

import org.example.bilderlings.AbstractIntegrationTest
import org.example.bilderlings.dto.ConversionDto
import org.example.bilderlings.repositories.RateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean

@SpringBootTest
@AutoConfigureMockMvc
class ConversionServiceTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var conversionService: ConversionService

    @MockBean
    private lateinit var rateService: RateService

    @MockBean
    private lateinit var feeService: FeeService

    @MockBean
    private lateinit var rateRepository: RateRepository

    @Value("\${application.fees.default_fraction}")
    private var defaultFeeFraction: Double = 0.01


    @Test
    fun `test convert with configured fee`() {
        val request = ConversionDto(100.0, "EUR", "USD")
        val fee = 0.1

        `when`(rateRepository.existsByCode("EUR")).thenReturn(true)
        `when`(rateRepository.existsByCode("USD")).thenReturn(true)
        `when`(feeService.getFeeFraction("EUR", "USD")).thenReturn(fee)
        `when`(rateService.getRate("EUR", "USD")).thenReturn(1.10)

        val response = conversionService.convert(request)
        val expectedAmount = (100.0 - 100.0 * 0.1) * 1.10
        assertEquals(expectedAmount, response.result)
    }

    @Test
    fun `test convert with default fee`() {
        val request = ConversionDto(100.0, "USD", "PLN")

        `when`(rateRepository.existsByCode("USD")).thenReturn(true)
        `when`(rateRepository.existsByCode("PLN")).thenReturn(true)
        `when`(feeService.getFeeFraction("USD", "PLN")).thenReturn(null)
        `when`(rateService.getRate("USD", "PLN")).thenReturn(4.09)

        val response = conversionService.convert(request)
        val expectedAmount = (100.0 - 100.0 * defaultFeeFraction) * 4.09
        assertEquals(expectedAmount, response.result)
    }

    @Test
    fun `test convert with invalid currency`() {
        val request = ConversionDto(100.0, "USD", "XXX")

        `when`(rateRepository.existsByCode("USD")).thenReturn(true)
        `when`(rateRepository.existsByCode("XXX")).thenReturn(false)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            conversionService.convert(request)
        }
        assertEquals("Unsupported currency pair: USD-XXX", exception.message)
    }
}
