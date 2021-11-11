package com.se.wiser.compose.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.se.wiser.App
import com.se.wiser.data.dao.HomeDao
import com.se.wiser.data.dao.UserDao
import com.se.wiser.data.entity.HomeEntity
import com.se.wiser.data.entity.UserEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class CreateUserHomeViewModel@Inject constructor(val app: App,
                                                 val userDao: UserDao,
                                                 val homeDao: HomeDao
): ViewModel() {

    val validateFields = MutableStateFlow(String())
    fun validateUserNameAndHomeName(userName: String?, homeName: String?) {
        if (userName?.isNullOrEmpty() == true) {
            validateFields.value = "User name is empty, please input user name"
            return
        }
        if (homeName?.isNullOrEmpty() == true) {
            validateFields.value = "Home name is empty, please input home name"
            return
        }
    }

    fun createUserAndHome(userName: String, homeName: String) {
        viewModelScope.launch(Dispatchers.IO) {
            withContext(Dispatchers.IO) {
                userDao.insertUsers(
                    UserEntity(0, userName, currentUserId = 0)
                )
            }
            withContext(Dispatchers.IO) {
                homeDao.insertHomes(HomeEntity(0, 0, homeName, currentHomeId = 0))
            }
        }
    }
}