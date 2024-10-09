package org.example.bilderlings.entities

import jakarta.persistence.*
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

@Entity
@Table(
    name = "fees",
    uniqueConstraints = [UniqueConstraint(columnNames = ["from_currency", "to_currency"])],
    indexes = [Index(columnList = "from_currency, to_currency")]
)
data class Fee(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,

    @Column(name = "from_currency")
    @NotBlank
    val fromCurrency: String,

    @Column(name = "to_currency")
    @NotBlank
    val toCurrency: String,

    @Min(0)
    @Max(1)
    val feeFraction: Double
)
