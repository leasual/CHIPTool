package com.se.wiser.viewmodel

import android.app.Activity
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.chip.chiptool.ChipClient
import com.se.wiser.App
import com.se.wiser.model.BaseDevice
import com.se.wiser.model.CommissioningInfo
import com.se.wiser.utils.ClusterUtil
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class SocketControlViewModel: ControlViewModel() {

}