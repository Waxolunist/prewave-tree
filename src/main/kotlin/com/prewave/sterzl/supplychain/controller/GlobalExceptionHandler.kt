package com.prewave.sterzl.supplychain.controller

import com.prewave.sterzl.supplychain.model.EdgeDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    data class ErrorResponse(
        val edge: EdgeDTO,
        val cause: String
    )

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EdgeExistsException::class)
    fun handleException(ex: EdgeExistsException): ErrorResponse =
        ErrorResponse(ex.edge, ex.message ?: "Unknown error")
}