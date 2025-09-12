package com.prewave.sterzl.supplychain.controller

import com.prewave.sterzl.supplychain.model.EdgeDTO
import com.prewave.sterzl.supplychain.service.EdgeService
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.*

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
    fun getTree(@RequestParam("from") from: Int): List<EdgeDTO?> {
        if (edgeService.existsNode(from)) {
            return edgeService.getTree(from)
        }
        throw NodeNotFoundException(from)
    }
}