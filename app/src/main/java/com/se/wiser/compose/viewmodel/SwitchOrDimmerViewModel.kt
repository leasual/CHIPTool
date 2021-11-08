package com.se.wiser.compose.viewmodel

import androidx.lifecycle.ViewModel
import com.se.wiser.App
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SwitchOrDimmerViewModel @Inject constructor(val app: App): ViewModel() {

}