package com.vyapp.doodle.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "score")
data class ScoreEntity(@PrimaryKey var id: Int = 0, var score: Long)