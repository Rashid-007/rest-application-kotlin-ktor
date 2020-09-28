package com.shop.userservice.web.api.v1

import com.fasterxml.jackson.databind.SerializationFeature
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.Route
import io.ktor.routing.post
import io.ktor.routing.get
import io.ktor.routing.route
import com.shop.userservice.config.simpleJwt
import com.shop.userservice.domain.*
import com.shop.userservice.web.api.v1.exception.InvalidCredentialException
import io.ktor.auth.authenticate
import io.ktor.features.StatusPages
import io.ktor.http.HttpStatusCode

fun Route.routeApiV1(path: String) = route(path) {
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(StatusPages) {
        // catch IllegalStateException and send back HTTP code 400
        exception<IllegalStateException> { cause ->
            call.respond(HttpStatusCode.BadRequest, mapOf("error" to (cause.message ?: "")))
        }

        exception<InvalidCredentialException> {
            call.respond(HttpStatusCode.Unauthorized, mapOf("Ok" to false, "error" to (it.message ?: "")))
        }
    }

    post("/login") {
        val userDto = call.receive<LoginRegister>()
        val user = users.getOrPut(userDto.username) {
            User(
                userDto.username,
                userDto.password
            )
        }

        if (user.password != userDto.password) throw InvalidCredentialException("Invalid credentials")
        call.respond(mapOf("token" to simpleJwt.sign(user.username)))
    }

    authenticate {
        post("/profile") {
            val profile = call.receive<Profile>()
            profiles.put(profile.id, profile)
            call.respond(profiles.get(profile.id)!!)
        }

        get("/profile/{profile_id}") {
            val profile = profiles.get(call.parameters["profile_id"]) ?: error("No Such Profile")
            call.respond(profile)
        }
    }
}