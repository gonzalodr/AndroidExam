package com.moviles.courses.Daos



import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.moviles.courses.RoomEntity.CourseEntity
import com.moviles.courses.RoomEntity.StudentEntity

@Dao
interface DaosCourse {

    @Query("SELECT * FROM courses")
    suspend fun getAllCourses(): List<CourseEntity>

    @Query("SELECT * FROM courses WHERE id = :id LIMIT 1")
    suspend fun getCourseById(id: Int): CourseEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(courses: List<CourseEntity>)

    @Query("DELETE FROM courses")
    suspend fun clearAll()

    @Query("SELECT * FROM students WHERE courseId = :courseId")
    suspend fun getStudentByCourse(courseId:Int):List<StudentEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAllStudentsByCourse(students: List<StudentEntity>)
}