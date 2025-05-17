package com.moviles.courses.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.Relation

data class Course(
    val id: Int?,
    val name: String,
    val description: String,
    val imageUrl: String?,
    val schedule: String,
    val professor: String,
    val students:List<Student>?
)
