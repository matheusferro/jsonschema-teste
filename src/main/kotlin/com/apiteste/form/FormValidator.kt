package com.apiteste.form

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.networknt.schema.*
import java.io.IOException
import javax.inject.Singleton

@Singleton
class FormValidator {

    private val mapper = ObjectMapper()

    private val nonValidationKeywords = setOf(
        "\$schema", "\$id", "title", "description", "default", "definitions",
        "\$comment", "\$defs", "\$anchor", "deprecated", "contentMediaType",
        "contentEncoding", "examples", "then", "else", "messages",
        "\$vocabulary", "\$dynamicAnchor",
    )

    private val jsonMetaSchema = JsonMetaSchema.Builder(JsonMetaSchema.getV201909().uri)
        .idKeyword("\$id")
        .addFormats(JsonMetaSchema.COMMON_BUILTIN_FORMATS)
        .addKeywords(ValidatorTypeCode.getNonFormatKeywords(SpecVersion.VersionFlag.V201909))
        .addKeywords(nonValidationKeywords.map{ NonValidationKeyword(it)})
        .build()

    private val validatorFactory = JsonSchemaFactory.Builder()
        .defaultMetaSchemaURI(jsonMetaSchema.uri)
        .addMetaSchema(jsonMetaSchema)
        .objectMapper(mapper)
        .build()

    private fun getJsonSchemaFromStringContent(schemaContent: String): JsonSchema = validatorFactory.getSchema(schemaContent)

    @Throws(IOException::class)
    private fun getJsonNodeFromStringContent(content: String): JsonNode = mapper.readTree(content)

    fun validate(schemaFileMeta: String, formJson: String): List<String>{
        val schema = getJsonSchemaFromStringContent(schemaFileMeta)
        val node = getJsonNodeFromStringContent(formJson)
        val  errors2 = schema.validateAndCollect(node)
        val  errors: Set<ValidationMessage> = schema.validate(node)


        return errors.map {
            val fieldSpl = it.message.split(".")

            println(fieldSpl)

            val msgSpl = it.message.split(": ")
            println("${fieldSpl[fieldSpl.size-2]}:  ${msgSpl[1]}  ")


            schema.getRefSchemaNode("#/messages/${it.path}.${it.type}")?.textValue() ?: it.message
        }
    }
}