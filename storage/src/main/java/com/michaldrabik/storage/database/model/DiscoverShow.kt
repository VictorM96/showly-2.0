package com.michaldrabik.storage.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

//TODO Foreign key to show
@Entity(tableName = "shows_discover")
data class DiscoverShow(
  @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id") var id: Long = 0,
  @ColumnInfo(name = "id_trakt", defaultValue = "-1") var idTrakt: Long,
  @ColumnInfo(name = "created_at", defaultValue = "-1") var createdAt: Long,
  @ColumnInfo(name = "updated_at", defaultValue = "-1") var updatedAt: Long
)