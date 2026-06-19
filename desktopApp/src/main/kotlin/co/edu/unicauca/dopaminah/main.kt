package co.edu.unicauca.dopaminah

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import co.edu.unicauca.dopaminah.data.db.DatabaseDriverFactory
import co.edu.unicauca.dopaminah.data.db.DopamiNahDb
import co.edu.unicauca.dopaminah.data.repository.WebGoalsRepositoryImpl

fun main() = application {
    val db = DopamiNahDb(DatabaseDriverFactory().createDriver())
    val webGoalsRepo = WebGoalsRepositoryImpl(db)
    Window(
        onCloseRequest = ::exitApplication,
        title = "DopamiNah",
    ) {
        App(webGoalsRepository = webGoalsRepo)
    }
}
