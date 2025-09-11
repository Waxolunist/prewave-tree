package com.prewave.sterzl.supplychain.controller

import com.prewave.sterzl.supplychain.service.EdgeService
import com.prewave.sterzl.supplychain.model.EdgeDTO
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/")
class EdgeController {

    @Autowired
    lateinit var edgeService: EdgeService

    @PostMapping()
    fun createEdge(@RequestBody edge: EdgeDTO): EdgeDTO {
        val affectedRows = edgeService.createEdge(edge)
        if (affectedRows == 0) {
            throw EdgeExistsException(edge)
        }
        return edge
    }
}