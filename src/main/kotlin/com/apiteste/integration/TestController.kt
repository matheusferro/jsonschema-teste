package com.apiteste.integration

import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import java.lang.Exception

@Controller("/test")
class TestController(private val client: ClientHttp) {

    @Get
    fun test(){
        try {
            val response = client.getListBucket(Teste("name"))
            println(response.body())
        }catch (exp: Exception){
            println(exp)
        }

    }
}