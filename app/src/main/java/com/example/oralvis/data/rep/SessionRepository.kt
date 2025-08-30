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
    suspend fun startSession(id: String) {
        sessionDao.insert(SessionEntity(sessionId = id))
    }


    suspend fun endSession(id: String, name: String, age: Int) {
        sessionDao.endSession(id, name, age, System.currentTimeMillis())
    }


    suspend fun addPhoto(sessionId: String, uri: Uri) {
        photoDao.insert(
            PhotoEntity(
                sessionOwnerId = sessionId,
                contentUri = uri.toString(),
            )
        )
    }


    suspend fun getSession(id: String): Pair<SessionEntity, List<PhotoEntity>>? {
        val s = sessionDao.get(id) ?: return null
        val photos = photoDao.forSession(id)
        return s to photos
    }
}