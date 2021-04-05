package com.rudyrachman16.githubuserv2.data.sqlite

import android.database.Cursor
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface FavDao {

    @Insert
    fun insert(user: FavUser): Long

    @Query("DELETE FROM fav_user WHERE id=:idSearch")
    fun delete(idSearch: String): Int

    @Query("SELECT * FROM fav_user ORDER BY id ASC")
    fun getAll(): Cursor

    @Query("SELECT COUNT(id) result FROM fav_user WHERE id=:idSearch")
    fun available(idSearch: String): Cursor
}