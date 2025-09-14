package com.prewave.sterzl.supplychain

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@Import(TestcontainersConfiguration::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PrewaveSupplyChainApplicationTests {
    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
    }

    @Test
    fun createAndDeleteEdgeTwice() {
        createEdge(1, 2).andExpect(status().isOk)
        createEdge(1, 2).andExpect(status().isConflict)
        this.mockMvc
            .perform(
                delete("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"from\":1,\"to\":2}"),
            ).andExpect(status().isOk)
        this.mockMvc
            .perform(
                delete("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"from\":1,\"to\":2}"),
            ).andExpect(status().isNotFound)
    }

    @Test
    fun getTreeWithCycles() {
        createEdge(10, 11).andExpect(status().isOk)
        createEdge(11, 12).andExpect(status().isOk)
        createEdge(12, 10).andExpect(status().isOk)
        val result =
            this.mockMvc
                .perform(
                    get("/")
                        .contentType(MediaType.APPLICATION_JSON)
                        .queryParam("from", "10"),
                ).andExpect(status().isOk)
                .andReturn()
        val content = result.response.contentAsString
        assertEquals("[{\"from\":11,\"to\":[12]},{\"from\":10,\"to\":[11]},{\"from\":12,\"to\":[10]}]", content)
    }

    private fun createEdge(
        from: Int,
        to: Int,
    ): ResultActions =
        this.mockMvc
            .perform(
                post("/")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"from\":$from,\"to\":$to}"),
            )
}
