package com.michaldrabik.storage.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.michaldrabik.storage.database.model.Movie
import com.michaldrabik.storage.database.model.WatchlistMovie

@Dao
interface WatchlistMoviesDao {

  @Query("SELECT movies.*, movies_see_later.updated_at FROM movies INNER JOIN movies_see_later USING(id_trakt)")
  suspend fun getAll(): List<Movie>

  @Query("SELECT movies.id_trakt FROM movies INNER JOIN movies_see_later USING(id_trakt)")
  suspend fun getAllTraktIds(): List<Long>

  @Query("SELECT movies.* FROM movies INNER JOIN movies_see_later ON movies_see_later.id_trakt == movies.id_trakt WHERE movies.id_trakt == :traktId")
  suspend fun getById(traktId: Long): Movie?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(show: WatchlistMovie)

  @Query("DELETE FROM movies_see_later WHERE id_trakt == :traktId")
  suspend fun deleteById(traktId: Long)
}
