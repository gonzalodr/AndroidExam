package com.moviles.courses.AppDataBase

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.moviles.courses.Daos.DaosCourse
import com.moviles.courses.Daos.DaosStudent
import com.moviles.courses.RoomEntity.CourseEntity
import com.moviles.courses.RoomEntity.StudentEntity

@Database(entities = [CourseEntity::class, StudentEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun DaosCourse(): DaosCourse
    abstract fun DaosStudent(): DaosStudent

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}