package com.moviles.courses.MappersEntity

import com.moviles.courses.RoomEntity.CourseEntity
import com.moviles.courses.common.Constants.API_BASE_URL
import com.moviles.courses.models.Course
import com.moviles.courses.models.Student


fun Course.toEntity() = CourseEntity(
    id = this.id ?: 0,
    name = this.name,
    description = this.description,
    imageUrl = API_BASE_URL + imageUrl?.trimStart('/'),
    schedule = this.schedule,
    professor = this.professor
)

fun CourseEntity.toModel(students: List<Student>): Course {
    return Course(
        id = id,
        name = name,
        description = description,
        imageUrl = API_BASE_URL + imageUrl?.trimStart('/'),
        schedule = schedule,
        professor = professor,
        students = students
    )
}