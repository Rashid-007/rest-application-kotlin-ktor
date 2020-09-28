package com.shop.userservice

import com.fasterxml.jackson.databind.SerializationFeature
import com.shop.userservice.config.Health
import com.shop.userservice.config.HealthStatus
import io.ktor.application.*
import io.ktor.auth.Authentication
import io.ktor.auth.UserIdPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.apache.*
import io.ktor.features.ContentNegotiation
import io.ktor.jackson.jackson
import com.shop.userservice.config.simpleJwt
import com.shop.userservice.web.api.v1.routeApiV1

fun main(args: Array<String>): Unit = io.ktor.server.netty.EngineMain.main(args)

@Suppress("unused") // Referenced in application.conf
@kotlin.jvm.JvmOverloads
fun Application.module(testing: Boolean = false) {
    val client = HttpClient(Apache) {
    }
    install(ContentNegotiation) {
        jackson {
            enable(SerializationFeature.INDENT_OUTPUT)
        }
    }

    install(Authentication) {
        jwt {
            verifier(simpleJwt.verifier)
            validate {
                UserIdPrincipal(it.payload.getClaim("name").asString())
            }
        }
    }

    routing {
        get("/") {
            call.respondText("Welcome to shop!", contentType = ContentType.Text.Plain)
        }

        get("/health") {
            call.respond(Health(HealthStatus.HEALTHY))
        }
        // route to main api
        routeApiV1("api/v1")
    }
}