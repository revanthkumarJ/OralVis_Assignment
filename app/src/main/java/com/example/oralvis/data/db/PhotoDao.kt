package com.example.oralvis.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PhotoDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(photo: PhotoEntity)


    @Query("SELECT * FROM photos WHERE sessionOwnerId=:sessionId ORDER BY timestamp ASC")
    suspend fun forSession(sessionId: String): List<PhotoEntity>
}