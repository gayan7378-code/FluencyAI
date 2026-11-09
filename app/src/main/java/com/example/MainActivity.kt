package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Slideshow
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.example.ui.AppScreen
import com.example.ui.SpeakFlowViewModel
import com.example.ui.screens.DailyReadingScreen
import com.example.ui.screens.DialogueScreen
import com.example.ui.screens.FreeSpeechScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.NativeVoiceCallScreen
import com.example.ui.screens.PhrasebookScreen
import com.example.ui.screens.PresentationScreen
import com.example.ui.screens.PronunciationScreen
import com.example.ui.screens.RoleplayChatScreen
import com.example.ui.screens.TongueTwisterScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.BookReadingViewModel
import com.example.ui.viewmodel.NativeVoiceCallViewModel
import com.example.ui.viewmodel.PresentationViewModel
import com.example.ui.viewmodel.RoleplayChatViewModel
import com.example.ui.viewmodel.TongueTwisterViewModel

class MainActivity : ComponentActivity() {

    private val mainViewModel: SpeakFlowViewModel by viewModels()
    private val roleplayViewModel: RoleplayChatViewModel by viewModels()
    private val voiceCallViewModel: NativeVoiceCallViewModel by viewModels()
    private val twisterViewModel: TongueTwisterViewModel by viewModels()
    private val presentationViewModel: PresentationViewModel by viewModels()
    private val readingViewModel: BookReadingViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainContent(
                    mainViewModel = mainViewModel,
                    roleplayViewModel = roleplayViewModel,
                    voiceCallViewModel = voiceCallViewModel,
                    twisterViewModel = twisterViewModel,
                    presentationViewModel = presentationViewModel,
                    readingViewModel = readingViewModel
                )
            }
        }
    }
}

@Composable
fun MainContent(
    mainViewModel: SpeakFlowViewModel,
    roleplayViewModel: RoleplayChatViewModel,
    voiceCallViewModel: NativeVoiceCallViewModel,
    twisterViewModel: TongueTwisterViewModel,
    presentationViewModel: PresentationViewModel,
    readingViewModel: BookReadingViewModel
) {
    val context = LocalContext.current
    val currentScreen by mainViewModel.currentScreen.collectAsState()

    var hasAudioPermission by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasAudioPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasAudioPermission) {
            permissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    // Support back button navigation back to HOME
    if (currentScreen != AppScreen.HOME) {
        BackHandler {
            mainViewModel.navigateTo(AppScreen.HOME)
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                modifier = Modifier
                    .windowInsetsPadding(WindowInsets.navigationBars)
                    .testTag("main_bottom_nav")
            ) {
                NavigationBarItem(
                    selected = currentScreen == AppScreen.HOME,
                    onClick = { mainViewModel.navigateTo(AppScreen.HOME) },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_home")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.ROLEPLAY_CHAT,
                    onClick = { mainViewModel.navigateTo(AppScreen.ROLEPLAY_CHAT) },
                    icon = { Icon(Icons.Default.Chat, contentDescription = "Roleplay") },
                    label = { Text("Roleplay", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_roleplay")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.NATIVE_CALL,
                    onClick = { mainViewModel.navigateTo(AppScreen.NATIVE_CALL) },
                    icon = { Icon(Icons.Default.Call, contentDescription = "Call") },
                    label = { Text("Call", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_call")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.TONGUE_TWISTERS,
                    onClick = { mainViewModel.navigateTo(AppScreen.TONGUE_TWISTERS) },
                    icon = { Icon(Icons.Default.Bolt, contentDescription = "Twisters") },
                    label = { Text("Twisters", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_twisters")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.PRESENTATION,
                    onClick = { mainViewModel.navigateTo(AppScreen.PRESENTATION) },
                    icon = { Icon(Icons.Default.Slideshow, contentDescription = "Present") },
                    label = { Text("Present", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_present")
                )

                NavigationBarItem(
                    selected = currentScreen == AppScreen.BOOK_READING,
                    onClick = { mainViewModel.navigateTo(AppScreen.BOOK_READING) },
                    icon = { Icon(Icons.Default.Book, contentDescription = "Read") },
                    label = { Text("Read", fontSize = 11.sp) },
                    modifier = Modifier.testTag("nav_item_read")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when (currentScreen) {
                AppScreen.HOME -> HomeScreen(viewModel = mainViewModel)
                AppScreen.ROLEPLAY_CHAT -> RoleplayChatScreen(viewModel = roleplayViewModel)
                AppScreen.NATIVE_CALL -> NativeVoiceCallScreen(viewModel = voiceCallViewModel)
                AppScreen.TONGUE_TWISTERS -> TongueTwisterScreen(viewModel = twisterViewModel)
                AppScreen.PRESENTATION -> PresentationScreen(viewModel = presentationViewModel)
                AppScreen.BOOK_READING -> DailyReadingScreen(viewModel = readingViewModel)
                AppScreen.DIALOGUES -> DialogueScreen(viewModel = mainViewModel)
                AppScreen.PRONUNCIATION -> PronunciationScreen(viewModel = mainViewModel)
                AppScreen.PHRASEBOOK -> PhrasebookScreen(viewModel = mainViewModel)
                AppScreen.FREE_SPEECH -> FreeSpeechScreen(viewModel = mainViewModel)
            }
        }
    }
}
