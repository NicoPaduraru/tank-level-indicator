package de.tanklevelindicator

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import de.tanklevelindicator.ui.TankApp
import de.tanklevelindicator.ui.TankLevelTheme

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TankLevelTheme {
                TankApp()
            }
        }
    }
}
