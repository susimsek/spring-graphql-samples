package io.github.susimsek.springgraphqlsamples.security.recaptcha

import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.util.StringUtils

class RecaptchaService(
    private val recaptchaClient: RecaptchaClient,
    private val recaptchaProperties: RecaptchaProperties
) {
    suspend fun validateToken(recaptchaToken: String?): Boolean {
        if (!recaptchaProperties.enabled) {
            return true
        }
        if (recaptchaToken == null || !StringUtils.hasLength(recaptchaToken)) {
           return false
        }
        val response = recaptchaClient.verifyResponse(recaptchaProperties.secretKey, recaptchaToken)
            .awaitSingle()
        if (!response.success || response.score < recaptchaProperties.threshold) {
           return false
        }
        return true
    }
}
