package io.github.susimsek.springgraphqlsamples.security.recaptcha

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.annotation.JsonPropertyOrder
import java.util.*

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonPropertyOrder(
    "success",
    "score",
    "action",
    "challenge_ts",
    "hostname",
    "error-codes"
)
data class RecaptchaResponse(
    val success: Boolean,
    val score: Float = 0.0F,
    val action: String?,
    @get:JsonProperty("challenge_ts")
    val challengeTs: String?,
    val hostname: String?,
    @get:JsonProperty("error-codes")
    val errorCodes: MutableList<ErrorCode>?
) {
    @JsonIgnore
    fun hasClientError(): Boolean {
        val errors = errorCodes ?: return false
        return errors.any {
            when (it) {
            ErrorCode.INVALID_RESPONSE, ErrorCode.MISSING_RESPONSE -> true
            else -> false
        }
        }
    }

    enum class ErrorCode(private val code: String) {
        MISSING_SECRET("missing-input-secret"),
        INVALID_SECRET("invalid-input-secret"),
        MISSING_RESPONSE("missing-input-response"),
        INVALID_RESPONSE("invalid-input-response"),
        BAD_REQUEST("bad-request"),
        TIMEOUT_OR_DUPLICATE("timeout-or-duplicate");

        companion object {
            private val codes = ErrorCode.values().associateBy(ErrorCode::code)

            @JvmStatic @JsonCreator fun from(value: String) = codes[value]
        }
    }
}
