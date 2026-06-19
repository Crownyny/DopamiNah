package co.edu.unicauca.dopaminah.data.db

import app.cash.sqldelight.db.SqlDriver

actual class DatabaseDriverFactory {
    actual fun createDriver(): SqlDriver {
        error("SQLDelight is not available on WasmJs. Use DevicePreferences instead.")
    }
}
