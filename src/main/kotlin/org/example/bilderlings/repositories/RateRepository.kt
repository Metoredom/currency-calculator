package org.example.bilderlings.repositories

import org.example.bilderlings.entities.Rate
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface RateRepository : JpaRepository<Rate, Long> {
    fun findByCode(code: String): Optional<Rate>
    fun existsByCode(code: String): Boolean
}
