package com.moviles.courses


import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.moviles.courses.models.Student
import com.moviles.courses.ui.theme.CoursesTheme
import com.moviles.courses.viewmodel.CourseViewModel
import com.moviles.courses.viewmodel.StudentViewModel
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import com.moviles.courses.AppDataBase.AppDatabase
import com.moviles.courses.Repository.CourseRepository
import com.moviles.courses.Repository.StudentRepository
import com.moviles.courses.network.RetrofitInstance
import com.moviles.courses.viewmodel.CourseViewModelFactory
import com.moviles.courses.viewmodel.StudentViewModelFactory

class StudentDetailActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val studentId = intent.getIntExtra("studentId", -1)

        val courseRepository = CourseRepository(RetrofitInstance.api, AppDatabase.getInstance(applicationContext).DaosCourse(),applicationContext)
        val studentRepository = StudentRepository(RetrofitInstance.api, AppDatabase.getInstance(applicationContext).DaosStudent(),applicationContext)

        setContent {
            CoursesTheme {
                val modelCourse: CourseViewModel = viewModel(
                    factory = CourseViewModelFactory(application, courseRepository)
                )

                val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModelFactory(studentRepository))
                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) { innerPadding ->
                    ShowInformationToStudent(
                        studentId = studentId,
                        viewModelStudent = studentViewModel,
                        viewModelCourse = modelCourse, // ✅ Nombre correcto
                        modifier = Modifier.padding(innerPadding)
                    )

                }
            }
        }
    }
    companion object {
        fun isNetworkAvailable(context: Context): Boolean {
            val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            return capabilities?.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET) == true
        }
    }
}

@Composable
fun ShowInformationToStudent(studentId:Int,viewModelStudent: StudentViewModel,viewModelCourse: CourseViewModel, modifier: Modifier = Modifier){

    val student by viewModelStudent.selectedStudent.collectAsState()
    val course by viewModelCourse.selectedCourse.collectAsState()

    var context = LocalContext.current
    LaunchedEffect(Unit) {
        if(!StudentsActivity.isNetworkAvailable(context)){
            Toast.makeText(context,"No internet connection", Toast.LENGTH_SHORT).show()
            Toast.makeText(context,"Loading local data...", Toast.LENGTH_SHORT).show()
        }
        viewModelStudent.fetchStudentById(studentId)
    }
    LaunchedEffect(student) {
        student?.courseId?.let {
            viewModelCourse.fetchCourseById(it)
        }
    }


    Column(Modifier.padding(all = 20.dp)){
        Text(text="Student information", textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.headlineLarge)

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Student's name:", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
        Text(text = "${student?.name}", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Student's email:", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
        Text(text = "${student?.email}", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Student's phone:", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
        Text(text = "${student?.phone}", modifier = Modifier.fillMaxWidth())

        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Course:", modifier = Modifier.fillMaxWidth(), style = MaterialTheme.typography.titleMedium)
        Text(text = "${course?.name}", modifier = Modifier.fillMaxWidth())
    }
}