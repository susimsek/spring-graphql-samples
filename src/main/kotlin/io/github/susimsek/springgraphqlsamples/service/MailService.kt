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
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine
import java.nio.charset.StandardCharsets

private const val USER = "user"
private const val TOKEN = "token"
private const val BASE_URL = "baseUrl"

@Service
@Suppress("TooGenericExceptionCaught")
class MailService(
    private val javaMailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
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
    fun sendConfirmationEmailFromTemplate(
        user: User,
        token: String,
        templateName: String,
        titleKey: String
    ) {
        val context = Context(user.lang).apply {
            setVariable(USER, user)
            setVariable(TOKEN, token)
            setVariable(BASE_URL, appProperties.mail.baseUrl)
        }
        val content = templateEngine.process(templateName, context)
        sendEmail(
            EmailSenderDTO(
                to = user,
                subject = titleKey,
                content = content,
                isMultipart = false,
                isHtml = true
            )
        )
    }

    @Async
    fun sendActivationEmail(user: User, activationToken: String) {
        log.debug("Sending activation email to '{}'", user.email)
        sendConfirmationEmailFromTemplate(
            user,
            activationToken,
            "mail/activationEmail",
            "email.activation.title"
        )
    }

    @Async
    fun sendPasswordResetMail(user: User, resetToken: String) {
        log.debug("Sending password reset email to '{}'", user.email)
        sendConfirmationEmailFromTemplate(user, resetToken, "mail/passwordResetEmail", "email.reset.title")
    }
}
