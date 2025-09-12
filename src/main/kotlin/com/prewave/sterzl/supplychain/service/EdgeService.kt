package com.prewave.sterzl.supplychain.service

import com.prewave.sterzl.supplychain.generated.jooq.tables.references.EDGE
import com.prewave.sterzl.supplychain.model.EdgeDTO
import org.jooq.DSLContext
import org.jooq.impl.DSL.*
import org.jooq.impl.SQLDataType
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

    /**
     * @return number of affected rows
     */
    fun deleteEdge(edge: EdgeDTO): Int {
        return dsl.deleteFrom(EDGE)
            .where(EDGE.FROM_ID.eq(edge.from))
            .and(EDGE.TO_ID.eq(edge.to))
            .execute()
    }

    fun existsNode(id: Int): Boolean {
        val count = dsl.select(count(EDGE.FROM_ID).add(count(EDGE.TO_ID)))
            .from(EDGE)
            .where(EDGE.FROM_ID.eq(id).or(EDGE.TO_ID.eq(id)))
            .fetchSingleInto(Int::class.javaPrimitiveType)
        return count > 0
    }

    fun getTree(from: Int): List<EdgeDTO?> {
        fun createNodeField(name: String) = field(name, SQLDataType.INTEGER.nullable(false))

        val subEdgeName = name("subedges")
        val subEdgesFromId = createNodeField("from_id")
        val subEdgesToId = createNodeField("to_id")
        val recursiveCTE = subEdgeName
            .`as`(
                select(EDGE.FROM_ID, EDGE.TO_ID)
                    .from(EDGE)
                    .where(EDGE.FROM_ID.eq(from))
                    .union(
                        select(createNodeField("e2.from_id"), createNodeField("e2.to_id"))
                            .from(EDGE.`as`("e2"))
                            .innerJoin(table(subEdgeName).`as`("s"))
                            .on(createNodeField("s.to_id").eq(createNodeField("e2.from_id")))
                    )
            )

        return dsl.withRecursive(recursiveCTE)
            .select(subEdgesFromId, subEdgesToId)
            .from(subEdgeName)
            .fetch {
                EdgeDTO(it.getValue(subEdgesFromId), it.getValue(subEdgesToId))
            }
    }

}