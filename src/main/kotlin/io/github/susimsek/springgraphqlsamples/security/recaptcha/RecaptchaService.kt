package io.github.susimsek.springgraphqlsamples.security.recaptcha

import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.util.StringUtils
import java.util.regex.Pattern

class RecaptchaService(
    private val recaptchaClient: RecaptchaClient,
    private val recaptchaProperties: RecaptchaProperties
) {

    private val responsePattern = Pattern.compile("[A-Za-z0-9_-]+")
    suspend fun validateToken(recaptchaToken: String?): Boolean {
        if (!recaptchaProperties.enabled) {
            return false
        }
        if (recaptchaToken == null || !responseSanityCheck(recaptchaToken)) {
            throw InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)
        }
        val response = recaptchaClient.verifyResponse(recaptchaProperties.secretKey, recaptchaToken)
            .awaitSingle()
        if (!response.success || response.score < recaptchaProperties.threshold) {
            throw InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)
        }
        return true
    }

    private fun responseSanityCheck(response: String): Boolean {
        return StringUtils.hasLength(response) && responsePattern.matcher(response).matches()
    }
}
