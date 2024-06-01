package io.bluetape4k.jackson.binary

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.dataformat.cbor.CBORFactory
import com.fasterxml.jackson.dataformat.cbor.CBORGenerator
import com.fasterxml.jackson.dataformat.cbor.databind.CBORMapper
import com.fasterxml.jackson.dataformat.ion.IonFactory
import com.fasterxml.jackson.dataformat.ion.IonGenerator
import com.fasterxml.jackson.dataformat.ion.IonObjectMapper
import com.fasterxml.jackson.dataformat.smile.SmileFactory
import com.fasterxml.jackson.dataformat.smile.SmileGenerator
import com.fasterxml.jackson.dataformat.smile.databind.SmileMapper
import io.bluetape4k.logging.KLogging

object JacksonBinary: KLogging() {

    object CBOR {

        val defaultMapper: CBORMapper by lazy {
            CBORMapper.builder()
                .findAndAddModules()
                .enable(
                    CBORGenerator.Feature.WRITE_TYPE_HEADER,
                    CBORGenerator.Feature.STRINGREF,
                )
                .disable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS
                )
                .enable(
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                    DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                    DeserializationFeature.READ_ENUMS_USING_TO_STRING,
                )
                .disable(
                    DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                )
                .build()
        }

        val defaultFactory: CBORFactory by lazy { defaultMapper.factory }

        val defaultJsonSerializer: CborJsonSerializer by lazy { CborJsonSerializer(defaultMapper) }
    }

    object ION {

        val defaultMapper: IonObjectMapper by lazy {
            IonObjectMapper.builder()
                .findAndAddModules()
                .enable(
                    IonGenerator.Feature.USE_NATIVE_TYPE_ID,
                )
                .disable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS
                )
                .enable(
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                    DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                    DeserializationFeature.READ_ENUMS_USING_TO_STRING,
                )
                .disable(
                    DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                )
                .build()
        }

        val defaultFactory: IonFactory by lazy { defaultMapper.factory }

        val defaultJsonSerializer: IonJsonSerializer by lazy { IonJsonSerializer(defaultMapper) }
    }

    object Smile {

        val defaultMapper: SmileMapper by lazy {
            SmileMapper.builder()
                .findAndAddModules()
                .enable(
                    SmileGenerator.Feature.WRITE_HEADER,
                    SmileGenerator.Feature.WRITE_END_MARKER,
                )
                .disable(
                    SerializationFeature.FAIL_ON_EMPTY_BEANS
                )
                .enable(
                    DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT,
                    DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY,
                    DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT,
                    DeserializationFeature.READ_UNKNOWN_ENUM_VALUES_AS_NULL,
                    DeserializationFeature.READ_ENUMS_USING_TO_STRING,
                )
                .disable(
                    DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES,
                    DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_CREATOR_PROPERTIES,
                    DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES,
                )
                .build()
        }

        val defaultFactory: SmileFactory by lazy { defaultMapper.factory }

        val defaultJsonSerializer: SmileJsonSerializer by lazy { SmileJsonSerializer(defaultMapper) }
    }
}
