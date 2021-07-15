package com.apiteste.exception

import com.networknt.schema.JsonSchemaException
import io.grpc.BindableService
import io.grpc.Status
import io.grpc.stub.StreamObserver
import io.micronaut.aop.InterceptorBean
import io.micronaut.aop.MethodInterceptor
import io.micronaut.aop.MethodInvocationContext
import javax.inject.Singleton

@Singleton
@InterceptorBean(ErrorHandler::class)
class ExceptionInterceptor : MethodInterceptor<BindableService, Any> {
    override fun intercept(context: MethodInvocationContext<BindableService, Any>?): Any? {
        return try {
            context!!.proceed()
        } catch (e: Exception){
            val statusError = when (e) {
                is IllegalArgumentException -> Status.INVALID_ARGUMENT.withDescription(e.message).asRuntimeException()
                is IllegalStateException -> Status.FAILED_PRECONDITION.withDescription(e.message).asRuntimeException()
                is JsonSchemaException -> Status.FAILED_PRECONDITION.withDescription("JSON ERROR: ${e.message}").withCause(e.cause).asRuntimeException()
                //is ConstraintViolationException -> handleConstraintValidationException(e)
                else -> Status.UNKNOWN.withDescription("unexpected error happened").asRuntimeException()
            }

            val responseObserver = context!!.parameterValues[1] as StreamObserver<*>
            responseObserver.onError(statusError)
            null
        }
    }
}