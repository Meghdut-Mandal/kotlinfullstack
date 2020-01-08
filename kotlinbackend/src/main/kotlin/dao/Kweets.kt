package dao

import org.jetbrains.exposed.sql.*

/**
 * Represents the Kweets table using Exposed as DAO.
 */
object Kweets : Table() {
    val id = integer("id").primaryKey().autoIncrement()
    val user = varchar("user_id", 20).index()
    val date = varchar("date",40).index()
    val replyTo = integer("reply_to").index().nullable()
    val directReplyTo = integer("direct_reply_to").index().nullable()
    val text = varchar("text", 1024)
}
