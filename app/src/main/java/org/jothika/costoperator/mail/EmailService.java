package org.jothika.costoperator.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.jothika.costoperator.reconciler.CostOptimizationRule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    private final String fromAddress;

    public EmailService(
            JavaMailSender mailSender,
            TemplateEngine templateEngine,
            @Value("${spring.mail.username}") String fromAddress) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.fromAddress = fromAddress;
    }

    public void sendAlertOnPodThreshold(CostOptimizationRule rule, double actualUsage) {
        log.debug("Sending alert email for pod: {}", rule.getSpec().getPodName());
        String to = rule.getSpec().getNotificationEmail();
        String subject = "Cost Optimization Alert: Pod " + rule.getSpec().getPodName();
        String htmlBody = getEmailBody(rule, actualUsage);
        try {
            sendSimpleEmail(to, subject, htmlBody);
        } catch (MessagingException | MailException | UnsupportedEncodingException e) {
            log.error("Error sending email to {}: {}", to, e.getMessage());
        }
    }

    void sendSimpleEmail(String to, String subject, String htmlBody)
            throws MessagingException, UnsupportedEncodingException, MailException {
        log.debug("Sending email to {}", to);
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper =
                new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED);
        mimeMessageHelper.setFrom(fromAddress, "Cost Optimization Operator");
        mimeMessageHelper.setTo(to);
        mimeMessageHelper.setSubject(subject);
        mimeMessageHelper.setText(htmlBody, true);
        mailSender.send(mimeMessage);
        log.info("Email sent to {}", to);
    }

    private String getEmailBody(CostOptimizationRule rule, double actualUsage) {
        log.debug("Generating email body for rule: {}", rule.getMetadata().getName());
        Context context = new Context();
        context.setVariable("podName", rule.getSpec().getPodName());
        context.setVariable("namespace", rule.getMetadata().getNamespace());
        context.setVariable("resourceType", rule.getSpec().getResourceType());
        context.setVariable("configuredThresholdCondition", rule.getSpec().getThresholdCondition());
        context.setVariable("configuredThreshold", rule.getSpec().getThreshold());
        context.setVariable("actualUsage", actualUsage);
        context.setVariable(
                "alertTime",
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        return templateEngine.process("pod-alert", context);
    }
}
