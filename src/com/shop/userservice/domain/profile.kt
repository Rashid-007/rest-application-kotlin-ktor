package com.shop.userservice.domain

import java.util.*

data class Profile(
    val id: String = UUID.randomUUID().toString(),
    val firstname: String,
    val lastname: String
)

val profiles = Collections.synchronizedMap(
    listOf(Profile(firstname = "test", lastname = "test"))
        .associateBy { it.id }
        .toMutableMap()
)