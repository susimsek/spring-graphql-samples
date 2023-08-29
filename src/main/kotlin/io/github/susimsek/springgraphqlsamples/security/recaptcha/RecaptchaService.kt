package io.github.susimsek.springgraphqlsamples.security.recaptcha

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
            return true
        }
        if (recaptchaToken == null || !responseSanityCheck(recaptchaToken)) {
           return false
        }
        val response = recaptchaClient.verifyResponse(recaptchaProperties.secretKey, recaptchaToken)
            .awaitSingle()
        if (!response.success || response.score < recaptchaProperties.threshold) {
           return false
        }
        return true
    }

    private fun responseSanityCheck(response: String): Boolean {
        return StringUtils.hasLength(response) && responsePattern.matcher(response).matches()
    }
}
