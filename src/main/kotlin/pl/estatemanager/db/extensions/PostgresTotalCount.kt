package pl.estatemanager.db.extensions

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.DoubleColumnType
import org.jetbrains.exposed.sql.Expression
import org.jetbrains.exposed.sql.Function
import org.jetbrains.exposed.sql.QueryBuilder
import org.jetbrains.exposed.sql.stringLiteral

fun similarity(column:Column<String>, to: String) = object : Function<Double>(DoubleColumnType()) {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        val x = stringLiteral(to)
        append("${column.name} <-> $x as ")
    }
}

class Literal(val value: String) : Expression<String>() {
    override fun toQueryBuilder(queryBuilder: QueryBuilder) = queryBuilder {
        append(value)
    }
}
