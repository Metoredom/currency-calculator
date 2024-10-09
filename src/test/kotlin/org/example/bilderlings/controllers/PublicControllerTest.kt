package org.example.bilderlings.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.bilderlings.AbstractIntegrationTest
import org.example.bilderlings.dto.ConversionDto
import org.example.bilderlings.repositories.RateRepository
import org.example.bilderlings.services.FeeService
import org.example.bilderlings.services.RateService
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class PublicControllerTest: AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var rateService: RateService

    @MockBean
    private lateinit var feeService: FeeService

    @MockBean
    private lateinit var rateRepository: RateRepository

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test refreshRates`() {
        doNothing().`when`(rateService).refreshRates()

        mockMvc.perform(post("/v1/refresh"))
            .andExpect(status().isNoContent)

        verify(rateService, times(2)).refreshRates()
    }

    @Test
    fun `test convert`(){
        val amount = 100.0
        val feeFraction = 0.01
        val rate = 1.10
        val expectedResult = (amount - (amount * feeFraction)) * rate

        `when`(rateRepository.existsByCode("EUR")).thenReturn(true)
        `when`(rateRepository.existsByCode("USD")).thenReturn(true)
        `when`(feeService.getFeeFraction("EUR", "USD")).thenReturn(feeFraction)
        `when`(rateService.getRate("EUR", "USD")).thenReturn(rate)

        val request = ConversionDto(amount, "EUR", "USD")

        mockMvc.perform(
            post("/v1/convert")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.result").value(expectedResult))
    }
}
