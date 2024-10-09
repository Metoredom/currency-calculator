package org.example.bilderlings.controllers

import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.example.bilderlings.dto.FeeDeleteDto
import org.example.bilderlings.dto.FeeDto
import org.example.bilderlings.entities.Fee
import org.example.bilderlings.services.FeeService
import org.example.bilderlings.validators.FeeDtoValidator.validate
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI

@RestController
@Tag(name = "Admin Controller", description = "Controller for admin endpoints")
@RequestMapping(value = ["/v1/admin"])
class AdminController(
    private val feeService: FeeService
) {

    @GetMapping("/fees")
    @Operation(description = "Get all fees")
    fun getFees(): ResponseEntity<List<Fee>> {
        val fees = feeService.getFees()
        return ResponseEntity.ok(fees)
    }

    @PostMapping("/fees")
    @Operation(description = "Add a new fee")
    fun addFee(@Valid @RequestBody dto: FeeDto): ResponseEntity<Fee> {
        validate(dto)
        val fee = feeService.addFee(dto)
        return ResponseEntity.created(URI.create("/fees/${fee.id}")).body(fee)
    }

    @PutMapping("/fees")
    @Operation(description = "Update an existing fee")
    fun editFee(@Valid @RequestBody dto: FeeDto): ResponseEntity<Fee> {
        validate(dto)
        val fee = feeService.editFee(dto)
        return ResponseEntity.ok(fee)
    }

    @DeleteMapping("/fees")
    @Operation(description = "Delete a fee")
    fun deleteFee(@Valid @RequestBody dto: FeeDeleteDto): ResponseEntity<Void> {
        validate(dto)
        feeService.deleteFee(dto)
        return ResponseEntity.noContent().build()
    }
}
