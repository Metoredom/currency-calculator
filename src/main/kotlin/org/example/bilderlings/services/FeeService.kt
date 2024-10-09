package org.example.bilderlings.services

import org.example.bilderlings.dto.FeeDeleteDto
import org.example.bilderlings.dto.FeeDto
import org.example.bilderlings.entities.Fee
import java.util.Optional

interface FeeService {
    fun getFees(): List<Fee>
    fun getFeeFraction(fromCurrency: String, toCurrency: String): Double?
    fun addFee(dto: FeeDto): Fee
    fun editFee(dto: FeeDto): Fee
    fun deleteFee(dto: FeeDeleteDto)
}
