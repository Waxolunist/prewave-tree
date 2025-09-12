package com.prewave.sterzl.supplychain.service

import com.prewave.sterzl.supplychain.generated.jooq.tables.references.EDGE
import com.prewave.sterzl.supplychain.model.EdgeDTO
import org.jooq.DSLContext
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EdgeService(private val dsl: DSLContext) {

    /**
     * @return number of affected rows
     */
    fun createEdge(edgeDTO: EdgeDTO): Int =
        dsl.insertInto(EDGE)
            .set(EDGE.FROM_ID, edgeDTO.from)
            .set(EDGE.TO_ID, edgeDTO.to)
            .onConflictDoNothing()
            .execute()

    fun deleteEdge(edge: EdgeDTO): Int {
        return dsl.deleteFrom(EDGE)
            .where(EDGE.FROM_ID.eq(edge.from))
            .and(EDGE.TO_ID.eq(edge.to))
            .execute()
    }

}