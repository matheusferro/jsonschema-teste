package com.apiteste.integration

import io.micronaut.http.HttpResponse
import io.micronaut.http.annotation.Body
import io.micronaut.http.annotation.Post
import io.micronaut.http.client.annotation.Client

@Client("http://localhost:8080/poc")
interface ClientHttp {

    @Post("/list-bucketsp")
    fun getListBucket(@Body bodyList: Teste): HttpResponse<ResponseApi>
}

data class Teste(val name: String)

data class ResponseApi(val listNameBuckets: Set<String>)