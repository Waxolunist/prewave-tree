package com.prewave.sterzl.supplychain.controller

import com.prewave.sterzl.supplychain.model.EdgeDTO

class EdgeExistsException(val edge: EdgeDTO) : Exception("Edge already exists")