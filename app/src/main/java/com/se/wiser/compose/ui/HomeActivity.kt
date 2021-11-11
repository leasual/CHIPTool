package com.se.wiser.compose.ui

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.se.wiser.App
import com.se.wiser.compose.ChipControllerApp
import com.se.wiser.compose.viewmodel.MainViewModel
import com.se.wiser.data.dao.UserDao
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.coroutineContext

@ExperimentalAnimationApi
@AndroidEntryPoint
class HomeActivity: AppCompatActivity() {

    @Inject
    lateinit var userDao: UserDao

    private val mainViewModel: MainViewModel by viewModels()

    companion object{
        const val splashFadeDurationMillis = 300
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val appContainer = (application as App).container
        val splashWasDisplayed = savedInstanceState != null
//        if(!splashWasDisplayed) {
            //it should execute before setContent
            val splashScreen = installSplashScreen()
//            splashScreen.setOnExitAnimationListener { splashScreenViewProvider ->
//                splashScreenViewProvider.iconView
//                    .animate()
//                    .setDuration(splashFadeDurationMillis.toLong())
//                    .alpha(0f)
//                    .withEndAction {
//                        // After the fade out, remove the splash and set content view
//                        splashScreenViewProvider.remove()
//                        setContent{
//                            SplashScreen()
//                        }
//                    }.start()
//            }
//        } else {

        mainViewModel.viewModelScope.launch(Dispatchers.IO) {
            userDao.getAllUsers().collect {
                withContext(Dispatchers.Main) {
                    setContent {
                        ChipControllerApp(appContainer)
                    }
                }

            }
        }

//        }
    }
}