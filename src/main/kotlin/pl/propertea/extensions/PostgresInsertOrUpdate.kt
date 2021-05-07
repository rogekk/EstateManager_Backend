package pl.propertea.extensions

import org.jetbrains.exposed.sql.Column
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.Transaction
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager

//TODO: replace with more robust implementation https://github.com/JetBrains/Exposed/issues/167#issuecomment-514558435

fun <T : Table> T.insertOrUpdate(vararg keys: Column<*>, isIgnore: Boolean = false, body: T.(InsertStatement<Number>) -> Unit) =
        InsertOrUpdate<Number>(keys, this, isIgnore).apply {
            body(this)
            execute(TransactionManager.current())
        }

class InsertOrUpdate<Key : Any>(private val keys: Array<out Column<*>>,
                                table: Table,
                                isIgnore: Boolean = false
) : InsertStatement<Key>(table, isIgnore) {

    override fun prepareSQL(transaction: Transaction): String {
        val updateSetter = super.values.keys.joinToString { "${it.name} = EXCLUDED.${it.name}" }
        val keyColumns = keys.joinToString(",") { it.name }
        val onConflict = "ON CONFLICT ($keyColumns) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}
