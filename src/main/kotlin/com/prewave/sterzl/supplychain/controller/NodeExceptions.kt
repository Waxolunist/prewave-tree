package com.prewave.sterzl.supplychain.controller

abstract class NodeException(val node: Int, message: String) : Exception(message)

class NodeNotFoundException(node: Int) : NodeException(node, "Node not found")