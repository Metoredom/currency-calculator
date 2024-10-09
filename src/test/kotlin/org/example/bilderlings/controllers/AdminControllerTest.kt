package org.example.bilderlings.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.bilderlings.AbstractIntegrationTest
import org.example.bilderlings.dto.FeeDeleteDto
import org.example.bilderlings.dto.FeeDto
import org.example.bilderlings.entities.Fee
import org.example.bilderlings.services.FeeService
import org.hamcrest.Matchers.hasSize
import org.junit.jupiter.api.Test
import org.mockito.Mockito.doNothing
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@SpringBootTest
@AutoConfigureMockMvc
class AdminControllerTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockBean
    private lateinit var feeService: FeeService

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @Test
    fun `test listFees`() {
        val feeList = listOf(
            Fee(1L, "EUR", "USD", 0.1),
            Fee(2L, "USD", "PLN", 0.2)
        )
        `when`(feeService.getFees()).thenReturn(feeList)

        mockMvc.perform(get("/v1/admin/fees"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$", hasSize<Any>(2)))
    }

    @Test
    fun `test addFee`() {
        val feeDTO = FeeDto(fromCurrency = "EUR", toCurrency = "USD", feeFraction = 0.1)
        val fee = Fee(1L, "EUR", "USD", 0.1)

        `when`(feeService.addFee(feeDTO)).thenReturn(fee)

        mockMvc.perform(
            post("/v1/admin/fees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDTO))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.id").value(1))
    }

    @Test
    fun `test editFee`() {
        val feeDTO = FeeDto("EUR", "USD", 0.3)
        val updatedFee = Fee(1L, "EUR", "USD", 0.3)

        `when`(feeService.editFee(feeDTO)).thenReturn(updatedFee)

        mockMvc.perform(
            put("/v1/admin/fees")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDTO))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.feeFraction").value(0.3))
    }

    @Test
    fun `test removeFee`() {
        val feeDeleteDto = FeeDeleteDto("EUR", "USD")

        doNothing().`when`(feeService).deleteFee(feeDeleteDto)

        mockMvc.perform(
            delete("/v1/admin/fees").contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(feeDeleteDto))
        )
            .andExpect(status().isNoContent)
    }
}
