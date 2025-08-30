package com.example.oralvis.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "sessions")
data class SessionEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionId:String?=null,
    val name: String? = null,
    val age:Int?=null,
    val startedAt: Long = System.currentTimeMillis(),
    val updatedAt: Long? = null,
    val totalPhotos:Long=0L
)