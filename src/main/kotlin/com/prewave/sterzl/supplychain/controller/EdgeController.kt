package com.prewave.sterzl.supplychain.controller

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.prewave.sterzl.supplychain.model.EdgeDTO
import com.prewave.sterzl.supplychain.service.EdgeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody
import kotlin.streams.asSequence

@RestController
@RequestMapping("/")
class EdgeController {

    @Autowired
    lateinit var edgeService: EdgeService

    @PostMapping
    fun createEdge(@RequestBody edge: EdgeDTO): EdgeDTO {
        val affectedRows = edgeService.createEdge(edge)
        if (affectedRows == 0) {
            throw EdgeExistsExceptions(edge)
        }
        return edge
    }

    @DeleteMapping
    fun deleteEdge(@RequestBody edge: EdgeDTO) {
        val affectedRows = edgeService.deleteEdge(edge)
        if (affectedRows == 0) {
            throw EdgeNotFoundException(edge)
        }
    }

    @GetMapping
    fun getTree(@RequestParam("from") from: Int): StreamingResponseBody {
        if (edgeService.existsNode(from)) {
            return StreamingResponseBody { outputStream ->
                JsonFactory().createGenerator(outputStream).use {
                    it.codec = ObjectMapper();
                    it.writeStartArray()

                    edgeService.getTree(from).asSequence().withIndex().forEach { (idx, edge) ->
                        it.writeObject(edge)
                        if (idx % 100 == 0) {
                            it.flush()
                        }
                    }

                    it.writeEndArray()
                }
            }
        }
        throw NodeNotFoundException(from)
    }
}