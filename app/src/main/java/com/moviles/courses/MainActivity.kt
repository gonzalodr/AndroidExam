package com.moviles.courses

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.ktx.messaging
import com.moviles.courses.AppDataBase.AppDatabase
import com.moviles.courses.Repository.CourseRepository
import com.moviles.courses.common.Constants.API_BASE_URL
import com.moviles.courses.common.Constants.IMAGES_URL
import com.moviles.courses.models.Course
import com.moviles.courses.network.RetrofitInstance
import com.moviles.courses.ui.theme.CoursesTheme
import com.moviles.courses.viewmodel.CourseViewModel
import com.moviles.courses.viewmodel.CourseViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        createNotificationChannel(this)
        subscribeToTopic()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TOKEN_FCM", "Error al obtener el token FCM", task.exception)
                    return@addOnCompleteListener
                }
                val token = task.result
                Log.d("TOKEN_FCM", "Token FCM actual: $token")
            }


        val courseDao = AppDatabase.getInstance(applicationContext).DaosCourse()
        val apiServices = RetrofitInstance.api
        val courseRepository = CourseRepository(apiServices, courseDao,applicationContext)

        setContent {
            CoursesTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    containerColor = Color.White,
                ) { innerPadding ->
                    val modelCourse: CourseViewModel = viewModel(
                        factory = CourseViewModelFactory(application, courseRepository)
                    )


                    ShowListCourses(
                        title = "Cursos",
                        viewModel = modelCourse,
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
fun ShowListCourses(title: String, viewModel: CourseViewModel, modifier: Modifier = Modifier) {
    val showDialog = remember { mutableStateOf(false) }
    val selectedCourse = remember { mutableStateOf<Course?>(null) }
    val courses by viewModel.courses.collectAsState()
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        if(!MainActivity.isNetworkAvailable(context)){
            Toast.makeText(context,"No internet connection", Toast.LENGTH_SHORT).show()
            Toast.makeText(context,"Loading local data...", Toast.LENGTH_SHORT).show()
        }
        viewModel.fetchCourses()
    }



    Column(modifier = Modifier.padding(16.dp).background(Color.White)) {
        Text(title, style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))
        LazyColumn(modifier = Modifier.weight(1f).fillMaxHeight()) {
            items(courses) { course ->
                CardsCourses(
                    course = course,
                    clikEditCourse = {
                        selectedCourse.value = course
                        showDialog.value = true
                    },
                    clickDelete = { idCourse ->
                        viewModel.deleteCourse(idCourse)
                        viewModel.fetchCourses()
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
        Button(
            onClick = {
                showDialog.value = true
                selectedCourse.value = null
            },
            modifier = Modifier.fillMaxWidth().height(40.dp)
        ) {
            Text("Add course")
        }
    }

    if (showDialog.value) {
        DialogAddCourse(
            selected = selectedCourse.value,
            onDismiss = { showDialog.value = false },
            viewModel = viewModel
        )
    }
}


@Composable
fun CardsCourses(course: Course, clikEditCourse: () -> Unit, clickDelete: (Int) -> Unit) {
    val context = LocalContext.current
    val imageUrl = IMAGES_URL + course.imageUrl
    Log.e("mainactivit","cartas $course.imageUrl")

    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp).clickable {
            val intent = Intent(context, StudentsActivity::class.java)
            intent.putExtra("courseId", course.id)
            context.startActivity(intent)
        },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.LightGray)
    ) {
        Row(modifier = Modifier.fillMaxWidth()) {
            AsyncImage(
                model = API_BASE_URL+course.imageUrl,
                contentDescription = "Course Image",
                modifier = Modifier.size(120.dp).padding(8.dp).align(Alignment.CenterVertically),
                contentScale = ContentScale.Crop,
                error = painterResource(id = R.drawable.ic_launcher_background)
            )
            Column(
                modifier = Modifier.padding(16.dp).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(course.name, style = MaterialTheme.typography.titleLarge, textAlign = TextAlign.Center)
                Text(course.description, style = MaterialTheme.typography.titleSmall)
                Text(course.professor, style = MaterialTheme.typography.titleSmall)
                Text(course.schedule, style = MaterialTheme.typography.titleSmall)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = clikEditCourse,
                        modifier = Modifier.height(35.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) { Text("Edit") }
                    Spacer(modifier = Modifier.width(35.dp))
                    Button(
                        onClick = { course.id?.let { clickDelete(it) } },
                        modifier = Modifier.height(35.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) { Text("Delete") }
                }
            }
        }
    }
}

@Composable
fun DialogAddCourse(selected: Course?, onDismiss: () -> Unit, viewModel: CourseViewModel) {
    var name        by remember { mutableStateOf(selected?.name ?: "") }
    var description by remember { mutableStateOf(selected?.description ?: "") }
    var professor   by remember { mutableStateOf(selected?.professor ?: "") }
    var schedule    by remember { mutableStateOf(selected?.schedule ?: "") }
    var imageUrl    by remember { mutableStateOf(selected?.imageUrl?:null) }
    var imageUri    by remember { mutableStateOf<Uri?>(null) }

    var infoText    by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Add new course") },
        text = {
            Column {
                Box(
                    modifier = Modifier.fillMaxWidth().height(200.dp).clickable { launcher.launch("image/*") }
                        .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(4.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    val imageModel = imageUri ?: imageUrl?.let { API_BASE_URL + it}

                    if (imageModel != null) {
                        AsyncImage(
                            model = imageModel,
                            contentDescription = "Selected image",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Default image icon",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(64.dp)
                            )
                            Text("Add image")
                        }
                    }
                }
                Spacer(Modifier.height(16.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Course's name") })
                OutlinedTextField(value = description, onValueChange = { description = it }, label = { Text("Description") })
                OutlinedTextField(value = professor, onValueChange = { professor = it }, label = { Text("Professor") })
                OutlinedTextField(value = schedule, onValueChange = { schedule = it }, label = { Text("Schedule") })
                Spacer(Modifier.height(10.dp))
                Text(text = "$infoText", fontSize = 15.sp, color = Color.Red, textAlign = TextAlign.Center)
            }
        },
        confirmButton = {
            Button(onClick = {
                // valid inputs is empty
                if (name.isBlank() || description.isBlank() || professor.isBlank() || schedule.isBlank()) {
                    infoText = "You cannot leave empty fields"
                    return@Button
                }
                val finalImage = imageUri ?: null

                if (selected?.id == null) {
                    //valid if image is empty
                    if (finalImage == null) {
                        infoText = "Select an image"
                        return@Button
                    }
                    viewModel.addCourseWithImage(name, description, professor, schedule, finalImage)
                } else {
                    val course = Course(
                        id = selected.id,
                        name = name,
                        description = description,
                        professor = professor,
                        schedule = schedule,
                        imageUrl = null,
                        students = null
                    )
                    viewModel.updateCourse(selected.id, course, finalImage)
                }
                onDismiss()
            }) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
// This function creates a notification channel for Android O and above
// It is used to group notifications and set their importance level
fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channelId = "event_reminder_channel"
        val channelName = "Event Reminders"
        val importance = NotificationManager.IMPORTANCE_DEFAULT

        val channel = NotificationChannel(channelId, channelName, importance).apply {
            description = "Notifies users about upcoming events"
        }

        val notificationManager =
            context.getSystemService(NotificationManager::class.java)
        notificationManager?.createNotificationChannel(channel)
    }
}

fun subscribeToTopic() {
    FirebaseMessaging.getInstance().subscribeToTopic("cursos")
        .addOnCompleteListener { task ->
            var msg = "Subscription successful"
            if (!task.isSuccessful) {
                msg = "Subscription failed"
            }
            Log.d("FCM", msg)
        }
}
