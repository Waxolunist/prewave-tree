package com.prewave.sterzl.supplychain.controller

import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.ObjectMapper
import com.prewave.sterzl.supplychain.MockitoHelper.anyObject
import com.prewave.sterzl.supplychain.model.BranchDTO
import com.prewave.sterzl.supplychain.model.EdgeDTO
import com.prewave.sterzl.supplychain.service.EdgeService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyInt
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.never
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.Spy
import org.mockito.junit.jupiter.MockitoExtension
import java.io.OutputStream
import kotlin.test.assertContentEquals
import kotlin.test.fail

@ExtendWith(MockitoExtension::class)
class EdgeControllerTest {
    @Spy
    var objectMapper = ObjectMapper()

    @Spy
    var jsonFactory = JsonFactory()

    @Mock
    lateinit var outputStream: OutputStream

    @Mock
    lateinit var edgeService: EdgeService

    @InjectMocks
    lateinit var edgeController: EdgeController

    @BeforeEach
    fun setUp() {
        Mockito.clearInvocations(edgeService)
    }

    @Test
    fun createEdge() {
        `when`(edgeService.createEdge(anyObject())).thenReturn(1)
        val edge = EdgeDTO(1, 2)
        val result = edgeController.createEdge(edge)
        verify(edgeService).createEdge(edge)
        assertEquals(edge, result)
    }

    @Test
    fun createEdgeNotExisting() {
        `when`(edgeService.createEdge(anyObject())).thenReturn(0)
        val edge = EdgeDTO(1, 2)
        try {
            edgeController.createEdge(edge)
            fail("Should have thrown an exception")
        } catch (e: EdgeExistsExceptions) {
            verify(edgeService).createEdge(edge)
            assertEquals(edge, e.edge)
        }
    }

    @Test
    fun deleteEdge() {
        `when`(edgeService.deleteEdge(anyObject())).thenReturn(1)
        val edge = EdgeDTO(1, 2)
        edgeController.deleteEdge(edge)
        verify(edgeService).deleteEdge(edge)
    }

    @Test
    fun deleteEdgeNotExisting() {
        `when`(edgeService.deleteEdge(anyObject())).thenReturn(0)
        val edge = EdgeDTO(1, 2)
        try {
            edgeController.deleteEdge(edge)
            fail("Should have thrown an exception")
        } catch (e: EdgeNotFoundException) {
            verify(edgeService).deleteEdge(edge)
            assertEquals(edge, e.edge)
        }
    }

    @Test
    fun getTree() {
        val edgeList = listOf(BranchDTO(1, arrayOf(2)), BranchDTO(2, arrayOf(3)))
        `when`(edgeService.getTree(anyInt())).thenReturn(edgeList.stream())
        `when`(edgeService.existsNode(anyInt())).thenReturn(true)
        val result = edgeController.getTree(1)
        verify(edgeService).getTree(1)
        assertContentEquals(result, edgeList)
    }

    @Test
    fun getTreeNotExisting() {
        `when`(edgeService.existsNode(anyInt())).thenReturn(false)
        try {
            edgeController.getTree(1)
            fail("Should have thrown an exception")
        } catch (e: NodeNotFoundException) {
            verify(edgeService, never()).getTree(1)
            assertEquals(1, e.node)
        }
    }

    @Test
    fun getTreeStream() {
        val edgeList = listOf(BranchDTO(1, arrayOf(2)), BranchDTO(2, arrayOf(3)))
        `when`(edgeService.getTree(anyInt())).thenReturn(edgeList.stream())
        `when`(edgeService.existsNode(anyInt())).thenReturn(true)
        val result = edgeController.getTreeStream(1)
        // write to mocked stream
        result.body?.writeTo(outputStream)
        verify(edgeService).getTree(1)
    }

    @Test
    fun getTreeStreamNotExisting() {
        `when`(edgeService.existsNode(anyInt())).thenReturn(false)
        try {
            edgeController.getTreeStream(1)
            fail("Should have thrown an exception")
        } catch (e: NodeNotFoundException) {
            verify(edgeService, never()).getTree(1)
            assertEquals(1, e.node)
        }
    }
}
