package co.edu.unicauca.dopaminah.data.db

import app.cash.sqldelight.Query
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import org.sqlite.SQLiteDataSource
import java.sql.Connection

private class SqliteDriver : JdbcDriver() {
    private val dataSource = SQLiteDataSource().apply { url = "jdbc:sqlite:dopaminah.db" }
    private val listeners = mutableMapOf<String, MutableList<Query.Listener>>()

    override fun getConnection(): Connection = dataSource.connection

    override fun closeConnection(connection: Connection) {
        connection.close()
    }

    override fun addListener(vararg queryKeys: String, listener: Query.Listener) {
        for (key in queryKeys) {
            listeners.getOrPut(key) { mutableListOf() }.add(listener)
        }
    }

    override fun removeListener(vararg queryKeys: String, listener: Query.Listener) {
        for (key in queryKeys) {
            listeners[key]?.remove(listener)
        }
    }

    override fun notifyListeners(vararg queryKeys: String) {
        for (key in queryKeys) {
            listeners[key]?.forEach { it.queryResultsChanged() }
        }
    }
}

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        return SqliteDriver()
    }
}
