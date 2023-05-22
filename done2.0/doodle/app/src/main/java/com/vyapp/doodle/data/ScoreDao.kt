package com.vyapp.doodle.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface ScoreDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun updateScore(scoreEntity: ScoreEntity)

    @Query("SELECT * FROM score WHERE id=(:id)")
    fun getValue(id: Long): LiveData<ScoreEntity?>

}