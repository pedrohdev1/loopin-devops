package br.com.loopin.mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import br.com.loopin.mobile.data.LoopinRepository
import br.com.loopin.mobile.data.TokenHolder
import br.com.loopin.mobile.data.UserSession
import br.com.loopin.mobile.ui.LoopinMainShell
import br.com.loopin.mobile.ui.LoginAndRegisterScreen
import br.com.loopin.mobile.ui.theme.LoopinCream
import br.com.loopin.mobile.ui.theme.LoopinForest
import br.com.loopin.mobile.ui.theme.LoopinTheme
import kotlinx.coroutines.flow.collect

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LoopinTheme {
                LoopinRoot()
            }
        }
    }
}

@Composable
private fun LoopinRoot() {
    val context = LocalContext.current
    val repository = remember { LoopinRepository(context.applicationContext) }
    var session by remember { mutableStateOf<UserSession?>(null) }
    var sessionLoaded by remember { mutableStateOf(false) }

    LaunchedEffect(repository) {
        repository.sessionStore.sessions.collect { restored ->
            session = restored
            TokenHolder.accessToken = restored?.token
            sessionLoaded = true
        }
    }

    when {
        !sessionLoaded -> LoopinLoadingScreen()
        session == null -> LoginAndRegisterScreen(repository = repository)
        else -> LoopinMainShell(
            session = session!!,
            repository = repository,
            onLogout = { repository.logout() }
        )
    }
}

@Composable
private fun LoopinLoadingScreen() {
    Box(
        modifier = Modifier.fillMaxSize().background(LoopinCream),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
            CircularProgressIndicator(color = LoopinForest, modifier = Modifier.size(36.dp))
            Text("Loopin", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = LoopinForest)
            Text("Preparando seu próximo ciclo", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}
