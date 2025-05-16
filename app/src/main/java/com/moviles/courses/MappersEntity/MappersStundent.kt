package com.moviles.courses.MappersEntity


import com.moviles.courses.RoomEntity.StudentEntity
import com.moviles.courses.models.Student

fun Student.toEntity() = StudentEntity(
    id = this.id ?: 0,
    name = this.name,
    email = this.email,
    phone = this.phone,
    courseId = this.courseId
)

fun StudentEntity.toModel() = Student(
    id = this.id,
    name = this.name,
    email = this.email,
    phone = this.phone,
    courseId = this.courseId
)
