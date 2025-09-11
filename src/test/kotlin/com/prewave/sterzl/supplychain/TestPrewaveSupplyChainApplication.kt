package com.prewave.sterzl.supplychain

import org.springframework.boot.fromApplication
import org.springframework.boot.with


fun main(args: Array<String>) {
	fromApplication<PrewaveSupplyChainApplication>().with(TestcontainersConfiguration::class).run(*args)
}
