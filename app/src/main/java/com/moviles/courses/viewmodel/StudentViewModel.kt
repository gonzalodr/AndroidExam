package com.moviles.courses.viewmodel


import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.moviles.courses.models.Student
import com.moviles.courses.Repository.StudentRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class StudentViewModel  (private val studentRepository: StudentRepository) : ViewModel() {

    private val _students = MutableStateFlow<List<Student>>(emptyList())
    val students: StateFlow<List<Student>> get() = _students

    private val _selectedStudent = MutableStateFlow<Student?>(null)
    val selectedStudent: StateFlow<Student?> get() = _selectedStudent

    fun fetchStudents() {
        viewModelScope.launch {
            try {
                _students.value = studentRepository.getStudents()
                Log.i("StudentViewModel", "Fetched students")
            } catch (e: Exception) {
                Log.e("fetchStudents", "Error: $e")
            }
        }
    }

    fun fetchStudentById(studentId: Int) {
        viewModelScope.launch {
            try {
                _selectedStudent.value = studentRepository.getStudentById(studentId)
                Log.i("StudentViewModel", "Fetched student by ID: ${_selectedStudent.value}")
            } catch (e: Exception) {
                Log.e("fetchStudentById", "Error: $e")
            }
        }
    }

    fun addStudent(student: Student) {
        viewModelScope.launch {
            try {
                val newStudent = studentRepository.addStudent(student)
                _students.value = _students.value + newStudent
                Log.i("StudentViewModel", "Added new student: $newStudent")
            } catch (e: Exception) {
                Log.e("addStudent", "Error: $e")
            }
        }
    }

    fun updateStudent(studentId: Int, updatedStudent: Student) {
        viewModelScope.launch {
            try {
                val updated = studentRepository.updateStudent(studentId, updatedStudent)
                _students.value = _students.value.map { if (it.id == studentId) updated else it }
                Log.i("StudentViewModel", "Updated student: $updatedStudent")
            } catch (e: Exception) {
                Log.e("updateStudent", "Error: $e")
            }
        }
    }

    fun deleteStudent(studentId: Int) {
        viewModelScope.launch {
            try {
                studentRepository.deleteStudent(studentId)
                _students.value = _students.value.filter { it.id != studentId }
                Log.i("StudentViewModel", "Deleted student with ID: $studentId")
            } catch (e: Exception) {
                Log.e("deleteStudent", "Error: $e")
            }
        }
    }
}


//class StudentViewModel:ViewModel() {
//
//    private val _students = MutableStateFlow<List<Student>>(emptyList())
//    val students: StateFlow<List<Student>> get() = _students
//
//    private val _selectedStudent = MutableStateFlow<Student?>(null)
//    val selectedStudent: StateFlow<Student?> get() = _selectedStudent
//
//    fun fetchStudents() {
//        viewModelScope.launch {
//            try {
//                val response = RetrofitInstance.api.getStudens()
//                _students.value = response
//                Log.i("StudentViewModel","Student $response")
//            } catch (e: Exception) {
//                Log.e("fetchStudents", "Error: $e")
//            }
//        }
//    }
//
//    fun fetchStudentById(studentId: Int) {
//        viewModelScope.launch {
//            try {
//                val student = RetrofitInstance.api.getStudentById(studentId)
//                _selectedStudent.value = student
//                Log.i("StudentViewModel","Student $student")
//            } catch (e: Exception) {
//                Log.e("fetchStudentById", "Error: $e")
//            }
//        }
//    }
//
//    fun addStudent(student: Student) {
//        viewModelScope.launch {
//            try {
//                val newStudent = RetrofitInstance.api.addStudent(student)
//                _students.value = _students.value + newStudent
//                Log.i("StudentViewModel","Student $newStudent")
//            } catch (e: Exception) {
//                Log.e("addStudent", "Error: $e")
//            }
//        }
//    }
//
//    fun updateStudent(studentId: Int, updatedStudent: Student) {
//        viewModelScope.launch {
//            try {
//                val updated = RetrofitInstance.api.updateStudent(studentId, updatedStudent)
//                _students.value = _students.value.map { if (it.id == studentId) updated else it }
//                Log.i("StudentViewModel","Student $updatedStudent")
//            } catch (e: Exception) {
//                Log.e("updateStudent", "Error: $e")
//            }
//        }
//    }
//
//    fun deleteStudent(studentId: Int) {
//        viewModelScope.launch {
//            try {
//                RetrofitInstance.api.deleteStudent(studentId)
//                _students.value = _students.value.filter { it.id != studentId }
//                Log.i("StudentViewModel","Student $studentId")
//            } catch (e: Exception) {
//                Log.e("deleteStudent", "Error: $e")
//            }
//        }
//    }
//}