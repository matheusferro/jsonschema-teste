package com.apiteste.form

import com.apiteste.FormServiceGrpc
import com.apiteste.SendFormRequest
import com.apiteste.SendFormResponse
import com.apiteste.exception.ErrorHandler
import io.grpc.stub.StreamObserver
import java.security.InvalidParameterException
import javax.inject.Singleton

@Singleton
@ErrorHandler
class FormServer(val service: FormService): FormServiceGrpc.FormServiceImplBase() {

    override fun sendForm(request: SendFormRequest?, responseObserver: StreamObserver<SendFormResponse>?) {
        request ?: throw InvalidParameterException("Invalid request.")
        responseObserver ?: throw InvalidParameterException("Invalid request.")

        val response = service.submitForm(FormModel(request.name, request.jsonForm))

        val responseObj = SendFormResponse.newBuilder().apply {
            message = response
        }.build()
        responseObserver.onNext(responseObj)
        responseObserver.onCompleted()
    }
}