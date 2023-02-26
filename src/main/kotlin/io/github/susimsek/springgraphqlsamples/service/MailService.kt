package io.github.susimsek.springgraphqlsamples.service

import io.github.susimsek.springgraphqlsamples.config.AppProperties
import io.github.susimsek.springgraphqlsamples.domain.User
import io.github.susimsek.springgraphqlsamples.service.dto.EmailSenderDTO
import jakarta.mail.MessagingException
import org.slf4j.LoggerFactory
import org.springframework.context.MessageSource
import org.springframework.mail.MailException
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import java.nio.charset.StandardCharsets

@Service
@Suppress("TooGenericExceptionCaught")
class MailService(
    private val javaMailSender: JavaMailSender,
    private val appProperties: AppProperties,
    private val messageSource: MessageSource
) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Async
    fun sendEmail(sender: EmailSenderDTO) {
        val mimeMessage = javaMailSender.createMimeMessage()
        val subject = messageSource.getMessage(sender.subject, null, sender.to.lang)
        try {
            MimeMessageHelper(
                mimeMessage,
                sender.isMultipart,
                StandardCharsets.UTF_8.name()
            ).apply {
                setTo(sender.to.email)
                if (appProperties.mail.from.enabled) {
                    setFrom(appProperties.mail.from.default)
                }
                setSubject(subject)
                setText(sender.content, sender.isHtml)
            }
            javaMailSender.send(mimeMessage)
            log.debug("Sent email to user '{}'", sender.to)
        } catch (ex: Exception) {
            when (ex) {
                is MailException,
                is MessagingException -> log.warn("Email could not be sent to user '{}'", sender.to, ex)
                else -> throw ex
            }
        }
    }

    @Async
    fun sendActivationEmail(user: User) {
        val confirmationUrl = "http://localhost:9091/regitrationConfirm?token=${user.activationToken}"
        log.debug("Sending activation email to '{}'", user.email)
        val content = messageSource.getMessage("email.activation.text", null, user.lang)
        sendEmail(
            EmailSenderDTO(
                to = user,
                subject = "email.activation.title",
                content = "$content\r\n$confirmationUrl",
                isMultipart = false,
                isHtml = false
            )
        )
    }
}
