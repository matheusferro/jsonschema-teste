package com.apiteste.form

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import io.micronaut.core.annotation.Introspected
import io.micronaut.validation.Validated
import javax.inject.Singleton
import javax.validation.Valid
import javax.validation.constraints.NotBlank

@Singleton
@Validated
class FormService(val formValidator: FormValidator, val mapper: ObjectMapper){

    private val STRING: String = "string"
    private val OBJECT: String = "object"
    private val ARRAY: String = "array"
    private val BOOLEAN: String = "boolean"

    fun submitForm(@Valid formsModel: FormModel): String{
        //return formValidator.validate(formsModel.jsonSchema, formsModel.jsonForm).toString()
        //val resp = mapper.createParser(formsModel.jsonSchema)
        val resp2 = mapper.readTree(formsModel.jsonSchema)
        iteradeJSONNode(resp2)
        /**
         * iteradeJSONNode esta printando:
         *
        title:""
        text_field:""
        sessions:[
            title:""
            nome_completo:{
                title:""
                text_field:""
                mandatory:""
            endereco:{
                title:""
                text_field:""
                mandatory:""
         */


        return ""
    }

    fun iteradeJSONNode(resp2: JsonNode){

        /*
        temos que cuidar de propriedades perdidas tipo:
        "nome_completo": {
              "type": "object",
              "component_type": "textArea",
              "properties": {

              'component_type' -> aqui esta avulso e não estamos tratando ainda
         */
        resp2.fields().forEach { jsonNode ->
            if(jsonNode.key.equals("properties")){
                jsonNode.value.fields().forEach { props ->
                    val typ = props.value.get("type").asText()
                    if(typ.equals(STRING) || typ.equals(BOOLEAN)){
                        println("           ${props.key}:\"\"    ")
                    }else if(typ.equals(OBJECT)){
                        println("       ${props.key}:{")
                        iteradeJSONNode(props.value)
                    }else if(typ.equals(ARRAY)){
                        println("   ${props.key}:[")
                        props.value.get("items").forEach { elementOnArrNode ->
                            iteradeJSONNode(elementOnArrNode)
                        }
                    }
                }
            }
        }
    }

}

@Introspected
data class FormModel(
    @field:NotBlank(message = "Schema not valid")
    val jsonSchema: String,
    @field:NotBlank(message = "Invalid form input.")
    val jsonForm: String
)
