package pl.propertea.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.Function

fun Transaction.similarity(column:Column<String>, to: String) = object : Function<Double>(DoubleColumnType()) {
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
