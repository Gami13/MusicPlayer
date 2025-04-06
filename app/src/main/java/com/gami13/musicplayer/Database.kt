package com.gami13.musicplayer

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Delete
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase

@Entity(tableName = "songs")
data class Song(
  @PrimaryKey(autoGenerate = true) val id: Int = 0,
  var youtubeId: String = "",
  var title: String = "",
  var storagePath: String = "",
  var duration: Int = 0,
  var album: String = "",
  var artist: String = "",
  var genre: String = "",
  var year: Int = 0,
  var cover: ByteArray = ByteArray(0),
  var isFavorite: Boolean = false,
)


@Dao
interface SongDao {
  @Query("SELECT * FROM songs")
  fun getAll(): List<Song>

  @Query("SELECT * FROM songs WHERE youtubeId = :youtubeId")
  fun getSong(youtubeId: String): Song

  @Insert
  fun insert(song: Song)

  @Insert
  fun insertAll(vararg songs: Song)


  @Delete
  fun delete(song: Song)
}


@Database(
  entities = [Song::class], version = 2,
//  autoMigrations = [AutoMigration(1, 2)],
  exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {
  abstract fun songDao(): SongDao

}