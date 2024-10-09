package org.example.bilderlings.entities

import jakarta.persistence.*
import jakarta.validation.constraints.NotNull

@Entity
@Table(name = "rates", indexes = [Index(columnList = "code", unique = true)])
data class Rate(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long,
    @NotNull
    val code: String,
    @NotNull
    val rate: Double
)
