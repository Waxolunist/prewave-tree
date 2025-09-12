package com.prewave.sterzl.supplychain.controller

import com.prewave.sterzl.supplychain.model.EdgeDTO
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestControllerAdvice


@RestControllerAdvice
class GlobalExceptionHandler {

    data class EdgeErrorResponse(
        val edge: EdgeDTO,
        val cause: String
    )

    data class NodeErrorResponse(
        val node: Int,
        val cause: String
    )

    @ResponseStatus(HttpStatus.CONFLICT)
    @ExceptionHandler(EdgeExistsExceptions::class)
    fun handleException(ex: EdgeExistsExceptions): EdgeErrorResponse =
        EdgeErrorResponse(ex.edge, ex.message ?: "Unknown error")

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(EdgeNotFoundException::class)
    fun handleException(ex: EdgeNotFoundException): EdgeErrorResponse =
        EdgeErrorResponse(ex.edge, ex.message ?: "Unknown error")

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NodeNotFoundException::class)
    fun handleException(ex: NodeNotFoundException): NodeErrorResponse =
        NodeErrorResponse(ex.node, ex.message ?: "Unknown error")
}