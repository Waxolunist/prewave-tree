package com.prewave.sterzl.supplychain.controller

import com.prewave.sterzl.supplychain.model.EdgeDTO

abstract class EdgeException(val edge: EdgeDTO, message: String) : Exception(message)

class EdgeExistsExceptions(edge: EdgeDTO) : EdgeException(edge, "Edge already exists")

class EdgeNotFoundException(edge: EdgeDTO) : EdgeException(edge, "Edge not found")