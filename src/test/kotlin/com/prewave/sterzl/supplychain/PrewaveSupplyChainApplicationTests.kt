package com.prewave.sterzl.supplychain

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.context.TestPropertySource

@Import(TestcontainersConfiguration::class)
@SpringBootTest
@ActiveProfiles("test")
class PrewaveSupplyChainApplicationTests {

	@Test
	fun contextLoads() {
	}

}
