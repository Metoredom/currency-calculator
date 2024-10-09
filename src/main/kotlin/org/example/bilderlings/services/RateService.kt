package org.example.bilderlings.services

import jakarta.annotation.PostConstruct

interface RateService {
    fun refreshRates()
    fun getRate(fromCurrency: String, toCurrency: String): Double
}
