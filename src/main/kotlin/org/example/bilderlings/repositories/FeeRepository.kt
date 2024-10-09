package org.example.bilderlings.repositories

import org.example.bilderlings.entities.Fee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface FeeRepository : JpaRepository<Fee, Long> {
    fun findByFromCurrencyAndToCurrency(fromCurrency: String, toCurrency: String): Optional<Fee>
}
