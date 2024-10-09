package org.example.bilderlings.services.impl

import jakarta.annotation.PostConstruct
import org.example.bilderlings.entities.Rate
import org.example.bilderlings.repositories.RateRepository
import org.example.bilderlings.services.RateService
import org.slf4j.LoggerFactory
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.stereotype.Component
import org.springframework.stereotype.Service
import org.w3c.dom.Document
import org.w3c.dom.Element
import java.net.URL
import javax.xml.parsers.DocumentBuilderFactory

@Service
class RateServiceImpl(
    private val rateRepository: RateRepository
) : RateService {

    companion object {
        const val ECB_URI = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml"
        private val LOGGER = LoggerFactory.getLogger(RateServiceImpl::class.java)
    }

    @CacheEvict(value = ["exchangeRates"], allEntries = true)
    override fun refreshRates() {
        LOGGER.info("Starting refresh of exchange rates")
        val dbFactory = DocumentBuilderFactory.newInstance()
        val dBuilder = dbFactory.newDocumentBuilder()
        val xmlDoc: Document = URL(ECB_URI).openStream().use { inputStream ->
            dBuilder.parse(inputStream)
        }
        xmlDoc.documentElement.normalize()

        val cubes = xmlDoc.getElementsByTagName("Cube")

        (0 until cubes.length)
            .asSequence()
            .map { cubes.item(it) }
            .filter { it is Element && it.hasAttribute("currency") && it.hasAttribute("rate") }
            .forEach { node ->
                val currency = node.attributes.getNamedItem("currency").nodeValue
                val rate = node.attributes.getNamedItem("rate").nodeValue.toDouble()

                LOGGER.debug("Processing rate for currency: $currency with value: $rate")
                val existingRate = rateRepository.findByCode(currency).orElse(Rate(0, currency, rate))
                val updatedRate = existingRate.copy(rate = rate)
                rateRepository.save(updatedRate)
            }

        if (!rateRepository.existsByCode("EUR"))
            rateRepository.save(Rate(0, "EUR", 1.0))
        LOGGER.info("Finished refresh of exchange rates")
    }

    @Cacheable(value = ["exchangeRates"], key = "#fromCurrency + ':' + #toCurrency")
    override fun getRate(fromCurrency: String, toCurrency: String): Double {
        val rate = when {
            fromCurrency == "EUR" -> getRateFromEUR(toCurrency)
            toCurrency == "EUR" -> 1 / getRateFromEUR(fromCurrency)
            else -> {
                val rateFromEURToFromCurrency = getRateFromEUR(fromCurrency)
                val rateFromEURToToCurrency = getRateFromEUR(toCurrency)
                rateFromEURToToCurrency / rateFromEURToFromCurrency
            }
        }
        LOGGER.debug("Fetching rate for pair $fromCurrency-$toCurrency, result: $rate")
        return rate
    }

    private fun getRateFromEUR(currency: String): Double {
        return rateRepository.findByCode(currency)
            .orElseThrow { IllegalArgumentException("Unsupported currency: $currency") }.rate
    }
}

@Component
class RateInitializer(
    private val rateService: RateService
) {

    companion object {
        private val LOGGER = LoggerFactory.getLogger(RateInitializer::class.java)
    }

    @PostConstruct
    fun init() {
        LOGGER.info("Initializing rates on application startup")
        rateService.refreshRates()
    }
}
