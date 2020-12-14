package com.michaldrabik.storage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.michaldrabik.storage.database.model.MoviesSyncLog

@Dao
interface MoviesSyncLogDao {

  @Query("SELECT * from sync_movies_log")
  suspend fun getAll(): List<MoviesSyncLog>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun upsert(log: MoviesSyncLog)
}
