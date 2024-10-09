package org.example.bilderlings.services

import org.example.bilderlings.AbstractIntegrationTest
import org.example.bilderlings.entities.Rate
import org.example.bilderlings.repositories.RateRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import java.util.*


@SpringBootTest
@AutoConfigureMockMvc
class RateServiceTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var exchangeRateService: RateService

    @MockBean
    private lateinit var exchangeRateRepository: RateRepository

    @BeforeEach
    fun setup() {
        val eurToEur = Rate(1, "EUR", 1.0)
        val eurToUsd = Rate(2, "USD", 1.10)
        val eurToPln = Rate(3, "PLN", 4.50)
        `when`(exchangeRateRepository.findByCode("EUR")).thenReturn(Optional.of(eurToEur))
        `when`(exchangeRateRepository.findByCode("USD")).thenReturn(Optional.of(eurToUsd))
        `when`(exchangeRateRepository.findByCode("PLN")).thenReturn(Optional.of(eurToPln))
        `when`(exchangeRateRepository.findByCode("JPY")).thenReturn(Optional.empty())
    }

    @Test
    fun `test getRate EUR to USD`() {
        val rate = exchangeRateService.getRate("EUR", "USD")
        assertEquals(1.10, rate)
    }

    @Test
    fun `test getRate USD to EUR`() {
        val rate = exchangeRateService.getRate("USD", "EUR")
        assertEquals(1 / 1.10, rate)
    }

    @Test
    fun `test getRate USD to PLN`() {
        val rate = exchangeRateService.getRate("USD", "PLN")
        val expectedRate = 4.50 / 1.10
        assertEquals(expectedRate, rate)
    }

    @Test
    fun `test getRate with unsupported currency`() {
        val exception = assertThrows(IllegalArgumentException::class.java) {
            exchangeRateService.getRate("EUR", "JPY")
        }
        assertEquals("Unsupported currency: JPY", exception.message)
    }
}
