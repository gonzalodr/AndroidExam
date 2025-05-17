package com.moviles.courses.Repository

import androidx.compose.ui.platform.LocalContext
import com.moviles.courses.Daos.DaosCourse
import com.moviles.courses.MappersEntity.toEntity
import com.moviles.courses.MappersEntity.toModel
import com.moviles.courses.models.Course
import com.moviles.courses.network.ApiServices
import com.moviles.courses.network.RetrofitInstance
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities


import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody


class CourseRepository (private val api: ApiServices, private val courseDao:DaosCourse, private val context: Context) {

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }

    suspend fun getCourses(): List<Course> {
        return try {
            if (!isNetworkAvailable()){
                throw Exception()
            }
            val remoteCourses = api.getCourses()
            courseDao.clearAll()
            courseDao.insertAll(remoteCourses.map { it.toEntity() })
            remoteCourses
        } catch (e: Exception) {
            courseDao.getAllCourses().map { courseEntity ->
                val students = courseDao.getStudentByCourse(courseEntity.id ?: 0)
                courseEntity.toModel(students.map { it.toModel() })
            }
        }
    }

    suspend fun getCourseById(id: Int): Course {
        return try {
            if (!isNetworkAvailable()){
                throw Exception()
            }
            val remoteCourse = api.getCourseById(id)
            courseDao.insertAll(listOf(remoteCourse.toEntity()))

            val studentEntities = remoteCourse.students?.map { it.toEntity() } ?: emptyList()
            if (studentEntities.isNotEmpty()) {
                courseDao.insertAllStudentsByCourse(studentEntities)
            }
            remoteCourse
//            api.getCourseById(id).also { remoteCourse ->
//                courseDao.insertAll(listOf(remoteCourse.toEntity()))
//            }
//            courseDao.insertAllStudentsByCourse()
        } catch (e: Exception) {
            val localCourse = courseDao.getCourseById(id)
            val students = courseDao.getStudentByCourse(id)
            localCourse.toModel(students.map { it.toModel() })
        }
    }

//    suspend fun addCourse(course: Course): Course {
//        val added = api.addCourse(course)
//        courseDao.insertAll(listOf(added.toEntity()))
//        return added
//    }

    suspend fun updateCourse(id: Int, course: Course, imageBytes: ByteArray?): Course {
        val namePart = course.name.toRequestBody("text/plain".toMediaType())
        val descPart = course.description.toRequestBody("text/plain".toMediaType())
        val profPart = course.professor.toRequestBody("text/plain".toMediaType())
        val schedPart = course.schedule.toRequestBody("text/plain".toMediaType())

        val filePart = imageBytes?.let {
            val requestBody = it.toRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("file", "image.jpg", requestBody)
        }

        return api.updateCourse(id, namePart, descPart, schedPart, profPart, filePart)
    }

    suspend fun deleteCourse(id: Int) {
        api.deleteCourse(id)
        val localCourses = courseDao.getAllCourses().filter { it.id != id }
        courseDao.clearAll()
        courseDao.insertAll(localCourses)
    }

    // addCourseWithImage new function
    suspend fun addCourseWithImage(
        name: String,
        description: String,
        professor: String,
        schedule: String,
        imageBytes: ByteArray
    ): Course {
        val requestBody = imageBytes.toRequestBody("image/*".toMediaTypeOrNull())
        val filePart = MultipartBody.Part.createFormData("file", "image.jpg", requestBody)

        val namePart = name.toRequestBody("text/plain".toMediaType())
        val descPart = description.toRequestBody("text/plain".toMediaType())
        val profPart = professor.toRequestBody("text/plain".toMediaType())
        val schedPart = schedule.toRequestBody("text/plain".toMediaType())

        return RetrofitInstance.api.addCourseWithImage(
            namePart,
            descPart,
            profPart,
            schedPart,
            filePart
        )
    }

}