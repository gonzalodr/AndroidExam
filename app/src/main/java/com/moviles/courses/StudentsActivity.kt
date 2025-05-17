package com.moviles.courses



import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.runtime.getValue
import com.moviles.courses.AppDataBase.AppDatabase
import com.moviles.courses.Repository.CourseRepository
import com.moviles.courses.Repository.StudentRepository


import com.moviles.courses.models.Student
import com.moviles.courses.network.RetrofitInstance
import com.moviles.courses.ui.theme.CoursesTheme
import com.moviles.courses.viewmodel.CourseViewModel
import com.moviles.courses.viewmodel.CourseViewModelFactory
import com.moviles.courses.viewmodel.StudentViewModel
import com.moviles.courses.viewmodel.StudentViewModelFactory

class StudentsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val courseId = intent.getIntExtra("courseId", -1)

        val courseRepository = CourseRepository(RetrofitInstance.api, AppDatabase.getInstance(applicationContext).DaosCourse(),applicationContext)
        val studentRepository = StudentRepository(RetrofitInstance.api, AppDatabase.getInstance(applicationContext).DaosStudent(),applicationContext)
        setContent {
            CoursesTheme {
                val courseViewModel: CourseViewModel = viewModel(
                    factory = CourseViewModelFactory(application, courseRepository)
                )

                val studentViewModel: StudentViewModel = viewModel(factory = StudentViewModelFactory(studentRepository))

                Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.White) { innerPadding ->
                    ShowListStudentsByCourses(
                        courseId = courseId,
                        viewModel = studentViewModel,
                        viewModelCourse = courseViewModel,
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
fun ShowListStudentsByCourses( courseId: Int,viewModel: StudentViewModel, viewModelCourse: CourseViewModel, modifier: Modifier = Modifier){
    val showDialog = remember { mutableStateOf(false) }
    val selectedStudent = remember { mutableStateOf<Student?>(null) }

    val course by viewModelCourse.selectedCourse.collectAsState()

//    LaunchedEffect(Unit) {
//        viewModelCourse.fetchCourseById(courseId)
//    }
    var context = LocalContext.current
    LaunchedEffect(courseId, showDialog.value) {
        if(!StudentsActivity.isNetworkAvailable(context)){
            Toast.makeText(context,"No internet connection", Toast.LENGTH_SHORT).show()
            Toast.makeText(context,"Loading local data...", Toast.LENGTH_SHORT).show()
        }
        viewModelCourse.fetchCourseById(courseId)
    }

    Column(modifier = Modifier.padding(16.dp).background(Color(0xFFFFFFFF))) {
        //title of the view
        Text(text="Student in ${course?.name}", style = MaterialTheme.typography.headlineLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
        Spacer(modifier = Modifier.height(16.dp))
        //list of student
        LazyColumn(modifier = Modifier.weight(1f).fillMaxHeight()) {
            //loading the students in the LazyColumn
            items(course?.students ?: emptyList()) { student ->
                CardsStudents(student = student, clickEditStudent = {
                    showDialog.value = true
                    selectedStudent.value = student
                },clickDelete={ idStudent->
                    if (idStudent!= null){
                        viewModel.deleteStudent(idStudent)
                    }
                    viewModelCourse.fetchCourseById(courseId)
                })
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        //Button to add new student
        Button(
            onClick = { showDialog.value = true
                selectedStudent.value = null},
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            Text("Add Student")
        }

        if (showDialog.value){
            DialogAddStundentAtCourse(student = selectedStudent.value, onDismiss = {
                showDialog.value = false
            }, courseId = courseId, onSave = { newStudent ->
                if(newStudent.id == null){
                    viewModel.addStudent(newStudent)
                }else{
                    viewModel.updateStudent(newStudent.id,newStudent)
                }
                //viewModelCourse.fetchCourseById(courseId)
                Log.e("Dialog student"," id:${newStudent.id} name: ${newStudent.name} email ${newStudent.email} courseId: ${newStudent.courseId}")
            })
        }
    }
}

@Composable
fun CardsStudents(student: Student, clickEditStudent:()->Unit, clickDelete:(Int)->Unit){
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).clickable {
            val intent = Intent(context, StudentDetailActivity::class.java)
            intent.putExtra("studentId", student.id)
            context.startActivity(intent)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.LightGray
        )
    ) {
        Row(modifier = Modifier.fillMaxWidth()){
            //Information student information
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = student.name, style = MaterialTheme.typography.titleLarge, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Center)
                Text(text = student.email, style = MaterialTheme.typography.titleSmall, modifier = Modifier.fillMaxWidth(), textAlign = TextAlign.Left)

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    //Button to edit
                    Button( onClick = clickEditStudent, modifier = Modifier.height(35.dp).align(Alignment.Bottom),
                        colors = ButtonDefaults.buttonColors( containerColor = Color(0xFF4CAF50), contentColor = Color.White)
                    ) { Text("Edit") }

                    Spacer(modifier = Modifier.width(35.dp))

                    Button(
                        onClick = {
                            var idStudent = student?.id
                            clickDelete(idStudent ?: -1)
                            Log.i("Cards", "Card delete ${student.id}")
                        },
                        //Design of the button
                        modifier = Modifier.height(35.dp).align(Alignment.CenterVertically),
                        colors = ButtonDefaults.buttonColors( containerColor = Color.Red, contentColor = Color.White)
                    ) { Text("Delete") }
                }
            }
        }
    }
}

@Composable
fun DialogAddStundentAtCourse(student: Student?,courseId:Int, onDismiss: ()->Unit, onSave:(Student)->Unit){
    val name        = remember { mutableStateOf(student?.name?:"") }
    val email       = remember { mutableStateOf(student?.email?:"") }
    val phone       = remember { mutableStateOf(student?.phone?:"") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add new course") },
        text = {
            Column {
                OutlinedTextField(value = name.value,
                    onValueChange = { name.value = it },
                    label = { Text("Student's name") }
                )
                OutlinedTextField(value = email.value,
                    onValueChange = { email.value = it },
                    label = { Text("Student's email") }
                )
                OutlinedTextField(value = phone.value,
                    onValueChange = { phone.value = it },
                    label = { Text("Student's phone") }
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                //Add courses to list
                val newStudent = Student(
                    id = student?.id,
                    name = name.value,
                    email = email.value,
                    phone = phone.value,
                    courseId = courseId
                )
                onSave(newStudent)
                Log.i("new Student", "CouresId: ${newStudent.courseId}  Name: ${name.value}, email: ${email.value}")
                onDismiss()
            }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}