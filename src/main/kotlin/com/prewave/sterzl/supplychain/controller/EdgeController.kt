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

    @Autowired
    lateinit var jsonFactory: JsonFactory

    @Autowired
    lateinit var objectMapper: ObjectMapper

    @PostMapping
    fun createEdge(
        @RequestBody edge: EdgeDTO,
    ): EdgeDTO {
        val affectedRows = edgeService.createEdge(edge)
        if (affectedRows == 0) {
            throw EdgeExistsExceptions(edge)
        }
        return edge
    }

    @DeleteMapping
    fun deleteEdge(
        @RequestBody edge: EdgeDTO,
    ) {
        val affectedRows = edgeService.deleteEdge(edge)
        if (affectedRows == 0) {
            throw EdgeNotFoundException(edge)
        }
    }

    @GetMapping
    fun getTree(
        @RequestParam("from") from: Int,
    ): List<EdgeDTO?> {
        if (edgeService.existsNode(from)) {
            return edgeService.getTree(from).toList()
        }
        throw NodeNotFoundException(from)
    }

    /**
     * This method is for performance measurement, to find out, if at a specific threshold
     * of edges returned, streaming is faster or nicer to the server.
     * But currently after some measurements, the standard method seems still faster.
     */
    @GetMapping("/stream")
    fun getTreeStream(
        @RequestParam("from") from: Int,
    ): StreamingResponseBody {
        if (edgeService.existsNode(from)) {
            return StreamingResponseBody { outputStream ->
                jsonFactory.createGenerator(outputStream).use {
                    it.codec = objectMapper
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
