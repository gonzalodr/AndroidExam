package com.moviles.courses.network

import com.moviles.courses.models.Course
import com.moviles.courses.models.Student
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part
import retrofit2.http.Path

interface ApiServices {
    //conection to courses
    @GET("/api/course")
    suspend fun getCourses():List<Course>

    //get course by id
    @GET("api/course/{id}")
    suspend fun getCourseById(@Path("id") courseId:Int):Course

    @Multipart
    @POST("api/course")
    suspend fun addCourseWithImage(
        @Part("name") name: okhttp3.RequestBody,
        @Part("description") description: okhttp3.RequestBody,
        @Part("professor") professor: okhttp3.RequestBody,
        @Part("schedule") schedule: okhttp3.RequestBody,
        @Part file: okhttp3.MultipartBody.Part
    ): Course

//    //insert a new course
//    @POST("api/course")
//    suspend fun addCourse(@Body course: Course):Course

    //update course
    @Multipart
    @PUT("api/course/{id}")
    suspend fun updateCourse(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("description") description: RequestBody,
        @Part("schedule") schedule: RequestBody,
        @Part("professor") professor: RequestBody,
        @Part file: MultipartBody.Part?
    ): Course

    @DELETE("api/course/{id}")
    suspend fun deleteCourse(@Path("id") id:Int?): Response<Unit>

    /* *
    * conextion to student
    * */
    @GET("api/student")
    suspend fun getStudens():List<Student>

    @GET("api/student/{id}")
    suspend fun getStudentById(@Path("id") id:Int): Student

    @POST("api/student")
    suspend fun addStudent(@Body student:Student):Student

    @PUT("api/student/{id}")
    suspend fun updateStudent(@Path("id") id:Int, @Body student: Student):Student

    @DELETE("api/student/{id}")
    suspend fun deleteStudent(@Path("id") id:Int):Response<Unit>

}