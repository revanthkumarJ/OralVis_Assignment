package com.example.oralvis.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: SessionEntity)


    @Query("UPDATE sessions SET name=:name, age=:age, endedAt=:endedAt WHERE sessionId=:id")
    suspend fun endSession(id: String, name: String, age: Int, endedAt: Long)


    @Query("SELECT * FROM sessions WHERE sessionId=:id")
    suspend fun get(id: String): SessionEntity?
}