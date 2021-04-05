package com.rudyrachman16.githubuserv2.data.sqlite

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [FavUser::class], version = 1)
abstract class FavDatabase : RoomDatabase() {
    companion object {
        private var INSTANCE: FavDatabase? = null
        fun getInstance(context: Context): FavDatabase = INSTANCE ?: synchronized(context) {
            INSTANCE ?: Room.databaseBuilder(context, FavDatabase::class.java, "fav_users_git_hub")
                .fallbackToDestructiveMigration().build()
        }
    }

    abstract fun favDao(): FavDao
}