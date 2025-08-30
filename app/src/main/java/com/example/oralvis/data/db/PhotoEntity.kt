package com.example.oralvis.data.db

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "photos",
    foreignKeys = [
        ForeignKey(
            entity = SessionEntity::class,
            parentColumns = ["sessionId"],
            childColumns = ["sessionOwnerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("sessionOwnerId")]
)
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val sessionOwnerId: String,
    val contentUri: String,
    val timestamp: Long = System.currentTimeMillis()
)