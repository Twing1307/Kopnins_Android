package com.vyapp.doodle.data

import android.content.Context
import androidx.room.Room
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


//izstrādāts, pamatojoties uz kodu no grāmatas "Android. Programmēšana profesionāļiem. 4. izdevums"
class ScoreRepository private constructor(context: Context)  {

    private val database : ScoreDatabase = Room.databaseBuilder(
        context.applicationContext,
        ScoreDatabase::class.java,
        "DATABASE_NAME"
    ).build()

    private val scoreDao = database.scoreDao()


    @OptIn(DelicateCoroutinesApi::class)
    fun updateScore(score: Long){
        GlobalScope.launch {
            scoreDao.updateScore(ScoreEntity(score = score))
        }
    }

    fun getValue(id: Long) = scoreDao.getValue(id)

    companion object {
        private var INSTANCE: ScoreRepository? = null
        fun initialize(context: Context) {
            if (INSTANCE == null) {
                INSTANCE = ScoreRepository(context)
            }
        }
        fun get(): ScoreRepository {
            return INSTANCE ?:
            throw IllegalStateException("ScoreRepository must be initialized")
        }
    }
}