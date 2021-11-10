package com.se.wiser.data

import com.se.wiser.App


interface AppContainer {

}

class AppContainerImpl(private val app: App): AppContainer {

}