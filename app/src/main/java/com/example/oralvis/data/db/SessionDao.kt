package com.example.oralvis.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SessionDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(session: SessionEntity):Long

    @Query("UPDATE sessions SET sessionId=:sessionId, name=:name, age=:age, updatedAt=:endedAt, totalPhotos=:total WHERE sessionId=:id")
    suspend fun updateSession(id: Long,sessionId:String, name: String, age:Int,endedAt: Long,total:Long)


    @Query("SELECT * FROM sessions WHERE sessionId=:id")
    suspend fun get(id: String): SessionEntity?

    @Query("SELECT * FROM sessions ORDER BY startedAt DESC")
    fun observeAllSessions(): Flow<List<SessionEntity>>
}