package com.example.oralvis.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey val sessionId: String,
    val name: String? = null,
    val age: Int? = null,
    val startedAt: Long = System.currentTimeMillis(),
    val endedAt: Long? = null
)