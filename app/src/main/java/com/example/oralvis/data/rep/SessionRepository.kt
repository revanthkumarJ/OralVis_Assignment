package com.example.oralvis.data.rep

import android.net.Uri
import com.example.oralvis.data.db.PhotoDao
import com.example.oralvis.data.db.PhotoEntity
import com.example.oralvis.data.db.SessionDao
import com.example.oralvis.data.db.SessionEntity

class SessionRepository(
    private val sessionDao: SessionDao,
    private val photoDao: PhotoDao,
) {
    suspend fun startSession(name: String,sessionId:String,age:Int):Long {
        return sessionDao.insert(SessionEntity(name=name, sessionId = sessionId,age=age))
    }


    suspend fun updateSession(id: Long, sessionId: String,name: String,age:Int,totalPhotos:Long) {
        sessionDao.updateSession(
            id=id,
            sessionId=sessionId,
            name= name,
            age=age,
            endedAt =  System.currentTimeMillis(),
            total=totalPhotos
        )
    }


    suspend fun addPhoto(sessionId: Long, uri: Uri) {
        photoDao.insert(
            PhotoEntity(
                sessionOwnerId = sessionId,
                contentUri = uri.toString(),
            )
        )
    }


    suspend fun getSession(id: Long): Pair<SessionEntity, List<PhotoEntity>>? {
        val s = sessionDao.get(id) ?: return null
        val photos = photoDao.forSession(id)
        return s to photos
    }

    fun observeAllSessions() = sessionDao.observeAllSessions()
}