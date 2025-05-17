package com.moviles.courses.viewmodel

import android.app.Application
import android.net.Uri
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.courses.Repository.CourseRepository
import com.moviles.courses.models.Course
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class CourseViewModel(
    application: Application,
    private val courseRepository: CourseRepository
) : AndroidViewModel(application) {

    private val _courses = MutableStateFlow<List<Course>>(emptyList())
    val courses: StateFlow<List<Course>> get() = _courses

    private val _selectedCourse = MutableStateFlow<Course?>(null)
    val selectedCourse: StateFlow<Course?> get() = _selectedCourse

    fun fetchCourses() {
        viewModelScope.launch {
            try {
                _courses.value = courseRepository.getCourses()
                Log.i("CourseViewModel", "Fetched courses")
            } catch (e: Exception) {
                Log.e("fetchCourses", "Error: $e")
            }
        }
    }

    fun fetchCourseById(courseId: Int) {
        viewModelScope.launch {
            try {
                _selectedCourse.value = courseRepository.getCourseById(courseId)
            } catch (e: Exception) {
                Log.e("fetchCourseById", "Error: $e")
            }
        }
    }

//    fun addCourse(course: Course) {
//        viewModelScope.launch {
//            try {
//                val newCourse = courseRepository.addCourse(course)
//                _courses.value = _courses.value + newCourse
//            } catch (e: Exception) {
//                Log.e("addCourse", "Error: $e")
//            }
//        }
//    }

    fun addCourseWithImage(name: String, description: String, professor: String, schedule: String, imageUri: Uri) {
        viewModelScope.launch {
            try {
                Log.e(" coursevm 60","$name $description $professor $schedule $imageUri")
                val contentResolver = getApplication<Application>().contentResolver
                val imageBytes = contentResolver.openInputStream(imageUri)?.readBytes()
                    ?: throw Exception("No se pudo leer la imagen")
                val newCourse = courseRepository.addCourseWithImage(
                    name,
                    description,
                    professor,
                    schedule,
                    imageBytes
                )
                _courses.value = _courses.value + newCourse
            } catch (e: Exception) {
                Log.e("addCourseWithImage", "Error: $e")
            }
        }
    }

    fun updateCourse(courseId: Int, updatedCourse: Course, imageUri: Uri? = null) {
        viewModelScope.launch {
            try {
                //get image
                val contentResolver = getApplication<Application>().contentResolver
                val imageBytes = imageUri?.let {
                    contentResolver.openInputStream(it)?.readBytes()
                        ?: throw Exception("No se pudo leer la imagen")
                }
                courseRepository.updateCourse(courseId, updatedCourse, imageBytes)
                fetchCourses() // Refresh the list after updating
            } catch (e: Exception) {
                Log.e("updateCourse", "Error: $e")
            }
        }
    }

    fun deleteCourse(courseId: Int) {
        viewModelScope.launch {
            try {
                courseRepository.deleteCourse(courseId)
                _courses.value = _courses.value.filter { it.id != courseId }
            } catch (e: Exception) {
                Log.e("deleteCourse", "Error: $e")
            }
        }
    }
}
