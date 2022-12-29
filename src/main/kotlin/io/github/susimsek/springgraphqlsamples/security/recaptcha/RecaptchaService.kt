package io.github.susimsek.springgraphqlsamples.security.recaptcha

import io.github.susimsek.springgraphqlsamples.exception.InvalidCaptchaException
import io.github.susimsek.springgraphqlsamples.exception.RECAPTCHA_INVALID_MSG_CODE
import kotlinx.coroutines.reactive.awaitSingle


class RecaptchaService(
    private val recaptchaClient: RecaptchaClient,
    private val recaptchaProperties: RecaptchaProperties
) {
    suspend fun validateToken(recaptchaToken: String): RecaptchaResponse {
        return recaptchaClient.verifyResponse(recaptchaProperties.secretKey, recaptchaToken)
            .map {
                when (it.success) {
                    false -> throw InvalidCaptchaException(RECAPTCHA_INVALID_MSG_CODE)
                    else -> it
                }
            }.awaitSingle()
    }
}
