package com.se.wiser.data.model

import androidx.room.Embedded
import androidx.room.Relation
import com.se.wiser.data.entity.HomeEntity
import com.se.wiser.data.entity.UserEntity

data class UserAndHomeList(
    @Embedded val user: UserEntity,
    @Relation(
        parentColumn = "userId",
        entityColumn = "userId"
    )
    val homeList: List<HomeEntity>?
)
