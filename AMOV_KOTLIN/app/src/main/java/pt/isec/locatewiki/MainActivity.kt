package pt.isec.locatewiki

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import pt.isec.locatewiki.ui.data.AppDataManager
import pt.isec.locatewiki.ui.screens.NavScreen
import pt.isec.locatewiki.ui.theme.LocateWikiTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        instance = this

        super.onCreate(savedInstanceState)

        setContent {
            LocateWikiTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavScreen()
                }
            }
        }

        org.osmdroid.config.Configuration.getInstance().userAgentValue = "LocateWiki"

        AppDataManager.verifyPermissions()
    }

    override fun onResume() {
        super.onResume()
        AppDataManager.startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        AppDataManager.stopLocationUpdates()
    }

    companion object {
        lateinit var instance: MainActivity
            private set
    }
}
