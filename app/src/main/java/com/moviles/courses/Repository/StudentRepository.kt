package com.moviles.courses.Repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.moviles.courses.Daos.DaosStudent
import com.moviles.courses.MappersEntity.toEntity
import com.moviles.courses.MappersEntity.toModel
import com.moviles.courses.models.Student
import com.moviles.courses.network.ApiServices

class StudentRepository (private val api: ApiServices, private val studentDao: DaosStudent,  private val context: Context) {

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val activeNetwork = connectivityManager.activeNetwork
        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
        return networkCapabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
    }


    suspend fun getStudents(): List<Student> {
        return try {
            if (!isNetworkAvailable()){
                throw Exception()
            }
            val remote = api.getStudens()
            studentDao.clearAll()
            studentDao.insertAll(remote.map { it.toEntity() })
            remote
        } catch (e: Exception) {
            studentDao.getAllStudents().map { it.toModel() }
        }
    }

    suspend fun getStudentById(id: Int): Student {

        return try {
            if (!isNetworkAvailable()){
                throw Exception()
            }
            api.getStudentById(id).also { remoteStudent ->
                studentDao.insertAll(listOf(remoteStudent.toEntity()))
            }
        } catch (e: Exception) {
            val localStudent = studentDao.getStudentById(id)
            localStudent.toModel()
        }
    }

    suspend fun addStudent(student: Student): Student {
        val added = api.addStudent(student)
        studentDao.insertAll(listOf(added.toEntity()))
        return added
    }

    suspend fun updateStudent(id: Int, student: Student): Student {
        val updated = api.updateStudent(id, student)
        studentDao.insertAll(listOf(updated.toEntity()))
        return updated
    }

    suspend fun deleteStudent(id: Int) {
        api.deleteStudent(id)
        val local = studentDao.getAllStudents().filter { it.id != id }
        studentDao.clearAll()
        studentDao.insertAll(local)
    }
}
