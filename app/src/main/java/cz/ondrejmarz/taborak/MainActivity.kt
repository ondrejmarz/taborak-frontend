package cz.ondrejmarz.taborak

import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.google.android.gms.auth.api.identity.Identity
import cz.ondrejmarz.taborak.auth.AuthTokenManager
import cz.ondrejmarz.taborak.auth.GoogleAuthUiClient
import cz.ondrejmarz.taborak.ui.theme.AppTheme

class MainActivity : AppCompatActivity() {

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Manually handle all the insets
        WindowCompat.setDecorFitsSystemWindows(window, false)

        var startDestination = "sign_in"

        val googleAuthUiClient by lazy {
            GoogleAuthUiClient(
                context = applicationContext,
                oneTapClient = Identity.getSignInClient(applicationContext)
            )
        }

        if(googleAuthUiClient.getSignInUser() != null) {
            startDestination = "home"
        }
        else {
            startDestination = "sign_in"
        }

        setContent {
            AppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavHost(navController = rememberNavController(), applicationContext, googleAuthUiClient, startDestination)
                }
            }
        }
    }
}